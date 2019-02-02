import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Grid {
    private Cell[][] grid;
    private HashMap<Integer, Color> stateColorMap;

    public Grid(int width, int height, double[] composition){
        //random case
        grid = new Cell[height][width];
        //for ()

    }

    public Grid(int width, int height, ArrayList<Integer[]> coordinates){
        //specific case
    }


    /**
     * Abstract method that will define algorithm for changing cell states. Will be defined explicitly in subclasses.
     */
    abstract void updateCells();

    /**
     * set grid randomly based on input composition (array of percentages)
     */
    public void setGridRandom(double[] composition){

    }

    /**
     * set grid specifically based on ArrayList of int[] arrays that are each of length 3 - last int[] specifies state for rest of grid
     */
    public void setGridSpecific(ArrayList<Integer[]> coordinates){
        int remainingState = -1;
        for (Integer[] point : coordinates){
            if (point[0] != -1) {
                grid[point[1]][point[0]].setColor(stateColorMap.get(point[2]));
                grid[point[1]][point[0]].setState(point[2]);
            }else{
                remainingState = point[3];
            }
        }
        for (Cell[] row : grid){
            for (Cell col: row){
                if (col.getState() == -1){
                    col.setColor(stateColorMap.get(remainingState));
                    col.setState(remainingState);
                }
            }
        }
    }


    public void setCellState(int row, int column){
        //grid[row][column].setColor();
    }


    public void changeGridSize(){

    }

    /**
     * Return the grid of cells so that it can interact with methods in other classes
     * @return
     */
    public Cell[][] getGrid(){
        return grid;
    }

}
