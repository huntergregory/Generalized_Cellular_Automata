package XML;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 *
 * Schema validation inspired by Wayan Saryada's article.
 * @see <a href="https://kodejava.org/how-do-i-validate-xml-against-xsd-in-java/">Wayan Saryada's</a> article
 * @author Hunter Gregory
 */

/*
Element order:
    - size
    - numStates
    - random OR configured
        - for random:
            - sequence of unspecified tags with int
        - for configured:
            - sequence of unspecified tags each with sequence of position tags each with row and col tags
    - parameters (might have no children if empty)
 */

//FIX CHECK TO SEE IF POSITIONS ARE IN BOUNDS AND IF COMPOSITIONS ARE BETWEEN 0-1 AND ADD UP
public class XMLParser {
    private final SchemaFactory SCHEMAFACTORY;
    private final DocumentBuilder DOCUMENT_BUILDER;
    public static final String RANDOM_TAG = "random";
    public static final String CONFIGURED_TAG = "configured";
    public static final int SIZE_INDEX = 0;
    public static final int NUM_STATES_INDEX = 1;
    public static final int STATES_INDEX = 2;
    public static final int PARAMS_INDEX = 3;

    File myXMLFile;
    private int myElementsIndex;
    private CA_TYPE myRootType;
    private Element myRoot;
    private int mySize;
    private int myNumStates;
    private boolean myIsRandom;
    private ArrayList<Double> myRandomComposition;             //only used if random
    private ArrayList<Integer[]> myStateConfiguration;   //only used if configured
    private ArrayList<Double> myParameters;
    private LinkedHashMap<String, Double[]> mySliderMap; //ordered map so that states and params are displayed in same order as xml file

    public XMLParser() {
        SCHEMAFACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        DOCUMENT_BUILDER = getDocumentBuilder();
        resetInstanceVars();
    }

    /**
     * Stores necessary data from the given xml file
     * @param xmlFile
     * @throws XMLException if xmlFile doesn't match a schema associated with a XML.CA_TYPE
     */
    public void parseFile(File xmlFile) throws XMLException {
        myXMLFile = xmlFile;
        resetInstanceVars();
        assignRootType();
        assignRoot();
        NodeList elements = myRoot.getElementsByTagName("*"); //matches all tags
        mySize = getIntFromNodeList(elements, myElementsIndex);
        myElementsIndex++;
        myNumStates = getIntFromNodeList(elements, myElementsIndex);
        myElementsIndex++;
        myIsRandom = RANDOM_TAG.equals(getTagNameFromNodeList(elements, myElementsIndex));
        if (myIsRandom)
            assignCompAndUpdateSliders(elements);
        else
            assignConfiguration(elements);

        myElementsIndex++;
        assignParamsAndUpdateSliders(elements);
    }

    private void assignRootType() throws XMLException {
        for (CA_TYPE type : CA_TYPE.values()) {
            if (fileIsType(type))
                myRootType = type;
        }
        if (myRootType == null)
            throw new XMLException("File does not match any automaton's schema");
    }

    private void assignRoot() throws XMLException {
        try {
            var xmlDoc = DOCUMENT_BUILDER.parse(myXMLFile);
            myRoot = xmlDoc.getDocumentElement();
        }
        catch (SAXException | IOException e) {
            throw new XMLException(e);
        }
    }

    private void assignCompAndUpdateSliders(NodeList elements) {
        Element randomTag = (Element) elements.item(myElementsIndex);
        NodeList compositions = randomTag.getElementsByTagName("*");
        int k=0; // foreach not allowed for NodeList
        while (k < compositions.getLength()) {
            myElementsIndex++;
            myRandomComposition.add(getDoubleFromNodeList(compositions, k));
            Double[] extrema = {0.0, 1.0};
            mySliderMap.put(getTagNameFromNodeList(compositions,k), extrema);
            k++;
        }
    }

    private void assignConfiguration(NodeList elements) {
        Element configuredTag = (Element) elements.item(STATES_INDEX);
        NodeList config = configuredTag.getElementsByTagName("*");
        int k=0;
        int numState=0;
        while(k<config.getLength()) {
            myElementsIndex++;
            int j=0;
            if (!itemIsState(config.item(k))) {
                k++;
                continue;
            }
            Element state = (Element) config.item(k);
            NodeList positions = state.getElementsByTagName("position");
            while(j<positions.getLength()) {
                myStateConfiguration.add(getCoords(positions, j, numState));
                j++;
            }
            k++;
            numState++;
        }
    }

    // Throws exception if coordinates out of bounds
    private Integer[] getCoords(NodeList positions, int positionIndex, int numState) throws XMLException {
        Element position = (Element) positions.item(positionIndex);
        NodeList rowColState = position.getElementsByTagName("*");
        int row = getIntFromNodeList(rowColState, 0);
        int col = getIntFromNodeList(rowColState, 1);
        validateInBounds(row, col, numState);
        return new Integer[]{row, col, numState};
    }

    private void validateInBounds(int row, int col, int numState) throws XMLException {
        if ((!(row == -1 && col==-1) && (row<0 || col<0)) || row>=mySize || col>=mySize)
            throw new XMLException("The location (%d, %d) for state %d is out of bounds for grid size %d", row, col, numState, mySize);
    }

    private boolean itemIsState(Node item) {
        String itemName = ((Element) item).getTagName();
        String[] nonStateNames = {"position", "row", "col"};
        for (String badName : nonStateNames) {
            if (itemName.equals(badName))
                return false;
        }
        return true;
    }

    private void assignParamsAndUpdateSliders(NodeList elements) {
        NodeList parameters = ((Element) elements.item(myElementsIndex)).getElementsByTagName("*");
        int k=0;
        //no need to increment myElementsIndex anymore
        while (k<parameters.getLength()) {
            myParameters.add(getDoubleFromNodeList(parameters, k));
            String paramName = getTagNameFromNodeList(parameters, k);
            String min = getAttributeFromNodeList(parameters, k, "min");
            String max = getAttributeFromNodeList(parameters, k, "max");
            Double[] extrema = { Double.parseDouble(min), Double.parseDouble(max) };
            mySliderMap.put(paramName, extrema);
            k++;
        }
    }

    private String getAttributeFromNodeList(NodeList list, int index, String name) {
        return ((Element) list.item(index)).getAttribute(name);
    }

    private boolean fileIsType(CA_TYPE type) {
        try {
            Schema schema = SCHEMAFACTORY.newSchema(type.getSchemaFile());

            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(myXMLFile));
            return true;
        } catch (SAXException | IOException e) {
            return false;
        }
    }


    private String getTagNameFromNodeList(NodeList list, int index) {
        var element = (Element) list.item(index);
        return element.getTagName();
    }

    private int getIntFromNodeList(NodeList list, int index) {
        return Integer.parseInt(list.item(index).getTextContent());
    }

    private double getDoubleFromNodeList(NodeList list, int index) {
        return Double.parseDouble(list.item(index).getTextContent());
    }

    private DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new XMLException(e);
        }
    }

    private void resetInstanceVars() {
        myRandomComposition = new ArrayList<>();
        myStateConfiguration = new ArrayList<>();
        myParameters = new ArrayList<>();
        mySliderMap = new LinkedHashMap<>();
        myRoot = null;
        myRootType = null;
        mySize = 0;
        myNumStates = 0;
        myIsRandom = false;
        myElementsIndex = 0;
    }

    /**
     * @return XML.CA_TYPE of file parsed
     */
    public CA_TYPE getCAType() { return myRootType; }

    /**
     * @return an ordered map with Slider names as keys and Double arrays containing min and max slider values
     */
    public LinkedHashMap<String, Double[]> getSliderNamesAndValues() { return mySliderMap; }

    /**
     * Get the configured positions for all states.
     * @return list of integer arrays in the form (row, col, state). The last state's row and col will be -1 to indicate
     *          that it's composition should be inferred.
     */
    public ArrayList<Integer[]> getConfiguration() { return myStateConfiguration; }

    /**
     * Get the percent composition for each state.
     * @return an array of Doubles. The last state's composition will be -1 to indicate that it's composition
     *         should be inferred.
     */
    public Double[] getRandomComposition() { return myRandomComposition.toArray(new Double[0]); }

    /**
     * @return an array of Doubles representing special parameters for the CA
     */
    public Double[] getParameters() { return myParameters.toArray(new Double[0]); }

    /**
     * @return true if the xml file is for a random-position CA
     */
    public boolean getIsRandom() { return myIsRandom; }

    /**
     * @return size of GridCell.Grid for the xml file
     */
    public int getGridSize() { return mySize; }

    /**
     * @return number of states for the xmlFile
     */
    public int getNumStates() { return myNumStates; }
}
