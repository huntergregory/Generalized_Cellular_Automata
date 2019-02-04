import java.io.File;
import java.lang.reflect.Constructor;

import XML.*;
import GridCell.*;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author Dhanush Madabusi
 * @coauthor Hunter Gregory
 */
public class SimulatorMain extends Application {
    public static final int GRID_DISPLAY_SIZE = 100;

    private CA_TYPE myType;
    private Grid myGrid;
    private XMLParser myParser;

    public SimulatorMain() {
        myType = null;
        myGrid = null;
        myParser = new XMLParser();
    }

    public void start(Stage primaryStage) {

    }

    private void getNewGrid(File xmlFile) throws InstantiationException {
        myParser.parseFile(xmlFile);

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
            Constructor<Grid> constructor = newType.getRandomConstructor();
            try {
                myGrid = constructor.newInstance(gridSize, GRID_DISPLAY_SIZE, myParser.getRandomComposition());
                return true;
            }
            catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                return false;
            }
        }
        else {
            Constructor<Grid> constructor = newType.getConfiguredConstructor();
            try {
                myGrid = constructor.newInstance(gridSize, GRID_DISPLAY_SIZE, myParser.getConfiguration());
                return true;
            }
            catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                return false;
            }
        }
    }
}
