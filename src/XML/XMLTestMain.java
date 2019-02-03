package XML;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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
        System.out.println();
        System.out.println(tester.myParser.getCAType());
        System.out.println(tester.myParser.getGridSize());
        System.out.println(tester.myParser.getNumStates());
        System.out.println(tester.myParser.getIsRandom());
        printAllInDubArray(tester.myParser.getRandomComposition());
        printAllPositions(tester.myParser.getConfiguration());
        printAllInDubArray(tester.myParser.getParameters());
        printSliders(tester.myParser.getSliderNamesAndValues());
    }

    private void printSliders(LinkedHashMap<String,Integer[]> map) {
        //FIx
    }

    private void printAllInDubArray(Double[] array) {
        for (Double dub : array)
            System.out.println(dub);
    }

    private void printAllPositions(ArrayList<Integer[]> list) {
        int x = 0; int y=0; int state=0;
        for (Integer[] coords : list) {
            x=coords[0]; y=coords[1]; state=coords[2];
            System.out.printf("(%d, %d) in state %d\n", x, y, state);
        }
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
