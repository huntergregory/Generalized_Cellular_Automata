package Simulation;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import XML.*;
import GridCell.*;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Dhanush Madabusi
 * @coauthor Hunter Gregory
 */
public class SimulatorMain extends Application {
    public static final int GRID_DISPLAY_SIZE = 100;
    public static final String DATA_FILE_EXTENSION = "*.xml";

    private CA_TYPE myType;
    private Grid myGrid;
    private XMLParser myParser;
    private FileChooser myChooser;

    public SimulatorMain() {
        myType = null;
        myGrid = null;
        myParser = new XMLParser();
        myChooser = makeChooser(DATA_FILE_EXTENSION);
        myChooser.setInitialDirectory(new File("data/automata"));
    }

    public void start(Stage primaryStage) {
        var xmlFile = myChooser.showOpenDialog(primaryStage);
        if (xmlFile == null) //in case someone clicks cancel
            return;

        try {
            getNewGrid(xmlFile);
        }
        catch (InstantiationException e) {
            System.out.println("Problem with instantiating grid");
        }

        simulateTransitions(10);
    }

    /**
     * DEBUGGING, prints grids for specified # of transitions
     * @param numTransitions
     */
    private void simulateTransitions(int numTransitions) {
        System.out.println("Initial State");
        myGrid.printGrid();
        for (int k=0; k<numTransitions;k++) {
            myGrid.updateCells();
            System.out.println("Round " + (k + 1));
            myGrid.printGrid();
        }
    }

    private void getNewGrid(File xmlFile) throws InstantiationException {
        myParser.parseFile(xmlFile);
        printShit();

        boolean wasSuccessful = assignGrid();
        if (!wasSuccessful)
            throw new InstantiationException("Creating the new grid didn't work. Problem with the constructor");

        myType = myParser.getCAType();
        Double[] parameters = myParser.getParameters();
        if (parameters.length > 0)
            myGrid.setAdditionalParams(parameters);
    }

    //returns false if didn't work
    private boolean assignGrid() {
        int gridSize = myParser.getGridSize();
        CA_TYPE newType = myParser.getCAType();
        if (myParser.getIsRandom()) {
            Constructor<? extends Grid> constructor = newType.getRandomConstructor();
            try {
                myGrid = constructor.newInstance(gridSize, GRID_DISPLAY_SIZE, myParser.getRandomComposition());
                return true;
            }
            catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                return false;
            }
        }
        else {
            Constructor<? extends Grid> constructor = newType.getConfiguredConstructor();
            try {
                myGrid = constructor.newInstance(gridSize, GRID_DISPLAY_SIZE, myParser.getConfiguration());
                return true;
            }
            catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                return false;
            }
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

    /////////////////////////////////   DEBUGGING   ///////////////////////////////////////////

    /**
     * This demonstrates how to use all the getter methods of XMLParser. It also is good for debugging
     */
    private void printShit() {
        System.out.println(myParser.getCAType());
        System.out.println("Grid size: " + myParser.getGridSize());
        System.out.println("Number of states: " + myParser.getNumStates());
        System.out.println("Grid is randomly composed? " + myParser.getIsRandom());
        printAllInDubArray(myParser.getRandomComposition(), "random composition of states");
        printAllPositions(myParser.getConfiguration());
        printAllInDubArray(myParser.getParameters(), "initial values of parameters");
        printSliders(myParser.getSliderNamesAndValues());
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
}
