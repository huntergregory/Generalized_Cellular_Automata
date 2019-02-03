import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
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
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * Validation inspired by Wayan Saryada's article.
 * @see <a href="https://kodejava.org/how-do-i-validate-xml-against-xsd-in-java/">Wayan Saryada's</a> article
 * @author Hunter Gregory
 */
public class XMLParser {
    private SchemaFactory mySchemaFactory;
    private String myXMLFile;
    private int mySize;
    private int myNumStates;
    private boolean myIsRandom;
    private ArrayList<Double> myRandomMakeup;             //only used if random
    private ArrayList<Integer[]> myStateConfigurations;   //only used if configured
    private ArrayList<Integer> myParameters; //FIX make parameters ints

    public XMLParser(String xmlFile) {
        mySchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    public Grid parseFile(String xmlFile) {
        myXMLFile = xmlFile;
        readFile();
        return extractGrid();
    }

    private void readFile() {
        myRandomMakeup = new ArrayList<>();
        myStateConfigurations = new ArrayList<>();
        myParameters = new ArrayList<>();


    }


    private Grid extractGrid() {
        for (CA_TYPE type : CA_TYPE.values()) {
            if (fileIsSchema(type)) {
                if (myIsRandom) {
                    Constructor<Grid> constructor = type.getRandomConstructor();
                    return constructor.newInstance(mySize, Main.GRID_DISPLAY_SIZE, myRandomMakeup, myParameters);
                }
                Constructor<Grid> constructor = type.getConfiguredConstructor();
                return constructor.newInstance(mySize, Main.GRID_DISPLAY_SIZE, myStateConfigurations, myParameters);
            }
        }
        return null; //shouldn't get to here
    }

    private boolean fileIsSchema(CA_TYPE type) {
        mySchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = mySchemaFactory.newSchema(new File(getResource(type.getSchemaFile())));

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

}
