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
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Dhanush Madabusi
 * @coauthor Hunter Gregory
 */
public class SimulatorMain extends Application {
    private static final double GRID_DISPLAY_SIZE = 400.0;
    private static final String DATA_FILE_EXTENSION = "*.xml";
    private static final double SCREEN_WIDTH = 650.0;
    private static final double SCREEN_HEIGHT = 650.0;
    private static final double BUTTON_WIDTH = 90.0;
    private static final double BUTTON_HEIGHT = 30.0;
    private static final double BUTTON_SPACING = 5.0;
    private static final double SLIDER_SPACING = 5.0;
    private static final double SLIDER_WIDTH = 175.0;
    private static final String SPECIFIED_LOCATIONS = "locations";
    private static final String RANDOM_COMP = "random composition";
    private static final String RANDOM_NUMS = "random numbers";
    private static final int INITIAL_DELAY = 30;

    private CA_TYPE myType;
    private Grid myGrid;
    private XMLParser myParser;
    private FileChooser myChooser;
    private Group root;
    private Group cellGroup;
    private VBox sliderLabels;
    private VBox sliderVBox;
    private int stepCounter = 0;
    private boolean pauseSim;
    private Button stopButton;
    private Button startButton;
    private Stage simStage;
    private int roundCounter = 0;
    private Text roundLabel;
    private int sliderDelayValue = INITIAL_DELAY;
    private boolean delayUpdated = false;
    private int simDelay = INITIAL_DELAY;
    private boolean cellBorders = true;

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
            Platform.exit();
            return;
        }

        simStage = stage;

        Scene scene = setUpScene();
        stage.setScene(scene);
        stage.setTitle("Cellular Automata Simulator");
        stage.show();

        var frame = new KeyFrame(Duration.ONE, e -> simulationStep(stage));
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
        cellGroup = initializeCellGroup(cellBorders);
        var buttonVBox = initializeButtonVBox();
        sliderVBox = initializeSliderVBox();
        rootList.addAll(cellGroup, createRoundLabel(), buttonVBox, sliderVBox, createBorderToggle());

        pauseSim = true;

        return scene;
    }

    private void simulationStep(Stage stage) {
        if (!pauseSim) {
            if (simDelay == 0 || stepCounter % simDelay == 0) {
                handleGridUpdate();
                stepCounter = 0;
            }
            stepCounter++;
        }
        else if (delayUpdated){
            simDelay = sliderDelayValue;
            delayUpdated = false;
        }
    }

    private void handleGridUpdate() {
        myGrid.updateCells();
        resetCellGroup(cellBorders);
        roundCounter++;
        updateRoundLabel();
    }

    private Group initializeCellGroup(boolean addBorder) {
        var gridArray = myGrid.getGrid();
        var cellGroup = new Group();
        ObservableList cellGroupList = cellGroup.getChildren();
        for (Cell[] cellRow : gridArray){
            for (Cell cell : cellRow){
                cell.setCellBorder(addBorder);
                cellGroupList.add(getCellBody(cell));
            }
        }
        myGrid.setGrid(gridArray);
        return cellGroup;
    }

    private Shape getCellBody(Cell c){
        if (myGrid.getMyCellShape() == CELL_SHAPE.SQUARE){
            return ((RectangleCell)c).getCellBody();
        }
        else if (myGrid.getMyCellShape() == CELL_SHAPE.TRIANGLE){
            return ((TriangleCell)c).getCellBody();
        }else{
            return ((HexagonCell)c).getCellBody();
        }
    }

    private void resetCellGroup(boolean addCellBorders) {
        root.getChildren().remove(cellGroup);
        cellGroup = initializeCellGroup(addCellBorders);
        root.getChildren().add(cellGroup);
    }

    private void resetSliderVBox(){
        root.getChildren().remove(sliderVBox);
        sliderVBox = initializeSliderVBox();
        root.getChildren().add(sliderVBox);
    }

    private boolean handleXMLFile(Stage stage){
        File xmlFile = myChooser.showOpenDialog(stage);
        if (xmlFile == null) { //in case someone clicks cancel
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

    //returns false if didn't work, but should always work
    private boolean assignGrid() {
        int gridSize = myParser.getGridSize();
        CA_TYPE newType = myParser.getCAType();
        Constructor<? extends Grid> constructor = newType.getConstructor();
        try {
            myGrid = constructor.newInstance(gridSize, GRID_DISPLAY_SIZE);
            myGrid.setImmutables(myParser.getEdgeType(), myParser.getCellShape(), myParser.getNeighborConfig());
            String configType = myParser.getConfigType();
            if (configType.equals(RANDOM_COMP))
                myGrid.setGridRandom(myParser.getRandomComposition());
            else if (configType.equals(SPECIFIED_LOCATIONS))
                myGrid.setGridSpecific(myParser.getLocations());
            else
                myGrid.setGridRandomNum(myParser.getRandomNumbers());
            return true;
        }
        catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            // should always work
            return false;
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

    private VBox initializeButtonVBox() {
        VBox buttonVBox = new VBox();
        ObservableList buttonList = buttonVBox.getChildren();
        var resetButton = createButton("Reset");
        resetButton.setOnMouseClicked(buttonClick -> handleReset());
        startButton = createButton("Start");
        startButton.setOnMouseClicked(buttonClick -> handleStart());
        stopButton = createButton("Stop");
        stopButton.setOnMouseClicked(buttonClick -> handleStop());
        var stepButton = createButton("Step");
        stepButton.setOnMouseClicked(buttonClick -> handleStep());
        var loadFileButton = createButton("Load File");
        loadFileButton.setOnMouseClicked(buttonClick -> handleLoadFile());
        buttonList.addAll(resetButton, startButton, stopButton, stepButton, loadFileButton);
        buttonVBox.setSpacing(BUTTON_SPACING);
        buttonVBox.setLayoutX(Grid.GRID_PADDING);
        buttonVBox.setLayoutY(GRID_DISPLAY_SIZE + (5*Grid.GRID_PADDING)/2);
        buttonVBox.setAlignment(Pos.CENTER);
        return buttonVBox;
    }

    private Button createButton(String name){
        var button = new Button(name);
        button.setPrefWidth(BUTTON_WIDTH);
        button.setPrefHeight(BUTTON_HEIGHT);
        button.setFocusTraversable(false);
        button.setOnMouseEntered(mouseEvent -> button.setEffect(new DropShadow()));
        button.setOnMouseExited(mouseEvent -> button.setEffect(null));
        button.setStyle("-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), " +
                "radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%); " +
                "-fx-background-radius: 30; -fx-background-insets: 0,1,1; -fx-text-fill: black; -fx-font-size: 14;");
        return button;
    }

    private void handleReset() {
        String configType = myParser.getConfigType();
        if (configType.equals(SPECIFIED_LOCATIONS)) {
            myGrid.setGridSpecific(myParser.getLocations());
        }
        else if (configType.equals(RANDOM_NUMS)) {
            myGrid.setGridRandomNum(myParser.getRandomNumbers());
        }
        else {
            myGrid.setGridRandom(myGrid.getCurComposition());
        }
        resetCellGroup(cellBorders);
        handleAgeAndEnergyReset();
        pauseSim = true;
        stopButton.setDisable(true);
        startButton.setDisable(false);
        roundCounter = 0;
        updateRoundLabel();
    }

    private void handleAgeAndEnergyReset() {
        if (myGrid instanceof PredatorPrey){
            Double[] parameters = myParser.getParameters();
            if (parameters.length > 0)
                myGrid.setAdditionalParams(parameters);
        }
    }

    private void handleStart() {
        pauseSim = false;
        stopButton.setDisable(false);
        startButton.setDisable(true);
    }

    private void handleStop() {
        pauseSim = true;
        startButton.setDisable(false);
        stopButton.setDisable(true);
    }

    private void handleStep() {
        pauseSim = true;
        handleGridUpdate();
        stopButton.setDisable(true);
        startButton.setDisable(false);
    }

    private void handleLoadFile() {
        pauseSim = true;
        if (handleXMLFile(simStage)) {
            stopButton.setDisable(true);
            startButton.setDisable(false);
            resetCellGroup(cellBorders);
            resetSliderVBox();
            roundCounter = 0;
            updateRoundLabel();
        }
    }

    private Text createRoundLabel(){
        roundLabel = new Text();
        roundLabel.setText("Round " + roundCounter);
        roundLabel.setFont(new Font(17.0));
        roundLabel.setX(Grid.GRID_PADDING);
        roundLabel.setY(GRID_DISPLAY_SIZE + 2*Grid.GRID_PADDING);
        return roundLabel;
    }

    private void updateRoundLabel(){
        roundLabel.setText("Round " + roundCounter);
    }

    private VBox initializeSliderVBox() {
        sliderVBox = new VBox();
        ObservableList sliderList = sliderVBox.getChildren();
        sliderList.addAll(createSizeSliderHBox(), createDelaySliderHBox());
        sliderVBox.setSpacing(SLIDER_SPACING);
        sliderVBox.setLayoutX(150.0);
        sliderVBox.setLayoutY(GRID_DISPLAY_SIZE + (5*Grid.GRID_PADDING)/2);
        sliderVBox.setAlignment(Pos.CENTER);
        return sliderVBox;
    }

    private HBox createSizeSliderHBox() {
        HBox sizeSliderHBox = new HBox();
        var sizeLabel = createSizeSliderLabel();
        sizeSliderHBox.getChildren().addAll(createSizeSlider(sizeLabel), sizeLabel);
        sizeSliderHBox.setSpacing(20.0);
        return sizeSliderHBox;
    }

    private Text createSizeSliderLabel() {
        Text sizeLabel = new Text();
        sizeLabel.setText("Size: " + myGrid.getGridSize() + "x" + myGrid.getGridSize());
        sizeLabel.setFont(new Font(17.0));
        return sizeLabel;
    }

    private Slider createSizeSlider(Text sizeLabel) {
        var sizeSlider = new Slider(10,50, myGrid.getGridSize());
        sizeSlider.setMajorTickUnit(10);
        sizeSlider.setMinorTickCount(9);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setMaxWidth(SCREEN_WIDTH);
        sizeSlider.valueProperty().addListener(e -> handleSizeSliderChange(sizeSlider, sizeLabel));
        return sizeSlider;
    }

    private void handleSizeSliderChange(Slider sizeSlider, Text sizeLabel) {
        var newSize = (int) sizeSlider.getValue();
        myGrid.setGridSize(newSize);
        sizeLabel.setText("Size: " + newSize + "x" + newSize);
        handleReset();
    }

    private HBox createDelaySliderHBox() {
        HBox delaySliderHBox = new HBox();
        var delayLabel = createDelaySliderLabel();
        delaySliderHBox.getChildren().addAll(createDelaySlider(delayLabel), delayLabel);
        delaySliderHBox.setSpacing(20.0);
        return delaySliderHBox;
    }

    private Text createDelaySliderLabel() {
        Text delayLabel = new Text();
        delayLabel.setText("Delay: " + INITIAL_DELAY + " ms");
        delayLabel.setFont(new Font(17.0));
        return delayLabel;
    }

    private Slider createDelaySlider(Text delayLabel) {
        var delaySlider = new Slider(100,3000, INITIAL_DELAY);
        delaySlider.setMajorTickUnit(100);
        delaySlider.setMinorTickCount(99);
        delaySlider.setSnapToTicks(true);
        delaySlider.setMaxWidth(SCREEN_WIDTH);
        delaySlider.valueProperty().addListener(e -> handleDelaySliderChange(delaySlider, delayLabel));
        return delaySlider;

    }

    private void handleDelaySliderChange(Slider delaySlider, Text delayLabel) {
        sliderDelayValue = (int)delaySlider.getValue();
        delayUpdated = true;
        delayLabel.setText("Delay: " + sliderDelayValue + " ms");
    }

    private ToggleButton createBorderToggle() {
        var borderToggle = new ToggleButton("Cell Borders");
        borderToggle.setSelected(true);
        borderToggle.selectedProperty().addListener(e -> handleBorderToggle(borderToggle.isSelected()));
        borderToggle.setLayoutX(450.0);
        borderToggle.setLayoutY(GRID_DISPLAY_SIZE + (5*Grid.GRID_PADDING)/2);
        return borderToggle;
    }

    private void handleBorderToggle(boolean addCellBorder) {
        resetCellGroup(addCellBorder);
        cellBorders = addCellBorder;
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
        System.out.println("Grid config type is " + myParser.getConfigType());
        printAllInDubArray(myParser.getRandomComposition(), "random composition of states");
        printAllPositions(myParser.getLocations());
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
        int row; int col; int state;
        System.out.println("Printing configuration");
        for (Integer[] coords : list) {
            row=coords[0]; col=coords[1]; state=coords[2];
            System.out.printf("Location row %d, col %d for state %d\n", row, col, state);
        }
    }
}
