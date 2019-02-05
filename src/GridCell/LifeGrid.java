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
    public static final int EMPTY = 1;
    public static final int POPULATED = 0;

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
            for (int c=0; c<size; c++)
                updateCell(gridCopy[r][c], newGrid[r][c], getNeighborCells(r,c, true));
        }
    }

    private void updateCell(Cell currentCell, Cell newCell, Cell[] neighbors) {
        int numPopulatedNeighbors = 0;
        for (Cell neighbor : neighbors) {
            if (neighbor.getState() == POPULATED)
                numPopulatedNeighbors ++;
        }

        if (currentCell.getState() == EMPTY) {
            if (numPopulatedNeighbors == 3)
                newCell.setState(POPULATED);
            return;
        }

        if (numPopulatedNeighbors <=1 || numPopulatedNeighbors >=4)
            newCell.setState(EMPTY);
    }
}
