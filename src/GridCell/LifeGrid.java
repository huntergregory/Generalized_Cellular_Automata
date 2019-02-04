package GridCell;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Grid subclass that represents a John Conway's Game of Life
 * The simulation can have a random composition of 'populated' cells, but typically it's more interesting to make
 * explicit configured positions to make patterns such as the Glider.
 * @author Hunter Gregory
 */
public class LifeGrid extends Grid {
    public static final int EMPTY = 0;
    public static final int POPULATED = 1;

    /**
     * Create a LifeGrid that initializes states in explicit positions based on the configuration.
     * @param gridSize
     * @param screenSize
     * @param configuration
     */
    public LifeGrid(int gridSize, double screenSize, ArrayList<Integer[]> configuration) {
        super(gridSize, screenSize);
        setGridSpecific(configuration);
    }

    /**
     * Create a LifeGrid that initializes states randomly based on their percent composition.
     * @param gridSize
     * @param screenSize
     * @param randomComp
     */
    public LifeGrid(int gridSize, double screenSize, Double[] randomComp) {
        super(gridSize, screenSize);
        setGridRandom(randomComp);
    }

    @Override
    public void initStateColorMap() {
        HashMap<Integer, Color> map = new HashMap<>();
        map.put(EMPTY, Color.GREY);
        map.put(POPULATED, Color.YELLOW);
        setStateColorMap(map);
    }

    @Override
    public void setAdditionalParams(Double[] params) {
        //no parameters for this game
    }

    @Override
    public void updateCells() {
        Cell[][] gridCopy = getGridCopy();
        Cell[][] newGrid = getGrid();

        int size = newGrid.length;
        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                Cell currentCell = gridCopy[r][c];
                Cell newCell = newGrid[r][c];
                if (currentCell.getState() == EMPTY) {
                    updateEmptyCell(newCell);
                    continue;
                }
                updatePopulatedCell(newCell);
            }
        }
    }

    private void updateEmptyCell(Cell newCell) {
        //FIX
    }

    private void updatePopulatedCell(Cell newCell) {
        //FIX
    }
}
