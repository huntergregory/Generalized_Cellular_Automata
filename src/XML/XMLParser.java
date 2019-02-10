package XML;

import GridCell.CELL_SHAPE;
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
 * Parses automaton XML files associated with any schema with a corresponding CA_TYPE enumeration.
 * Gathers information from one of these files, and has the ability to share the information.
 * Checks for errors in the xml file such as:
 *      - xml file doesn't conform to an automaton schema
 *      - values given to elements are out of bounds
 * Replaces error values with defaults. If an invalid xml file is given, a warning will displayed and the parser will
 * continue with parsing a default fire-random configuration file.
 * Schema validation inspired by Wayan Saryada's article.
 * @see <a href="https://kodejava.org/how-do-i-validate-xml-against-xsd-in-java/">Wayan Saryada's</a> article
 * @author Hunter Gregory
 */
public class XMLParser {
    private final SchemaFactory SCHEMAFACTORY;
    private final DocumentBuilder DOCUMENT_BUILDER;
    public static final String RANDOM_TAG = "random";
    public static final File DEFAULT_XML_FILE = new File("data/automata/fire-random-comp.xml");

    File myXMLFile;
    private int myElementsIndex; //increment after passing an element in order to know where the parameters start
    private CA_TYPE myRootType;
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
     * Stores necessary data from the given xml file or a default file if the given file fails.
     * Additionally, overrides erroneous xml values.
     * @param xmlFile
     * @throws XMLException
     */
    public void parseFile(File xmlFile) {
        myXMLFile = xmlFile;
        resetInstanceVars();
        assignRootType();
        NodeList elements = getRoot().getElementsByTagName("*"); //matches all tags
        assignSizeAndSizeSlider(elements);
        myNumStates = getIntFromNodeList(elements, myElementsIndex);
        myElementsIndex++;
        myIsRandom = RANDOM_TAG.equals(getTagNameFromNodeList(elements, myElementsIndex));
        if (myIsRandom)
            assignCompAndUpdateSliders(elements); //FIX throws XML exception
        else
            assignConfiguration(elements);

        myElementsIndex++;
        assignParamsAndUpdateSliders(elements);
    }

    private void assignRootType() {
        for (CA_TYPE type : CA_TYPE.values()) {
            if (fileIsType(type))
                myRootType = type;
        }
        if (myRootType == null) {
            String message = "Warning: file does not match any automaton schema. Continuing with " +
                                "default file: " + DEFAULT_XML_FILE.getAbsolutePath();
            System.out.println(message);
            myXMLFile = DEFAULT_XML_FILE;
            assignRootType(); //should never have an infinite loop unless the default xml doesn't follow a schema
        }
    }

    //should never throw the exception because the xml doc will conform to an xsd after assignRootType
    private Element getRoot() throws XMLException {
        try {
            var xmlDoc = DOCUMENT_BUILDER.parse(myXMLFile);
            return xmlDoc.getDocumentElement();
        }
        catch (SAXException | IOException e) {
            throw new XMLException(e);
        }
    }

    private void assignSizeAndSizeSlider(NodeList elements) {
        addSliderFromNodeList(elements, myElementsIndex);
        mySize = (int) outOfBoundsRevision(elements, myElementsIndex);
        myElementsIndex++;
    }

    //extrema in form [min, max]
    private Double[] getExtrema(NodeList list, int index) {
        String min = getAttributeFromNodeList(list, index, "min");
        String max = getAttributeFromNodeList(list, index, "max");
        return new Double[]{ Double.parseDouble(min), Double.parseDouble(max) };
    }

    private void assignCompAndUpdateSliders(NodeList elements) throws XMLException {
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
        validateComp();
    }

    private void assignConfiguration(NodeList elements) {
        Element configuredTag = (Element) elements.item(myElementsIndex);
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

    //no need to increment myElementsIndex anymore
    private void assignParamsAndUpdateSliders(NodeList elements) {
        NodeList parameters = ((Element) elements.item(myElementsIndex)).getElementsByTagName("*");
        int k=0;
        //make sure only one comp is -1
        int numNegatives = 0;
        while (k<parameters.getLength()) {
            addSliderFromNodeList(parameters, k);
            //FIX
            myParameters.add(getDoubleFromNodeList(parameters, k));
            k++;
        }
    }

    //returns the error-checked and possibly revised value to add to mySize or myParameters
    private void addSliderFromNodeList(NodeList parameters, int index) {
        mySliderMap.put(getTagNameFromNodeList(parameters, index), getExtrema(parameters, index));
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



    private boolean itemIsState(Node item) {
        String itemName = ((Element) item).getTagName();
        String[] nonStateNames = {"position", "row", "col"};
        for (String badName : nonStateNames) {
            if (itemName.equals(badName))
                return false;
        }
        return true;
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

    private String getAttributeFromNodeList(NodeList list, int index, String name) {
        return ((Element) list.item(index)).getAttribute(name);
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
        myRootType = null;
        mySize = 0;
        myNumStates = 0;
        myIsRandom = false;
        myElementsIndex = 0;
    }

    /*
    ------------- Error-handling helper functions  ----------
    */

    private void validateComp() throws XMLException {
        double totalComp = 0;
        int negativeCount = 0;
        for (int k=0; k<myRandomComposition.size(); k++) {
            if (myRandomComposition.get(k) == -1 && negativeCount==0) {
                negativeCount ++;
                continue;
            }
            totalComp += myRandomComposition.get(k);
            if (totalComp > 1.0 || negativeCount==2)
                throw new XMLException("Error in composition values");
        }

        if (totalComp != 1 && negativeCount == 0)
            throw new XMLException("Error in composition values");
    }

    private void validateInBounds(int row, int col, int numState) throws XMLException {
        if ((!(row == -1 && col==-1) && (row<0 || col<0)) || row>=mySize || col>=mySize)
            throw new XMLException("The location (%d, %d) for state %d is out of bounds for grid size %d", row, col, numState, mySize);
    }

    private double outOfBoundsRevision(NodeList list, int index) {
        double value = getDoubleFromNodeList(list, index);
        Double[] extrema = getExtrema(list, index);
        double min = extrema[0]; double max = extrema[1];
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    /*
    -------------------     GETTERS  ------------------------
    */

    /**
     * @return CELL_SHAPE of file parsed
     */
    public CELL_SHAPE getCellShape() { return CELL_SHAPE.SQUARE; } //FIX

    /**
     * @return integer array of specified neighbors for the cell shape
     */
    public int[] getNeighborConfig() { return new int[]{-1}; } //FIX

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
