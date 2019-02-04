package GridCell;

import javafx.scene.paint.Color;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Grid {
    private Cell[][] grid;
    private HashMap<Integer, Color> stateColorMap;
    private Random rand = new Random();
    private int gridSize;
    private double cellSize;


    /**
     * constructor
     * @param gridSize
     * @param screenSize
     */
    public Grid(int gridSize, double screenSize){
        grid = new Cell[gridSize][gridSize];
        initStateColorMap();
        this.gridSize = gridSize;
        cellSize = screenSize/gridSize;
    }


    /**
     * Abstract method called in the GridCell.Grid constructor. Method must create a state-color map and call setStateColorMap.
     */
    public abstract void initStateColorMap();


    /**
     * set color map that maps each state to a particular color
     * @param colorMap
     */
    public void setStateColorMap(HashMap<Integer, Color> colorMap){
        stateColorMap = colorMap;
    }


    /**
     * Abstract method to get any additional parameters required by specific simulation types
     */
    public abstract void setAdditionalParams(Double[] params);



    /**
     * Abstract method that will define algorithm for changing cell states. Will be defined explicitly in subclasses.
     */
    public abstract void updateCells();

    /**
     * Set grid randomly based on input composition (array of percentages)
     * @param composition array of percentages associated with each state
     */
    public void setGridRandom(Double[] composition){
        //make array of number of cells per state
        int[] stateCounts = calcCellsPerState(composition);
        //fill grid randomly
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[0].length; j++){
                boolean availableState = false;
                int index = getRandomInt(4);
                while(!availableState){
                    if (stateCounts[index]>0){
                        availableState = true;
                    }else{
                        index = getRandomInt(4);
                    }
                }
                grid[i][j] = new Cell(i*cellSize, j*cellSize, cellSize);
                setCellState(i,j,index);
                stateCounts[index]--;
            }
        }
    }



    /**
     * Make array of number of cells per state
     * @param composition array of percentages associated with each state
     * @return array of number of cells per state
     */
    private int[] calcCellsPerState(Double[] composition){
        int gridSize = grid.length*grid.length;
        int[] stateCounts = new int[composition.length];
        int sum = 0;
        int inferredState = -1;
        for (int i = 0; i < composition.length; i++){
            if (composition[i] != -1){
                int numCells = (int)(gridSize*composition[i]);
                stateCounts[i] = numCells;
                sum += numCells;
            }else{
                inferredState = i;
                stateCounts[i] = 0;
            }
        }
        if (inferredState == -1){
            sum -= stateCounts[stateCounts.length-1];
            stateCounts[stateCounts.length-1] = gridSize - sum;
        }else{
            stateCounts[inferredState] = gridSize - sum;
            return stateCounts;
        }
        
    }



    /**
     * set grid specifically based on ArrayList of int[] arrays that are each of length 3 - last int[] specifies state for rest of grid
     */
    public void setGridSpecific(ArrayList<Integer[]> coordinates){
        int remainingState = coordinates.get(coordinates.size()-1)[2];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = new Cell(i*cellSize, j*cellSize,cellSize);
                setCellState(i,j,remainingState);
            }
        }
        for (Integer[] point : coordinates){
            if (point[0] != -1) {
                setCellState(point[1],point[0],point[2]);
            }
        }
    }





    /**
     * Access cell at particular grid location and set its state
     * @param row
     * @param column
     * @param state
     */
    private void setCellState(int row, int column, int state){
        grid[row][column].setState(state);
        grid[row][column].setColor(stateColorMap.get(state));
    }
    


    public ArrayList<Integer[]> getNeighbors(int row, int col, boolean eightNeighbors){
        ArrayList<Integer[]> neighbors = new ArrayList<Integer[]>();
        if(row > 0){
            Integer[] neighbor = {row-1, col, grid[row-1][col].getState()};
            neighbors.add(neighbor);
        }
        if(row < grid.length-1){
            Integer[] neighbor = {row+1, col, grid[row+1][col].getState()};
            neighbors.add(neighbor);
        }
        if(col > 0){
            Integer[] neighbor = {row, col-1, grid[row][col-1].getState()};
            neighbors.add(neighbor);
        }
        if(col < grid.length-1){
            Integer[] neighbor = {row, col+1, grid[row][col+1].getState()};
            neighbors.add(neighbor);
        }
        if (eightNeighbors){
            if(row > 0 && col > 0){
                Integer[] neighbor = {row-1, col-1, grid[row-1][col-1].getState()};
                neighbors.add(neighbor);
            }
            if(row < grid.length-1 && col < grid.length-1){
                Integer[] neighbor = {row+1, col+1, grid[row+1][col+1].getState()};
                neighbors.add(neighbor);
            }
            if(row < grid.length-1 && col > 0){
                Integer[] neighbor = {row+1, col-1, grid[row+1][col-1].getState()};
                neighbors.add(neighbor);
            }
            if(row > 0 && col < grid.length-1){
                Integer[] neighbor = {row-1, col+1, grid[row-1][col+1].getState()};
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * Gets the neighborhood of a cell at specified position.
     * @param row
     * @param col
     * @return array of neighboring Cells
     * @throws IllegalArgumentException
     */
    Cell[] getNeighborCells(int row, int col, boolean eightNeighbors) throws IllegalArgumentException {
        if (!isInBounds(row,col))
            throw new IllegalArgumentException(String.format("(%d, %d) is not in the grid bounds", row,col));

        ArrayList<Cell> neighbors = new ArrayList<>();
        var deltaX = eightNeighbors ? new int[]{-1, -1, -1, 1, 1, 1, 0, 0} : new int[]{-1, 1, 0, 0};
        var deltaY = eightNeighbors ? new int[]{0, 1, -1, 0, 1, -1, 1, -1} : new int[]{0, 0, -1, 1};
        for (int k=0; k<deltaX.length; k++) {
            int neighborRow = row + deltaX[k];
            int neighborCol = col + deltaY[k];
            if (isInBounds(neighborRow, neighborCol))
                neighbors.add(grid[neighborRow][neighborCol]);
        }
        return neighbors.toArray(new Cell[0]);
    }


    private boolean isInBounds(int r, int c) {
        return r>=0 && r<gridSize && c>=0 && c<gridSize;
    }


    /**
     * Return the grid of cells so that it can interact with methods in other classes
     * @return
     */
    public Cell[][] getGrid(){
        return grid;
    }



    /**
     * Set grid to new grid passed as input
     * @param cells
     */
    public void setGrid(Cell[][] cells){
        grid = cells;
    }



    /**
     * Return in between 0 and bound-1 (inclusive)
     * @param bound
     * @return random integer
     */
    public int getRandomInt(int bound){
        return rand.nextInt(bound);
    }

    /**
     * Return a number picked from a uniform distribution between 0.0 and 1.0.
     * @return random double
     */
    public double getRandomDouble() {
        return rand.nextDouble();
    }

    /**
     * Creates a copy of the current grid
     * @return Cell grid copy
     */
    public Cell[][] getGridCopy() {
        Cell[][] grid = getGrid();
        int size = grid.length;
        Cell[][] gridCopy = new Cell[size][size];
        for(int k = 0; k<size; k++) {
            Cell[] row = grid[k];
            System.arraycopy(row, 0, gridCopy[k], 0, size);
        }
        return gridCopy;
    }

    /**
     * Prints the current grid's states. Useful for debugging
     */
    public void printGrid() {
        System.out.println("Printing Grid");
        for (int r=0; r<grid.length; r++) {
            for (int c=0; c<grid.length; c++) {
                System.out.print(grid[r][c].getState());
            }
            System.out.println();
        }
        System.out.println();
    }
}