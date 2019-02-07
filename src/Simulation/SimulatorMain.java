package Simulation;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import XML.*;
import GridCell.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Dhanush Madabusi
 * @coauthor Hunter Gregory
 */
public class SimulatorMain extends Application {
    public static final double GRID_DISPLAY_SIZE = 350.0;
    public static final String DATA_FILE_EXTENSION = "*.xml";
    private static final double SCREEN_WIDTH = 550.0;
    private static final double SCREEN_HEIGHT = 550.0;
    private static final int FRAMES_PER_SECOND = 60;
    private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;


    private CA_TYPE myType;
    private Grid myGrid;
    private XMLParser myParser;
    private FileChooser myChooser;
    private Group root;
    private Group cellGroup;
    private int stepCounter = 0;

    public SimulatorMain() {
        myType = null;
        myGrid = null;
        myParser = new XMLParser();
        myChooser = makeChooser(DATA_FILE_EXTENSION);
        myChooser.setInitialDirectory(new File("data/automata"));
    }

    @Override
    public void start(Stage stage) {
        if (!handleXMLFile(stage)){
            return;
        }

        Scene scene = setUpScene();
        stage.setScene(scene);
        stage.setTitle("Cell Society");
        stage.show();

        var frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> simulationStep(stage));
        var gameTime = new Timeline();
        gameTime.setCycleCount(Timeline.INDEFINITE);
        gameTime.getKeyFrames().add(frame);
        gameTime.play();
        //simulateTransitions(10);
    }

    private Scene setUpScene() {
        root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BEIGE);
        ObservableList rootList = root.getChildren();
        cellGroup = initializeCellGroup();
        rootList.addAll(cellGroup);
        return scene;
    }

    private void simulationStep(Stage stage) {
        if (stepCounter % 5 == 0) {
            handleGridUpdate();
            stepCounter = 0;
        }
        stepCounter++;
    }

    private void handleGridUpdate() {
        myGrid.updateCells();
        resetCellGroup();
    }

    private Group initializeCellGroup() {
        var gridArray = myGrid.getGrid();
        var cellGroup = new Group();
        ObservableList cellGroupList = cellGroup.getChildren();
        for (Cell[] cellRow : gridArray){
            for (Cell cell : cellRow){
                cellGroupList.add(cell.getCellBody());
            }
            //System.out.println();
        }
        return cellGroup;
    }

    private void resetCellGroup() {
        root.getChildren().remove(cellGroup);
        cellGroup = initializeCellGroup();
        root.getChildren().add(cellGroup);
    }

    private boolean handleXMLFile(Stage stage){
        File xmlFile = myChooser.showOpenDialog(stage);
        if (xmlFile == null) { //in case someone clicks cancel
            Platform.exit();
            return false;
        }
        else {
            try {
                getNewGrid(xmlFile);
            } catch (InstantiationException e) {
                System.out.println(e.getMessage());
            }
        }
        return true;
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
                myGrid.setGridRandom(myParser.getRandomComposition());
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
                myGrid.setGridSpecific(myParser.getConfiguration());
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


    /**
     * Starts the program.
     *
     * @param args
     */
    public static void main (String[] args){
        launch(args);
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
        int row = 0; int col=0; int state=0;
        System.out.println("Printing configuration");
        for (Integer[] coords : list) {
            row=coords[0]; col=coords[1]; state=coords[2];
            System.out.printf("Location row %d, col %d for state %d\n", row, col, state);
        }
    }
}
