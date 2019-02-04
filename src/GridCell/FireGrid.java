package GridCell;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.HashMap;
import java.util.Map;

/**
 * A Grid subclass that represents a wildfire spreading through a forest.
 * The simulation can have explicit configured positions for all states, or it can have a random composition
 * Based on the Duke CS project.
 * @see <a href="https://www2.cs.duke.edu/courses/spring19/compsci308/assign/02_cellsociety/nifty/shiflet-fire/">Spreading of Fire</a>
 * @author Hunter Gregory
 */
public class FireGrid extends Grid {
    public static final int EMPTY = 0;
    public static final int BURNING = 1;
    public static final int GREEN = 2;

    private double probCatch;
    private double probLightning;
    private double probGrow;

    public FireGrid(int gridSize, double screenSize, Double[] randomComp) {
        super(gridSize, screenSize);
        setGridRandom(randomComp);
    }

    @Override
    public void initStateColorMap() {
        HashMap<Integer, Color> map = new HashMap<>();
        map.put(EMPTY, Color.YELLOW);
        map.put(BURNING, Color.RED);
        map.put(GREEN, Color.GREEN);
        setStateColorMap(map);
    }

    @Override
    public void setAdditionalParams(Double[] params) {
        probCatch = params[0];
        probLightning = params[1];
        probGrow = params[2];
    }

    @Override
    public void updateCells() {
        Cell[][] gridCopy = new Cell[getGrid().length][getGrid()[0].length];
    }
}
