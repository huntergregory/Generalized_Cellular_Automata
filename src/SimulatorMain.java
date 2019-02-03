import java.lang.reflect.Constructor;

/**
 *
 * @author Dhanush Madabusi
 * @coauthor Hunter Gregory
 */
public class SimulatorMain {
    public static final int GRID_DISPLAY_SIZE = 100;

    private CA_TYPE myType;
    private Grid myGrid;
    private XMLParser myParser;


    private void getNewGrid(String xmlFile) {
        myParser.parseFile(xmlFile);
        myType = myParser.getFileType();

        int gridSize = myParser.getGridSize();
        if (myParser.getIsRandom()) {
            Constructor<Grid> constructor = myType.getRandomConstructor();
            myGrid = constructor.newInstance(gridSize, GRID_DISPLAY_SIZE, myParser.getRandomMakeup());
        }
        else {
            Constructor<Grid> constructor = myType.getConfiguredConstructor();
            myGrid = constructor.newInstance(gridSize, GRID_DISPLAY_SIZE, myParser.getConfiguration());
        }

        Integer[] parameters = myParser.getParameters();
        if (parameters.length > 0)
            myGrid.setAdditionalParams(parameters);
    }
}
