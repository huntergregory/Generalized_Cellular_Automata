package GridCell;

import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.LinkedHashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Super class that all other simulations will inherit from. This class defines all methods common to all simulations such as a method to get the neighbors of the current cell, as well as getters and setters for objects contained within the super class. This class also sets global variables that help to define input parameters to some of the methods and customize their behavior to for difference scenarios. It contains another important method to set all the immutable properties of a simulation that are related to the configuration of the simulation. The most important method in this class is the getNeighbors() method which defines which cells in the grid have the relationship "neighbor" to the current cell based on the shape of the cell.
 * @author Connor Ghazaleh
 */
public abstract class Grid {
    private static final String TOROIDAL_EDGE = "toroidal";
    private static final String NORMAL_EDGE = "normal";
    private static final String INFINITE_EDGE = "infinite";

    protected static final int ROW_INDEX = 0;
    protected static final int COL_INDEX = 1;
    protected static final int STATE_INDEX = 2;

    private Cell[][] grid;
    private HashMap<Integer, Color> stateColorMap;
    private Random rand = new Random();
    private int gridSize;
    private double cellSize;
    private final double screenSize;
    public static final double GRID_PADDING = 25.0;
    private Double[] curComposition;
    LinkedHashMap<String, Double[]> sliderMap;

    //Immutables
    private String edgeType;
    private CELL_SHAPE myCellShape;
    private Integer[] myNeighborConfig;

    /**
     * constructor
     * @param gridSize
     * @param screenSize
     */
    public Grid(int gridSize, double screenSize) {
        System.out.println("Called constructor on size: "+gridSize);
        System.out.println("Grid of size: " + gridSize);
        initStateColorMap();
        this.gridSize = gridSize;
        this.screenSize = screenSize;
        cellSize = screenSize/gridSize;
    }

    /**
     * Set values that should not change throughout the simulation
     * @param sim_edgeType
     * @param cellShape
     * @param neighborConfig
     */
    public void setImmutables(String sim_edgeType, CELL_SHAPE cellShape, Integer[] neighborConfig){
        edgeType = sim_edgeType;
        myCellShape = cellShape;
        switch(myCellShape){
            case HEXAGON:
                grid = new HexagonCell[gridSize][gridSize];
                break;
            case TRIANGLE:
                grid = new TriangleCell[gridSize][gridSize];
                break;
            default:
                grid = new RectangleCell[gridSize][gridSize];
        }
        myNeighborConfig = neighborConfig;
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
     * Return the map that assigns colors to states
     * @return
     */
    public HashMap<Integer, Color> getStateColorMap() {
        return stateColorMap;
    }


    /**
     * Returns cell shape
     * @return
     */
    public CELL_SHAPE getMyCellShape(){
        return myCellShape;
    }


    /**
     * Initialize the sliders for a simulation
     */
    public abstract void initSliderMap();


    /**
     * Return the map that holds the sliders for a simulation
     * @return
     */
    public LinkedHashMap<String, Double[]> getSliderMap(){
        return sliderMap;
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
        setCurrentComposition(composition);
        ArrayList<Integer> stateCounts = calcCellsPerState(composition);
        assignGridByStateCounts(stateCounts);
    }

    private void drawCells(int row, int col){
        if (myCellShape == CELL_SHAPE.SQUARE){
            grid[row][col] = new RectangleCell(col*cellSize + GRID_PADDING, row*cellSize + GRID_PADDING, cellSize);
        }
        if (myCellShape == CELL_SHAPE.TRIANGLE){
            //rotate based on row-col position
            boolean flip = false;
            if ((row+col) % 2 == 1){
                flip = true;
            }
            grid[row][col] = new TriangleCell(col*cellSize*.5 + GRID_PADDING, row*cellSize*.75 + GRID_PADDING, cellSize, flip);
        }
        if (myCellShape == CELL_SHAPE.HEXAGON){
            if (col % 2 == 0){
                grid[row][col] = new HexagonCell(col*cellSize*(2.0/3.0) + GRID_PADDING, row*cellSize + GRID_PADDING-cellSize/2, cellSize);
            }else{
                grid[row][col] = new HexagonCell(col*cellSize*(2.0/3.0) + GRID_PADDING, row*cellSize + GRID_PADDING, cellSize);
            }
        }
    }

    /**
     * Set grid randomly based on input composition (array of numbers)
     * @param composition array of numbers associated with each state
     */
    public void setGridRandomNum(Double[] composition){
        setCurrentComposition(composition);
        ArrayList<Integer> stateCounts = calcNumStatesFromStatesArray(composition);
        for (Integer dub : stateCounts) {
            System.out.println(dub);
        }
        assignGridByStateCounts(stateCounts);
    }

    private void assignGridByStateCounts(ArrayList<Integer> stateCounts) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                int index = getRandomInt(stateCounts.size());
                int state = stateCounts.get(index);
                stateCounts.remove(index);
                drawCells(row,col);
                setCellState(row,col,state);
            }
        }
    }

    private ArrayList<Integer> calcNumStatesFromStatesArray(Double[] composition){
        ArrayList<Integer> statesArrList = new ArrayList<>();
        int sum = 0;
        int index = -1;
        for (int i = 0; i < composition.length; i++){
            if (composition[i] == -1){
                index = i;
            }else{
                sum += composition[i];
            }
        }
        double inferred = gridSize*gridSize - sum;
        if (index == -1)
            composition[composition.length-1] += inferred;
        else
            composition[index] = inferred;
        for(int state = 0; state < composition.length; state++){
            for (int i = 0; i < composition[state]; i++){
                statesArrList.add(state);
            }
        }
        return statesArrList;
    }

    private void setCurrentComposition(Double[] composition) {
        double total = 0.0;
        for (int i = 0; i < composition.length - 1; i++){
            total += composition[i];
        }
        composition[composition.length - 1] = 1 - total;
        curComposition = composition;
    }

    /**
     * Return the current composition of the grid (in terms of states)
     * @return
     */
    public Double [] getCurComposition(){
        return curComposition;
    }

    private ArrayList<Integer> calcCellsPerState(Double[] composition){
        int gridSize = grid.length*grid.length;
        int[] stateCounts = fillStateCounts(composition,gridSize);
        ArrayList<Integer> allStates = new ArrayList<>();
        for (int index = 0; index < stateCounts.length; index++){
            for (int i = 0; i < stateCounts[index]; i++){
                allStates.add(index);
            }
        }
        return allStates;
    }

    private int[] fillStateCounts(Double[] composition, int gridSize){
        int sum = 0;
        int inferredState = -1;
        int[] stateCounts = new int[composition.length];
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
            sum -= stateCounts[0];
            stateCounts[0] = gridSize - sum;
        }else {
            stateCounts[inferredState] = gridSize - sum;
        }
        return stateCounts;
    }


    /**
     * set grid specifically based on ArrayList of int[] arrays that are each of length 3 - last int[] specifies state for rest of grid
     * @param coordinates
     */
    public void setGridSpecific(ArrayList<Integer[]> coordinates){
        int remainingState = coordinates.get(coordinates.size()-1)[2];
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                drawCells(row,col);
                setCellState(row,col,remainingState);
            }
        }
        for (Integer[] point : coordinates){
            if (point[0] != -1) {
                setCellState(point[0],point[1],point[2]);
            }
        }
    }


    private void setCellState(int row, int column, int state){
        grid[row][column].setState(state);
        grid[row][column].setColor(stateColorMap.get(state));
    }
    

    /**
     * Gets the neighborhood of a cell at specified position.
     * @param row
     * @param col
     * @return array of Integers of the form [row, col, state] for each neighboring cell
     * @throws IllegalArgumentException
     */
    protected ArrayList<Integer[]> getNeighbors(int row, int col) throws IllegalArgumentException {
        if (!isInBounds(row,col))
            throw new IllegalArgumentException(String.format("(%d, %d) is not in the grid bounds", row,col));
        Integer[] deltaR = myCellShape.getDeltaR(row, col, myNeighborConfig);
        Integer[] deltaC = myCellShape.getDeltaC(row, col, myNeighborConfig);
        ArrayList<Integer[]> neighbors = new ArrayList<>();
        for (int k=0; k<deltaR.length; k++) {
            int neighborRow = row + deltaR[k];
            int neighborCol = col + deltaC[k];
            if (edgeType.equals(TOROIDAL_EDGE)){
                int[] coordinates = wrapEdge(neighborCol,neighborRow);
                neighborRow = coordinates[0];
                neighborCol = coordinates[1];
            }
            if (isInBounds(neighborRow, neighborCol)) {
                Integer[] neighbor = {neighborRow, neighborCol, grid[neighborRow][neighborCol].getState()};
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }


    private int[] wrapEdge(int neighborCol, int neighborRow){
        if (neighborCol < 0){
            neighborCol = grid.length+neighborCol;
        }
        if (neighborRow < 0) {
            neighborRow = grid.length+neighborRow;
        }
        if (neighborCol >= 0 && neighborRow >= 0){
            neighborRow = neighborRow % grid.length;
            neighborCol = neighborCol % grid.length;
        }
        return new int[]{neighborRow,neighborCol};
    }


    private boolean isInBounds(int r, int c) {
        return r>=0 && r<gridSize && c>=0 && c<gridSize;
    }


    /**
     * Return a copy of the grid of cells so that it can interact with methods in other classes
     * @return grid of cells
     */
    public Cell[][] getGrid() {
        int size = grid.length;
        Cell[][] gridCopy = new Cell[size][size];
        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                gridCopy[r][c] = grid[r][c].getCopy();
            }
        }
        return gridCopy;
    }


    /**
     * Set grid to new grid passed as input
     * @param cells
     */
    public void setGrid(Cell[][] cells){
        grid = cells;
    }


    /**
     * Return size of grid
     * @return
     */
    public int getGridSize(){
        return gridSize;
    }

    /**
     * Set size of the grid
     * @param gridSize grid is always square so this param specifies 1 side length
     */
    public void setGridSize(int gridSize){
        this.gridSize = gridSize;
        cellSize = screenSize/gridSize;
        grid = new Cell[gridSize][gridSize];

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


    //used for debugging before the visualization was up and running
    protected void printGrid() {
        for (int r=0; r<grid.length; r++) {
            for (int c=0; c<grid.length; c++) {
                System.out.print(grid[r][c].getState());
            }
            System.out.println();
        }
        System.out.println();
    }
}