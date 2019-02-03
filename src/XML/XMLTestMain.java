package XML;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class XMLTestMain extends Application {
    public static final String DATA_FILE_EXTENSION = "*.xml";
    public XMLParser myParser;
    public FileChooser myChooser = makeChooser(DATA_FILE_EXTENSION);

    public XMLTestMain() {
        myParser = new XMLParser();
    }

    public void start(Stage primaryStage) {
        XMLTestMain tester = new XMLTestMain();
        var xmlFile = tester.myChooser.showOpenDialog(primaryStage);
        if (xmlFile == null)
            return;
        tester.myParser.parseFile(xmlFile);
        System.out.println(tester.myParser.getCAType());
        System.out.println(tester.myParser.getGridSize());
        System.out.println(tester.myParser.getNumStates());
        System.out.println(tester.myParser.getIsRandom());
        /*System.out.println(myParser.getConfiguration());
        System.out.println(myParser.getParameters());
        System.out.println(myParser.getRandomComposition());*/
    }

    private FileChooser makeChooser (String extensionAccepted) {
        var result = new FileChooser();
        result.setTitle("Open Data File");
        // pick a reasonable place to start searching for files
        result.setInitialDirectory(new File(System.getProperty("user.dir")));
        result.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Text Files", extensionAccepted));
        return result;
    }
}
