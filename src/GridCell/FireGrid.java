package GridCell;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.HashMap;
import java.util.Map;

/**
 * A Grid subclass that represents a wildfire spreading through a forest.
 * Based on the Duke CS project.
 * @see <a href="https://www2.cs.duke.edu/courses/spring19/compsci308/assign/02_cellsociety/nifty/shiflet-fire/">Spreading of Fire</a>
 * @author Hunter Gregory
 */
public class FireGrid extends Grid {
    public static final Integer EMPTY = 0;
    public static final Integer BURNING = 1;
    public static final Integer GREEN = 2;

    private double probCatch;

    public FireGrid(int gridSize, double screenSize, double probCatch) {
        super(gridSize, screenSize);
        this.probCatch = probCatch;
    }

    @Override
    public void setAdditionalParams(Double[] params) {

    }

    @Override
    void initStateColorMap() {
        HashMap<Integer, Color> map = new HashMap<>();
        map.put(EMPTY, Color.YELLOW);
        map.put(BURNING, Color.RED);
        map.put(GREEN, Color.GREEN);
        setStateColorMap(map);
    }

    @Override
    void setAdditionalParams() {
        //FIX or delete??
    }

    @Override
    void updateCells() {
        Cell[][] gridCopy = new Cell[getGrid().length][getGrid()[0].length];
    }

    @Override
    void changeGridSize() {
        //FIX
    }
}
