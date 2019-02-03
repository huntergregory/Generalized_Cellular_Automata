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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * Validation inspired by Wayan Saryada's article.
 * @see <a href="https://kodejava.org/how-do-i-validate-xml-against-xsd-in-java/">Wayan Saryada's</a> article
 * @author Hunter Gregory
 */
public class XMLParser {
    private final SchemaFactory SCHEMAFACTORY;
    private final DocumentBuilder DOCUMENT_BUILDER;

    private String myXMLFile;
    private CA_TYPE rootType;
    private int mySize;
    private int myNumStates;
    private boolean myIsRandom;
    private ArrayList<Double> myRandomMakeup;             //only used if random
    private ArrayList<Integer[]> myStateConfigurations;   //only used if configured
    private ArrayList<Integer> myParameters; //FIX make parameters ints

    public XMLParser() {
        SCHEMAFACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        DOCUMENT_BUILDER = getDocumentBuilder();
    }

    /**
     * Stores necessary data from the given xml file
     * @param xmlFile
     * @throws XMLException if xmlFile doesn't match a schema associated with a CA_TYPE
     */
    public void parseFile(String xmlFile) throws XMLException {
        myXMLFile = xmlFile;
        myRandomMakeup = new ArrayList<>();
        myStateConfigurations = new ArrayList<>();
        myParameters = new ArrayList<>();


    }

    private boolean fileIsSchema(CA_TYPE type) {
        try {
            Schema schema = SCHEMAFACTORY.newSchema(new File(getResource(type.getSchemaFile())));

            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(getResource(myXMLFile))));
            return true;
        } catch (SAXException | IOException e) {
            return false;
        }
    }

    private String getResource(String filename) throws FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(filename);
        Objects.requireNonNull(resource);

        return resource.getFile();
    }

    private DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new XMLException(e);
        }
    }

    /**
     * Get the configured positions for all states.
     * @return list of integer arrays in the form (row, col, state). The last state's row and col will be -1 to indicate
     *          that it's composition should be inferred.
     */
    public ArrayList<Integer[]> getConfiguration() { return myStateConfigurations; }

    /**
     * Get the percent composition for each state.
     * @return an array of integers. The last state's composition will be -1 to indicate that it's composition
     *         should be inferred.
     */
    public Double[] getRandomMakeup() { return myRandomMakeup.toArray(new Double[0]); }

    /**
     * @return an array of Integers representing special parameters for the CA
     */
    public Integer[] getParameters() { return myParameters.toArray(new Integer[0]); }

    /**
     * @return true if the xml file is for a random-position CA
     */
    public boolean getIsRandom() { return myIsRandom; }

    /**
     * @return size of Grid for the xml file
     */
    public int getGridSize() { return mySize; }
}
