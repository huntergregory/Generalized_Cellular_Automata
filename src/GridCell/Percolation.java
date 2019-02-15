package GridCell;

import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 *
 * @author Dhanush Madabusi
 */
public class Percolation extends Grid{

    public Percolation(int gridSize, double screenSize){
        super(gridSize, screenSize);
    }

    @Override
    public void initStateColorMap() {
        HashMap<Integer, Color> colorMap = new HashMap<Integer,Color>();
        colorMap.put(0,Color.BLACK);
        colorMap.put(1,Color.WHITE);
        colorMap.put(2,Color.AQUAMARINE);
        setStateColorMap(colorMap);
    }

    @Override
    public void initSliderMap() {

    }

    @Override
    public void setAdditionalParams(Double[] params) {

    }

    @Override
    public void updateCells() {

    }

}
