package GridCell;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Grid subclass that represents a wildfire spreading through a forest.
 * The simulation can have explicit configured positions for all states, or it can have a random composition.
 * The parameters influencing each transition are
 *      - probability that a green tree catches fire (given either one of its neighbors is ablaze or it was struck by lightning)
 *      - probability that lightning strikes a green tree
 *      - time it takes for a tree to burn
 *      - probability that a dead tree grows (turns green)
 * Based on the Duke CS project.
 * @see <a href="https://www2.cs.duke.edu/courses/spring19/compsci308/assign/02_cellsociety/nifty/shiflet-fire/">Spreading of Fire</a>
 * @author Hunter Gregory
 */
public class FireGrid extends Grid {
    public static final int EMPTY = 0;
    public static final int GREEN = 1;
    public static final int BURNING = 2;

    private double probCatch;
    private double probLightning;
    private double burnTime;
    private double probGrow;

    /**
     * Create a FireGrid that initializes states in explicit positions based on the configuration.
     * @param gridSize
     * @param screenSize
     * @param configuration
     */
    public FireGrid(int gridSize, double screenSize, ArrayList<Integer[]> configuration) {
        super(gridSize, screenSize);
        setGridSpecific(configuration);
    }

    /**
     * Create a FireGrid that initializes states randomly based on their percent composition.
     * @param gridSize
     * @param screenSize
     * @param randomComp
     */
    public FireGrid(int gridSize, double screenSize, Double[] randomComp) {
        super(gridSize, screenSize);
        setGridRandom(randomComp);
    }

    @Override
    public void initStateColorMap() {
        HashMap<Integer, Color> map = new HashMap<>();
        map.put(EMPTY, Color.YELLOW);
        map.put(GREEN, Color.GREEN);
        map.put(BURNING, Color.RED);
        setStateColorMap(map);
    }

    @Override
    public void setAdditionalParams(Double[] params) {
        probCatch = params[0];
        probLightning = params[1];
        burnTime = params[2];
        probGrow = params[3];
    }

    @Override
    public void updateCells() {
        Cell[][] gridCopy = getGridCopy();
        Cell[][] newGrid = getGrid();

        int size = newGrid.length;
        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                Cell currentCell = gridCopy[r][c];
                if (currentCell.getState() == EMPTY) {
                    updateEmptyCell(currentCell);
                    continue;
                }
                if (currentCell.getState() == BURNING) {
                    updateBurningCell(currentCell);
                    continue;
                }
                updateGreenCell(r, c, currentCell);
            }
        }
    }

    private void updateEmptyCell(Cell currentCell) {
        if (getRandomDouble() <= probGrow) {
            currentCell.setState(GREEN);
            System.out.println("Growing!");
        }
    }

    private void updateBurningCell(Cell currentCell) {
        currentCell.setAge(1 + currentCell.getAge());
        if (currentCell.getAge() >= burnTime)
            currentCell.setState(EMPTY);
    }

    private void updateGreenCell(int r, int c, Cell currentCell) {
        //double probTransition = probLightning * probCatch;
        double probTransition = 0;
        for (Cell neighbor : getNeighborCells(r,c)) {
            if (neighbor.getState() == BURNING) {
                probTransition += probCatch;
            }
        }
        double dub = getRandomDouble();
        if (dub <= probTransition) {
            currentCell.setState(BURNING);
            System.out.println("Neighbor burnt me!");
        }
        else if (dub <= probTransition + probLightning * probCatch) {
            currentCell.setState(BURNING);
            System.out.println("Lightning!");
        }
    }

    private Cell[][] getGridCopy() {
        Cell[][] grid = getGrid();
        int size = grid.length;
        Cell[][] gridCopy = new Cell[size][size];
        for(int k = 0; k<size; k++) {
            Cell[] row = grid[k];
            System.arraycopy(row, 0, gridCopy[k], 0, size);
        }
        return gridCopy;
    }
}
