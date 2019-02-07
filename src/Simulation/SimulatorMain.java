package Simulation;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

import XML.*;
import GridCell.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
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
    private static final double BUTTON_WIDTH = 65.0;
    private static final double BUTTON_HEIGHT = 30.0;
    private static final double BUTTON_SPACING = 3.0;

    private CA_TYPE myType;
    private Grid myGrid;
    private XMLParser myParser;
    private FileChooser myChooser;
    private Group root;
    private Group cellGroup;
    private int stepCounter = 0;
    private boolean pauseSim;
    private Button stopButton;
    private Button startButton;

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
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.LIGHTCYAN);
        ObservableList rootList = root.getChildren();
        cellGroup = initializeCellGroup();
        var buttonGroup = initializeButtonGroup();
        var sliderGroup = initializeSliderGroup();
        rootList.addAll(cellGroup, buttonGroup, sliderGroup);

        pauseSim = true;

        return scene;
    }

    private void simulationStep(Stage stage) {
        if (!pauseSim) {
            if (stepCounter % 40 == 0) {
                handleGridUpdate();
                stepCounter = 0;
            }
            stepCounter++;
        }
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

    private Group initializeButtonGroup() {
        var buttonGroup = new Group();
        ObservableList buttonList = buttonGroup.getChildren();
        buttonList.addAll(createResetButton(), createStartButton(), createStopButton(), createStepButton());
        return buttonGroup;
    }

    private Button createResetButton() {
        Button resetButton = new Button("Reset");
        resetButton.setPrefWidth(BUTTON_WIDTH);
        resetButton.setPrefHeight(BUTTON_HEIGHT);
        resetButton.setLayoutX(Grid.GRID_PADDING);
        resetButton.setLayoutY(Grid.GRID_PADDING + GRID_DISPLAY_SIZE + Grid.GRID_PADDING);
        resetButton.setFocusTraversable(false);
        resetButton.setOnMouseClicked(buttonClick -> handleReset());
        resetButton.setOnMouseEntered(mouseEvent -> resetButton.setEffect(new DropShadow()));
        resetButton.setOnMouseExited(mouseEvent -> resetButton.setEffect(null));
        resetButton.setStyle("-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), " +
                "radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%); " +
                "-fx-background-radius: 30; -fx-background-insets: 0,1,1; -fx-text-fill: black; -fx-font-size: 14;");
        return resetButton;
    }

    private void handleReset() {
        if (!myParser.getIsRandom()) {
            myGrid.setGridSpecific(myParser.getConfiguration());
        }
        else {
            myGrid.setGridRandom(myGrid.getCurComposition());
        }
        resetCellGroup();
        pauseSim = true;
        stopButton.setDisable(true);
        startButton.setDisable(false);
    }

    private Button createStartButton() {
        startButton = new Button("Start");
        startButton.setPrefWidth(BUTTON_WIDTH);
        startButton.setPrefHeight(BUTTON_HEIGHT);
        startButton.setLayoutX(Grid.GRID_PADDING);
        startButton.setLayoutY(Grid.GRID_PADDING + GRID_DISPLAY_SIZE + Grid.GRID_PADDING + startButton.getPrefHeight()
                + BUTTON_SPACING);
        startButton.setFocusTraversable(false);
        startButton.setOnMouseClicked(buttonClick -> handleStart());
        startButton.setOnMouseEntered(mouseEvent -> startButton.setEffect(new DropShadow()));
        startButton.setOnMouseExited(mouseEvent -> startButton.setEffect(null));
        startButton.setStyle("-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), " +
                "radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%); " +
                "-fx-background-radius: 30; -fx-background-insets: 0,1,1; -fx-text-fill: black; -fx-font-size: 14;");
        return startButton;
    }

    private void handleStart() {
        pauseSim = false;
        System.out.println("Start");
        stopButton.setDisable(false);
        startButton.setDisable(true);
    }

    private Button createStopButton() {
        stopButton = new Button("Stop");
        stopButton.setPrefWidth(BUTTON_WIDTH);
        stopButton.setPrefHeight(BUTTON_HEIGHT);
        stopButton.setLayoutX(Grid.GRID_PADDING);
        stopButton.setLayoutY(Grid.GRID_PADDING + GRID_DISPLAY_SIZE + Grid.GRID_PADDING + 2*stopButton.getPrefHeight()
                + 2*BUTTON_SPACING);
        stopButton.setFocusTraversable(false);
        stopButton.setOnMouseClicked(buttonClick -> handleStop());
        stopButton.setOnMouseEntered(mouseEvent -> stopButton.setEffect(new DropShadow()));
        stopButton.setOnMouseExited(mouseEvent -> stopButton.setEffect(null));
        stopButton.setStyle("-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), " +
                "radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%); " +
                "-fx-background-radius: 30; -fx-background-insets: 0,1,1; -fx-text-fill: black; -fx-font-size: 14;");
        stopButton.setDisable(true);
        return stopButton;
    }

    private void handleStop() {
        pauseSim = true;
        System.out.println("Stop");
        startButton.setDisable(false);
        stopButton.setDisable(true);
    }

    private Button createStepButton() {
        Button stepButton = new Button("Step");
        stepButton.setPrefWidth(BUTTON_WIDTH);
        stepButton.setPrefHeight(BUTTON_HEIGHT);
        stepButton.setLayoutX(Grid.GRID_PADDING);
        stepButton.setLayoutY(Grid.GRID_PADDING + GRID_DISPLAY_SIZE + Grid.GRID_PADDING + 3*stepButton.getPrefHeight()
                + 3*BUTTON_SPACING);
        stepButton.setFocusTraversable(false);
        stepButton.setOnMouseClicked(buttonClick -> handleStep());
        stepButton.setOnMouseEntered(mouseEvent -> stepButton.setEffect(new DropShadow()));
        stepButton.setOnMouseExited(mouseEvent -> stepButton.setEffect(null));
        stepButton.setStyle("-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), " +
                "radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%); " +
                "-fx-background-radius: 30; -fx-background-insets: 0,1,1; -fx-text-fill: black; -fx-font-size: 14;");
        return stepButton;
    }

    private void handleStep() {
        pauseSim = true;
        handleGridUpdate();
        stopButton.setDisable(true);
        startButton.setDisable(false);
        System.out.println("Step");
    }

    private Group initializeSliderGroup() {
        var sliderGroup = new Group();
//        ObservableList sliderList = sliderGroup.getChildren();
//        var sliderMap = myGrid.getSliderMap();
//        for (String key : sliderMap.keySet()){
//
//
//        }
        return sliderGroup;
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
