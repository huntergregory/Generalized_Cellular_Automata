package GridCell;

import javafx.scene.paint.Color;

import java.lang.reflect.Array;
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
    private HashMap<Integer, Color> myStateColorMap;

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
        myStateColorMap = new HashMap<>();
        myStateColorMap.put(EMPTY, Color.YELLOW);
        myStateColorMap.put(GREEN, Color.GREEN);
        myStateColorMap.put(BURNING, Color.RED);
        setStateColorMap(myStateColorMap);

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
        Cell[][] newGrid = getGrid(); // this method returns a copy of the grid, so these two variables reference different cells
        Cell[][] oldGrid = getGrid();
        //System.out.println("new update");

        int size = oldGrid.length;
        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                Cell currentCell = oldGrid[r][c];
                Cell newCell = newGrid[r][c];
                if (currentCell.getState() == EMPTY) {
                    updateEmptyCell(newCell);
                    continue;
                }
                if (currentCell.getState() == BURNING) {
                    updateBurningCell(newCell);
                    continue;
                }
                updateGreenCell(newCell, getNeighbors(r,c,false));
            }
        }
        setGrid(newGrid);
    }

    private void updateEmptyCell(Cell newCell) {
        if (getRandomDouble() <= probGrow) {
            setCellState(newCell, GREEN);
            //System.out.println("Growing!");
        }
    }

    private void updateBurningCell(Cell newCell) {
        //System.out.println("age was " + newCell.getAge());
        newCell.setAge(1 + newCell.getAge());
        if (newCell.getAge() >= burnTime) {
            setCellState(newCell, EMPTY);
            newCell.setAge(0);
            //System.out.println("setting empty");
        }
    }

    private void updateGreenCell(Cell newCell, ArrayList<Integer[]> neighborCoords) {
        //double probTransition = probLightning * probCatch;
        double probTransition = 0;
        for (Integer[] neighborCoord : neighborCoords) {
            if (neighborCoord[2] == BURNING) { //gets the state from the (row, col, state) coordinate
                probTransition += probCatch;
            }
        }
        double dub = getRandomDouble();
        if (dub <= probTransition) {
            setCellState(newCell, BURNING);
            //System.out.println("Neighbor burnt me!");
        }
        else if (dub <= probTransition + probLightning * probCatch) {
            setCellState(newCell, BURNING);
            //System.out.println("Lightning!");
        }
    }

    private void setCellState(Cell cell, int state) {
        cell.setState(state);
        cell.setColor(myStateColorMap.get(state));
    }
}
