package GridCell;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * @author Connor Ghazaleh
 */

public class Segregation extends Grid {

    private double happyPercent;

    /**
     * Create a Segregation
     * @param gridSize
     * @param screenSize
     */
    public Segregation(int gridSize, double screenSize) {
        super(gridSize, screenSize);
    }

    /**
     * Sets all states of grid and creates cell objects based on the percent of the grid that each state occupies
     * @param composition
     */
    public void setGridStates(Double[] composition){
        setGridRandom(composition);
    }

    /**
     * Initialize the map assigning colors to states
     */
    @Override
    public void initStateColorMap(){
        HashMap<Integer, Color > colorMap = new HashMap<Integer,Color>();
        colorMap.put(0,Color.WHITE);
        colorMap.put(1,Color.RED);
        colorMap.put(2,Color.BLUE);
        setStateColorMap(colorMap);
    }

    /**
     * initializes the sliders relevant to the simulation
     */
    @Override
    public void initSliderMap(){
        sliderMap = new LinkedHashMap<>();
        sliderMap.put("Similar", new Double[]{0.0, 0.1});
        sliderMap.put("Red/Blue", new Double[]{0.0, 0.1});
        sliderMap.put("Empty", new Double[]{0.0, 0.1});
    }

    /**
     * Set additional parameters specific to the current simulation
     * @param params array of doubles specifying parameter values
     */
    @Override
    public void setAdditionalParams(Double[] params){
        happyPercent = params[0];
    }

    /**
     * Updates properties of cells to run simulation
     */
    @Override
    public void updateCells(){
        Cell[][] currentGrid = getGrid();
        ArrayList<Integer[]> unhappy = new ArrayList<Integer[]>();
        ArrayList<Integer[]> empty = new ArrayList<Integer[]>();
        for (int i = 0; i < currentGrid.length; i++){
            for (int j = 0; j < currentGrid.length; j++){
                int myState = currentGrid[i][j].getState();
                determineHappiness(i, j, myState, unhappy, empty);
            }
        }
        moveUnhappyCells(unhappy,empty,currentGrid);
        setGrid(currentGrid);
    }

    /**
     * Determine if cells are happy and no longer want to move
     * @param i row of the current cell
     * @param j column of the current cell
     * @param myState state of the current cell
     * @param unhappy arraylist of all unhappy cells
     * @param empty arraylist of all empty cells
     */
    private void determineHappiness(int i, int j, int myState, ArrayList<Integer[]> unhappy, ArrayList<Integer[]> empty){
        if (myState!= 0){
            ArrayList<Integer[]> neighbors = getNeighbors(i,j);
            int numSameState = 0;
            int numOccupied = 0;
            for (Integer[] cell : neighbors){
                if (cell[2] == myState){
                    numSameState++;
                }
                if (cell[2] != 0){
                    numOccupied++;
                }
            }
            if (numOccupied == 0){
                unhappy.add(new Integer[]{i,j,myState});
            }else if (((double)numSameState)/((double)numOccupied) < happyPercent){
                unhappy.add(new Integer[]{i,j,myState});
            }
        }else{
            empty.add(new Integer[]{i,j});
        }
    }

    /**
     * Relocate cells that are unhappy to a random empty cell
     * @param unhappy arraylist of unhappy cells
     * @param empty arraylist of empty cells
     * @param currentGrid copy of the current grid that gets updated on each iteration replaces the main grid
     */
    private void moveUnhappyCells(ArrayList<Integer[]> unhappy, ArrayList<Integer[]> empty, Cell[][] currentGrid){
        while (!unhappy.isEmpty()){
            Random rand = new Random();
            //unhappy person and empty spot
            Integer[] person = unhappy.remove(0);
            int emptyIndex = rand.nextInt(empty.size());
            Integer[] emptySpot = empty.get(emptyIndex);
            empty.remove(emptyIndex);
            //switch random empty spot
            currentGrid[emptySpot[0]][emptySpot[1]].setState(person[2]);
            currentGrid[emptySpot[0]][emptySpot[1]].setColor(getStateColorMap().get(person[2]));
            currentGrid[person[0]][person[1]].setState(0);
            currentGrid[person[0]][person[1]].setColor(getStateColorMap().get(0));
            Integer[] vacatedSpot = {person[0],person[1]};
            empty.add(vacatedSpot);
        }
    }



}

