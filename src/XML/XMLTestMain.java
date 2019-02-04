package XML;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class XMLTestMain extends Application {
    public static final String DATA_FILE_EXTENSION = "*.xml";
    public XMLParser myParser;
    public FileChooser myChooser;

    public XMLTestMain() {
        myParser = new XMLParser();
        myChooser = makeChooser(DATA_FILE_EXTENSION);
        myChooser.setInitialDirectory(new File("data/automata"));
    }

    public void start(Stage primaryStage) {
        XMLTestMain tester = new XMLTestMain();
        var xmlFile = tester.myChooser.showOpenDialog(primaryStage);
        if (xmlFile == null) //in case someone clicks cancel
            return;
        tester.myParser.parseFile(xmlFile);
        printShit(tester);
    }

    /**
     * This demonstrates how to use all the getter methods of XMLParser. It also is good for debugging
     * @param tester
     */
    private void printShit(XMLTestMain tester) {
        System.out.println(tester.myParser.getCAType());
        System.out.println("Grid size: " + tester.myParser.getGridSize());
        System.out.println("Number of states: " + tester.myParser.getNumStates());
        System.out.println("Grid is randomly composed? " + tester.myParser.getIsRandom());
        printAllInDubArray(tester.myParser.getRandomComposition(), "random composition of states");
        printAllPositions(tester.myParser.getConfiguration());
        printAllInDubArray(tester.myParser.getParameters(), "initial values of parameters");
        printSliders(tester.myParser.getSliderNamesAndValues());
    }

    private void printSliders(LinkedHashMap<String,Double[]> map) {
        System.out.println("Printing slider information for grid");
        for (String key : map.keySet()) {
            Double[] extrema = map.get(key);
            System.out.printf("Slider %s with min=%f and max=%f\n", key, extrema[0], extrema[1]);
        }
    }

    private void printAllInDubArray(Double[] array, String reason) {
        System.out.println("Printing doubles for " + reason);
        for (Double dub : array)
            System.out.println(dub);
    }

    private void printAllPositions(ArrayList<Integer[]> list) {
        int x = 0; int y=0; int state=0;
        System.out.println("Printing configuration");
        for (Integer[] coords : list) {
            x=coords[0]; y=coords[1]; state=coords[2];
            System.out.printf("Location (%d, %d) for state %d\n", x, y, state);
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
