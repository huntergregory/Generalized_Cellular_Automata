package XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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

public class XMLParser {
    private final SchemaFactory SCHEMAFACTORY;
    private final DocumentBuilder DOCUMENT_BUILDER;

    File myXMLFile;
    private CA_TYPE myRootType;
    private Element myRoot;
    private int mySize;
    private int myNumStates;
    private boolean myIsRandom;
    private ArrayList<Double> myRandomMakeup;             //only used if random
    private ArrayList<Integer[]> myStateConfiguration;   //only used if configured
    private ArrayList<Integer> myParameters;
    private HashMap<String, Integer[]> mySliderMap;

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
        System.out.println(myRoot);
        assignSize();
        assignNumStates();
        assignMyIsRandom();
        if (myIsRandom)
            assignRandomMakeup();
        else
            assignConfiguration();

        assignParameters();
    }

    private void assignRootType() throws XMLException {
        for (CA_TYPE type : CA_TYPE.values()) {
            if (fileIsType(type))
                myRootType = type;
        }
        if (myRootType == null)
            throw new XMLException("File does not match any schema");
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

    private void assignSize() {

    }

    private void assignNumStates() {

    }

    private void assignMyIsRandom() {

    }

    private void assignRandomMakeup() {

    }

    private void assignConfiguration() {

    }

    private void assignParameters() {

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

    private DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new XMLException(e);
        }
    }

    private void resetInstanceVars() {
        myRandomMakeup = new ArrayList<>();
        myStateConfiguration = new ArrayList<>();
        myParameters = new ArrayList<>();
        mySliderMap = new HashMap<>();
        myRoot = null;
        myRootType = null;
        mySize = 0;
        myNumStates = 0;
        myIsRandom = false;
    }

    /**
     * @return XML.CA_TYPE of file parsed
     */
    public CA_TYPE getCAType() { return myRootType; }

    /**
     * @return a map of with Slider names as keys and Double arrays containing min and max slider values
     */
    public HashMap<String, Integer[]> getSliderNamesAndValues() { return mySliderMap; }

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
    public Double[] getRandomMakeup() { return myRandomMakeup.toArray(new Double[0]); }

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
}
