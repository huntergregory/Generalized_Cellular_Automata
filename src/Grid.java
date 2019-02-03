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
     * Abstract method called in the Grid constructor. Method must create a state-color map and call setStateColorMap.
     */
    abstract void initStateColorMap();


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
    abstract void setAdditionalParams();



    /**
     * Abstract method that will define algorithm for changing cell states. Will be defined explicitly in subclasses.
     */
    abstract void updateCells();

    /**
     * Set grid randomly based on input composition (array of percentages)
     * @param composition array of percentages associated with each state
     */
    public void setGridRandom(double[] composition){
        //make array of number of cells per state
        int[] stateCounts = calcCellsPerState(composition);
        //fill grid randomly
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[0].length; j++){
                boolean availableState = false;
                int index = random(4);
                while(!availableState){
                    if (stateCounts[index]>0){
                        availableState = true;
                    }else{
                        index = random(4);
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
    private int[] calcCellsPerState(double[] composition){
        int gridSize = grid.length*grid.length;
        int[] stateCounts = new int[composition.length+1];
        int sum = 0;
        for (int i = 0; i < composition.length; i++){
            int numCells = (int)(gridSize*composition[i]);
            stateCounts[i] = numCells;
            sum += numCells;
        }
        stateCounts[stateCounts.length-1] = gridSize - sum;
        return stateCounts;
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
    

    /**
     * method to resize the grid
     */
    abstract void changeGridSize();


    public ArrayList<Integer[]> getNeighbors(int row, int col){
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
        return neighbors;
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
     * @return
     */
    private int random(int bound){
        return rand.nextInt(bound);
    }

}
