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
import java.util.*;
import java.util.regex.PatternSyntaxException;

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
    //private static final String RANDOM_TAG = "random";
    private static final File DEFAULT_XML_FILE = new File("data/automata/fire/fire-random-comp.xml");
    private static final CELL_SHAPE DEFAULT_SHAPE = CELL_SHAPE.SQUARE;

    private static final String SIZE_TAG = "size";
    private static final String STATE_NAMES_TAG = "state-names";
    private static final String EDGES_TAG = "edges";
    private static final String SHAPE_TAG = "shape";
    private static final String NEIGHBORS_TAG = "neighbors";
    private static final String RANDOM_COMP_TAG = "random-composition";
    private static final String RANDOM_COMP_TYPE = "random composition"; //aligned with SimulatorMain
    private static final String RANDOM_NUM_TAG = "random-numbers";
    private static final String RANDOM_NUM_TYPE = "random numbers";     //aligned with SimulatorMain
    private static final String LOCATIONS_TAG = "configured";
    private static final String LOCATIONS_TYPE = "locations";         //aligned with SimulatorMain
    private static final String PARAMETERS_TAG = "parameters";

    private File myXMLFile;
    private Element myRoot;
    private CA_TYPE myRootType;
    private int mySize;
    private String[] myStates;
    private String myEdgeType;
    private CELL_SHAPE myCellShape;
    private Integer[] myNeighborConfig;
    private String myConfigType;
    private ArrayList<Double> myRandomComposition;       //only used if myConfigType is RANDOM_NUM_TYPE
    private ArrayList<Double> myRandomNumbers;          //only used if myConfigType is RANDOM_COMP_TYPE
    private ArrayList<Integer[]> myStateLocations;       //only used if myConfigType is LOCATIONS_TYPE
    private ArrayList<Double> myParameters;
    private LinkedHashMap<String, Double[]> mySliderMap; //ordered map so that states and params are displayed in same order as xml file

    public XMLParser() {
        SCHEMAFACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        DOCUMENT_BUILDER = getDocumentBuilder();
    }

    /**
     * Stores necessary data from the given xml file or a default file if the given file fails.
     * Additionally, overrides erroneous xml values.
     * @param xmlFile
     * @throws XMLException
     */
    public void parseFile(File xmlFile) {
        myXMLFile = xmlFile;
        resetListsAndMaps();
        assignRootType();
        assignRoot();
        assignSizeAndSizeSlider();
        assignStateNames();
        assignEdges();
        assignCellShape();
        assignNeighborConfig();
        assignConfigTypeAndUpdateSliders();
        assignParamsAndUpdateSliders();
    }

    private Element getElementNamed(String name) {
        return (Element) myRoot.getElementsByTagName(name).item(0);
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
    private void assignRoot() throws XMLException {
        try {
            var xmlDoc = DOCUMENT_BUILDER.parse(myXMLFile);
            myRoot = xmlDoc.getDocumentElement();
        }
        catch (SAXException | IOException e) {
            throw new XMLException(e);
        }
    }

    private void assignSizeAndSizeSlider() {
        var element = getElementNamed(SIZE_TAG);
        addSlider(element);
        mySize = (int) outOfRangeRevision(element);
    }

    private void assignStateNames() {
        var element = getElementNamed(STATE_NAMES_TAG);
        myStates = element.getTextContent().split(", ");
    }

    private void assignEdges() {
        var element = getElementNamed(EDGES_TAG);
        myEdgeType = element.getTextContent();
        //the xml file conforms to the general CA schema, which has edge enumerations
    }

    private void assignCellShape() {
        var element = getElementNamed(SHAPE_TAG);
        for (CELL_SHAPE shape : CELL_SHAPE.values()) {
            if (shape.toString().equals(element.getTextContent()))
                myCellShape = shape;
        }
        if (myCellShape == null)
            myCellShape = DEFAULT_SHAPE;
    }

    private void assignNeighborConfig() {
        int maxNeighbors = myCellShape.getMaxNumNeighbors();
        var element = getElementNamed(NEIGHBORS_TAG);
        try {
            String text =element.getTextContent();
            if (text.equals("-1"))
                myNeighborConfig = new Integer[]{-1};
            else {
                String[] stringNumbers = text.split(", ");
                Integer[] numbers = new Integer[stringNumbers.length];
                for (int k = 0; k < numbers.length; k++) {
                    numbers[k] = Integer.parseInt(stringNumbers[k]);
                    if (numbers[k] < 0 || numbers[k] >= maxNeighbors)
                        throw new XMLException("neighbor config out of bounds");
                }
                myNeighborConfig = numbers;
            }
        }
        catch (PatternSyntaxException | NumberFormatException | XMLException e) {
            System.out.println("Warning: " + e.getMessage() + "\nSetting neighbor config to max possible.\n");
            myNeighborConfig = new Integer[]{-1}; // max possible neighbors
        }
    }

    private void assignConfigTypeAndUpdateSliders() {
        if (elementInDocument(RANDOM_COMP_TAG)) {
            myConfigType = RANDOM_COMP_TYPE;
            assignCompAndUpdateSliders(); //FIX throws XML exception
        }
        else if (elementInDocument(RANDOM_NUM_TAG)) {
            myConfigType = RANDOM_NUM_TYPE;
            assignRandomNumsAndUpdateSliders();
        }
        else {   //must be locations type at this point because the xml file conforms to general CA schema
            myConfigType = LOCATIONS_TYPE;
            assignLocations();
        }
    }

    private boolean elementInDocument(String name) {
        return myRoot.getElementsByTagName(name).getLength() > 0;
    }

    //extrema in form [min, max]
    private Double[] getExtrema(Element element) {
        String min = element.getAttribute("min");
        String max = element.getAttribute("max");
        return new Double[]{ Double.parseDouble(min), Double.parseDouble(max) };
    }

    //makes sure compositions sum to 1.0 or exactly one of the numbers is -1
    private void assignCompAndUpdateSliders() throws XMLException {
        var element = getElementNamed(RANDOM_COMP_TAG);
        NodeList compositions = element.getElementsByTagName("*");
        double totalComp = 0;
        boolean negativeOneIncluded = false;
        int k=0;
        while (k < compositions.getLength()) {
            var comp = (Element) compositions.item(k);
            addSlider(comp);

            Double value = getDouble(comp);
            if (value == -1 && !negativeOneIncluded) {
                negativeOneIncluded = true;
            }
            else
                value = outOfRangeRevision(comp);
            totalComp += value;
            if (k == compositions.getLength() - 1) {
                if (totalComp < 1 && !negativeOneIncluded)
                    value = -1.0;
                else if (totalComp > 1)
                    value = 1 - (totalComp - value);
            }
            if (totalComp > 1) {
                totalComp -= value;
                value = 0.0;
            }
            myRandomComposition.add(value);
            k++;
        }
    }

    //makes sure numbers sum to mySize * mySize or exactly one of the numbers is -1
    private void assignRandomNumsAndUpdateSliders() {
        var element = getElementNamed(RANDOM_NUM_TAG);
        NodeList states = element.getElementsByTagName("*");
        int maxNum = mySize * mySize;
        int totalNum = 0;
        boolean negativeOneIncluded = false;
        int k=0;
        while (k<states.getLength()) {
            var state = (Element) states.item(k);
            addSlider(state);

            Double value = getDouble(state);
            if (value == -1 && !negativeOneIncluded) {
                negativeOneIncluded = true;
            }
            else if (value < 0)
                value = 0.0;
            totalNum += value;
            if (k == states.getLength() - 1) {
                if (totalNum < maxNum && !negativeOneIncluded)
                    value = -1.0;
                else if (totalNum > maxNum)
                    value = maxNum - (totalNum - value);
            }
            if (totalNum > maxNum) {
                totalNum -= value;
                value = 0.0;
            }
            myRandomNumbers.add(value);
            k++;
        }
    }

    private void assignLocations() {
        var locations = getElementNamed(LOCATIONS_TAG);
        NodeList config = locations.getElementsByTagName("*");
        int k=0;
        int numState=0;
        while(k<config.getLength()) {
            var element = (Element) config.item(k);
            if (!isState(element)) {
                k++;
                continue;
            }
            if (isState(element)) {
                addStatePositions(element, numState);
            }
            k++;
            numState++;
        }
    }

    //makes sure to only include positions that are in bounds and haven't been included before
    private void addStatePositions(Element element, int numState) {
        NodeList positions = element.getElementsByTagName("position");
        int j = 0;
        while (j < positions.getLength()) {
            var position = (Element) positions.item(j);
            NodeList rowColState = position.getElementsByTagName("*");
            int row = getInt((Element) rowColState.item(0));
            int col = getInt((Element) rowColState.item(1));
            if (inBounds(row, col) && locationIsUnclaimed(row,col))
                myStateLocations.add(new Integer[]{row, col, numState});
            j++;
        }
    }

    private boolean locationIsUnclaimed(int row, int col) {
        for (Integer[] coords : myStateLocations) {
            if (coords[0] == row && coords[1] == col)
                return false;
        }
        return true;
    }

    private void assignParamsAndUpdateSliders() {
        Element parameters = getElementNamed(PARAMETERS_TAG);
        NodeList parametersList = parameters.getElementsByTagName("*");
        int k=0;
        while (k<parametersList.getLength()) {
            var param = (Element) parametersList.item(k);
            addSlider(param);
            myParameters.add(outOfRangeRevision(param));
            k++;
        }
    }

    private double getDouble(Element element) {
        return Double.parseDouble(element.getTextContent());
    }

    private void addSlider(Element element) {
        mySliderMap.put(element.getTagName(), getExtrema(element));
    }

    private boolean isState(Node item) {
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

    private int getInt(Element element) {
        return Integer.parseInt(element.getTextContent());
    }

    private boolean inBounds(int row, int col) {
        return row<mySize && col<mySize &&
                ((row>=0 && col>=0) || (row == -1 && col ==-1));
    }

    private double outOfRangeRevision(Element element) {
        double value = getDouble(element);
        Double[] extrema = getExtrema(element);
        double min = extrema[0]; double max = extrema[1];
        if (value < min || value > max)
            return min;
        return value;
    }

    private void resetListsAndMaps() {
        myRandomComposition = new ArrayList<>();
        myRandomNumbers = new ArrayList<>();
        myStateLocations = new ArrayList<>();
        myParameters = new ArrayList<>();
        mySliderMap = new LinkedHashMap<>();
    }

    private DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new XMLException(e);
        }
    }

    /*
    -------------------     GETTERS  ------------------------
    */

    /**
     * @return CELL_SHAPE of file parsed
     */
    public CELL_SHAPE getCellShape() { return myCellShape; }

    /**
     * @return integer array of specified neighbors for the cell shape
     */
    public Integer[] getNeighborConfig() { return myNeighborConfig; }

    /**
     * @return XML.CA_TYPE of file parsed
     */
    public CA_TYPE getCAType() { return myRootType; }

    /**
     * @return String representation of edge type. Either "normal", "toroidal", or "infinite"
     */
    public String getEdgeType() { return myEdgeType; }

    /**
     * @return an ordered map with Slider names as keys and Double arrays containing min and max slider values
     */
    public LinkedHashMap<String, Double[]> getSliderNamesAndValues() { return mySliderMap; }

    /**
     * Get the configured positions for all states. Only should be called if myConfigType is "locations"
     * @return list of integer arrays in the form (row, col, state). The last state's row and col will be -1 to indicate
     *          that it's composition should be inferred.
     */
    public ArrayList<Integer[]> getLocations() { return myStateLocations; }

    /**
     * Get the percent composition for each state. Only should be called if myConfigType is "random composition"
     * @return an array of Doubles. The last state's composition will be -1 to indicate that it's composition
     *         should be inferred.
     */
    public Double[] getRandomComposition() { return myRandomComposition.toArray(new Double[0]); }

    /**
     * Get the number of cells for each cell to occupy. Only should be called if myConfigType is "random numbers"
     * @return
     */
    public Double[] getRandomNumbers() { return myRandomNumbers.toArray(new Double[0]); }

    /**
     * @return an array of Doubles representing special parameters for the CA
     */
    public Double[] getParameters() { return myParameters.toArray(new Double[0]); }

    /**
     * String representation of configuration type. Will either be "random composition", "random numbers", or "locations"
     * @return type
     */
    public String getConfigType() { return myConfigType; }

    /**
     * @return size of GridCell.Grid for the xml file
     */
    public int getGridSize() { return mySize; }

    /**
     * @return state names for the xmlFile
     */
    public String[] getStates() { return myStates; }
}