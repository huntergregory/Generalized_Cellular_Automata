package Simulation;
import java.io.File;
import java.lang.reflect.Constructor;
import XML.*;
import GridCell.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
/**
 * This class is the main driver for the CellSociety simulation. The class initializes the visualization by first
 * allowing the user to select an XML file for the simulation. The class then sets up the scene and all visual
 * components, including the Grid and all other UI components, such as buttons and sliders.
 * @author Dhanush Madabusi
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
    private static final String SPECIFIED_LOCATIONS = "locations";
    private static final String RANDOM_COMP = "random composition";
    private static final String RANDOM_NUMS = "random numbers";
    private static final int INITIAL_DELAY = 50;
    private Grid myGrid;
    private XMLParser myParser;
    private FileChooser myChooser;
    private Group root;
    private Group cellGroup;
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
        myGrid = null;
        myParser = new XMLParser();
        myChooser = makeChooser();
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
        var frame = new KeyFrame(Duration.ONE, e -> simulationStep());
        var gameTime = new Timeline();
        gameTime.setCycleCount(Timeline.INDEFINITE);
        gameTime.getKeyFrames().add(frame);
        gameTime.play();
    }

    private Scene setUpScene() {
        root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.LIGHTCYAN);
        cellGroup = initializeCellGroup(cellBorders);
        sliderVBox = initializeSliderVBox();
        roundLabel = new SimLabel("Round " + roundCounter, 17.0, Grid.GRID_PADDING, GRID_DISPLAY_SIZE + 2*Grid.GRID_PADDING);
        root.getChildren().addAll(cellGroup, roundLabel, initializeButtonVBox(), sliderVBox, createBorderToggle());
        pauseSim = true;
        return scene;
    }

    private void simulationStep() {
        if (!pauseSim) {
            if (simDelay == 0 || stepCounter % simDelay == 0) {
                stepCounter = 0;
                handleGridUpdate();
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
        incrementRoundLabel();
    }

    private Group initializeCellGroup(boolean addBorder) {
        var gridArray = myGrid.getGrid();
        var cellGroup = new Group();
        for (Cell[] cellRow : gridArray){
            for (Cell cell : cellRow){
                cell.setCellBorder(addBorder);
                cellGroup.getChildren().add(cell.getCellBody());
            }
        }
        myGrid.setGrid(gridArray);
        return cellGroup;
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
        boolean wasSuccessful = assignGrid();
        if (!wasSuccessful)
            throw new InstantiationException("Creating the new grid didn't work. Problem with the constructor");
        Double[] parameters = myParser.getParameters();
        if (parameters.length > 0)
            myGrid.setAdditionalParams(parameters);
    }

    private boolean assignGrid() {
        int gridSize = myParser.getGridSize();
        CA_TYPE newType = myParser.getCAType();
        Constructor<? extends Grid> constructor = newType.getConstructor();
        try {
            myGrid = constructor.newInstance(gridSize, GRID_DISPLAY_SIZE);
            myGrid.setImmutables(myParser.getEdgeType(), myParser.getCellShape(), myParser.getNeighborConfig());
            setGridConfig();
            return true;
        }
        catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            return false;
        }
    }

    private void setGridConfig(){
        String configType = myParser.getConfigType();
        if (configType.equals(RANDOM_COMP))
            if (myGrid.getCurComposition() == null) {
                myGrid.setGridRandom(myParser.getRandomComposition());
            }
            else{
                myGrid.setGridRandom(myGrid.getCurComposition());
            }
        else if (configType.equals(SPECIFIED_LOCATIONS))
            myGrid.setGridSpecific(myParser.getLocations());
        else
            myGrid.setGridRandomNum(myParser.getRandomNumbers());
    }

    private FileChooser makeChooser () {
        var result = new FileChooser();
        result.setTitle("Open Data File");
        result.setInitialDirectory(new File(System.getProperty("user.dir")));
        result.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Text Files", DATA_FILE_EXTENSION));
        return result;
    }

    private VBox initializeButtonVBox() {
        VBox buttonVBox = new VBox();
        var resetButton = new SimButton("Reset", BUTTON_WIDTH, BUTTON_HEIGHT);
        resetButton.setOnMouseClicked(buttonClick -> handleReset());
        startButton = new SimButton("Start", BUTTON_WIDTH, BUTTON_HEIGHT);
        startButton.setOnMouseClicked(buttonClick -> handleStart());
        stopButton = new SimButton("Stop", BUTTON_WIDTH, BUTTON_HEIGHT);
        stopButton.setOnMouseClicked(buttonClick -> handleStop());
        var stepButton = new SimButton("Step", BUTTON_WIDTH, BUTTON_HEIGHT);
        stepButton.setOnMouseClicked(buttonClick -> handleStep());
        var loadFileButton = new SimButton("Load File", BUTTON_WIDTH, BUTTON_HEIGHT);
        loadFileButton.setOnMouseClicked(buttonClick -> handleLoadFile());
        buttonVBox.getChildren().addAll(resetButton, startButton, stopButton, stepButton, loadFileButton);
        buttonVBox.setSpacing(BUTTON_SPACING);
        buttonVBox.setLayoutX(Grid.GRID_PADDING);
        buttonVBox.setLayoutY(GRID_DISPLAY_SIZE + (5*Grid.GRID_PADDING)/2);
        buttonVBox.setAlignment(Pos.CENTER);
        return buttonVBox;
    }

    private void handleReset() {
        setGridConfig();
        resetCellGroup(cellBorders);
        handleAgeAndEnergyReset();
        handleStop();
        resetRoundLabel();
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
        handleStop();
        handleGridUpdate();
    }

    private void handleLoadFile() {
        pauseSim = true;
        if (handleXMLFile(simStage)) {
            stopButton.setDisable(true);
            startButton.setDisable(false);
            resetCellGroup(cellBorders);
            resetSliderVBox();
            resetRoundLabel();
        }
    }

    private void incrementRoundLabel(){
        roundCounter++;
        roundLabel.setText("Round " + roundCounter);
    }

    private void resetRoundLabel(){
        roundCounter = 0;
        roundLabel.setText("Round " + roundCounter);
    }

    private VBox initializeSliderVBox() {
        sliderVBox = new VBox();
        sliderVBox.getChildren().addAll(createSizeSliderHBox(), createDelaySliderHBox());
        sliderVBox.setSpacing(SLIDER_SPACING);
        sliderVBox.setLayoutX(150.0);
        sliderVBox.setLayoutY(GRID_DISPLAY_SIZE + (5*Grid.GRID_PADDING)/2);
        sliderVBox.setAlignment(Pos.CENTER);
        return sliderVBox;
    }

    private HBox createSizeSliderHBox() {
        HBox sizeSliderHBox = new HBox();
        var sizeLabel = new SimLabel("Size: " + myGrid.getGridSize() + "x" + myGrid.getGridSize(), 17.0);
        var sizeSlider = new SimSlider(10,50, myGrid.getGridSize());
        sizeSlider.valueProperty().addListener(e -> handleSizeSliderChange(sizeSlider, sizeLabel));
        sizeSliderHBox.getChildren().addAll(sizeSlider, sizeLabel);
        sizeSliderHBox.setSpacing(20.0);
        return sizeSliderHBox;
    }

    private void handleSizeSliderChange(Slider sizeSlider, Text sizeLabel) {
        var newSize = (int) sizeSlider.getValue();
        myGrid.setGridSize(newSize);
        sizeLabel.setText("Size: " + newSize + "x" + newSize);
        handleReset();
    }

    private HBox createDelaySliderHBox() {
        HBox delaySliderHBox = new HBox();
        var delayLabel = new SimLabel("Delay: " + INITIAL_DELAY + " ms", 17.0);
        var delaySlider = new SimSlider(50,3000, INITIAL_DELAY);
        delaySlider.valueProperty().addListener(e -> handleDelaySliderChange(delaySlider, delayLabel));
        delaySliderHBox.getChildren().addAll(delaySlider, delayLabel);
        delaySliderHBox.setSpacing(20.0);
        return delaySliderHBox;
    }

    private void handleDelaySliderChange(Slider delaySlider, Text delayLabel) {
        sliderDelayValue = (int)delaySlider.getValue();
        delayUpdated = true;
        delayLabel.setText("Delay: " + sliderDelayValue + " ms");
    }

    private ToggleButton createBorderToggle() {
        var borderToggle = new SimToggle("Cell Borders", true, 450.0, GRID_DISPLAY_SIZE + (5*Grid.GRID_PADDING)/2);
        borderToggle.selectedProperty().addListener(e -> handleBorderToggle(borderToggle.isSelected()));
        return borderToggle;
    }

    private void handleBorderToggle(boolean addCellBorder) {
        resetCellGroup(addCellBorder);
        cellBorders = addCellBorder;
    }

    /**
     * Starts the program.
     * @param args program arguments
     */
    public static void main (String[] args){
        launch(args);
    }
}