import javafx.scene.paint.Color;
import java.util.HashMap;

public abstract class Grid {
    private Cell[][] grid;
    private HashMap<Integer, Color> stateColorMap;

    public Grid(int size){
        grid = new Cell[size][size];
    }

    /**
     * Abstract method that will define algorithm for changing cell states. Will be defined explicitly in subclasses.
     */
    abstract void updateCells();

    /**
     * Return the grid of cells so that it can interact with methods in other classes
     * @return
     */
    public Cell[][] getGrid(){
        return grid;
    }

}
