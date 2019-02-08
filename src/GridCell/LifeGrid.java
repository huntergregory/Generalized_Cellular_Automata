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

    private HashMap<Integer, Color> myStateColorMap;

    /**
     * Create a LifeGrid
     * @param gridSize
     * @param screenSize
     * @param cellShape
     * @param neigborConfig
     */
    public LifeGrid(int gridSize, double screenSize, CELL_SHAPE cellShape, int[] neigborConfig) {
        super(gridSize, screenSize, cellShape, neigborConfig);
    }

    @Override
    public void initStateColorMap() {
        myStateColorMap = new HashMap<>();
        myStateColorMap.put(EMPTY, Color.GREY);
        myStateColorMap.put(POPULATED, Color.YELLOW);
        setStateColorMap(myStateColorMap);
    }

    @Override
    public void initSliderMap(){

    }

    @Override
    public void setAdditionalParams(Double[] params) {
        //no parameters for this game
    }

    @Override
    public void updateCells() {
        Cell[][] newGrid = getGrid(); // this method returns a copy of the grid, so these two variables reference different cells
        Cell[][] oldGrid = getGrid();

        int size = oldGrid.length;
        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                updateCell(oldGrid[r][c], newGrid[r][c], getNeighbors(r, c));
                //System.out.println("for " + r + " and col " + c);
            }
        }
        setGrid(newGrid);
    }

    private void updateCell(Cell oldCell, Cell newCell, ArrayList<Integer[]> neighborCoords) {
        int numPopulatedNeighbors = 0;
        for (Integer[] neighborCoord : neighborCoords) {
            if (neighborCoord[2] == POPULATED) //gets the state from the (row, col, state) coordinate
                numPopulatedNeighbors ++;
        }

        if (oldCell.getState() == EMPTY && numPopulatedNeighbors == 3) {
            setCellState(newCell, POPULATED);
            System.out.println("populating the empty spot ");
        }
        else if (numPopulatedNeighbors <=1 || numPopulatedNeighbors >=4) {
            setCellState(newCell, EMPTY);
            if (oldCell.getState() == POPULATED) System.out.println("emptying the populated spot ");
        }
    }

    private void setCellState(Cell cell, int state) {
        cell.setState(state);
        cell.setColor(myStateColorMap.get(state));
    }
}
