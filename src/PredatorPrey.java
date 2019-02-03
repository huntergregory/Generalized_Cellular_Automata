import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

public class PredatorPrey extends Grid {
    private double fishBreedingAge;
    private double sharkLifeSpan;
    private HashMap<Integer, Color> stateColorMap;

    public PredatorPrey(int gridSize, double screenSize){
        super(gridSize,screenSize);
    }

    public void setGridStates(double[] composition){
        setGridRandom(composition);
    }

    @Override
    public void initStateColorMap(){
        HashMap<Integer, Color > colorMap = new HashMap<Integer,Color>();
        colorMap.put(0,Color.WHITE); //empty
        colorMap.put(1,Color.GREEN); //fish
        colorMap.put(2,Color.BLUE); //shark
        stateColorMap = colorMap;
        setStateColorMap(colorMap);
    }

    @Override
    public void setAdditionalParams(double[] params){
        fishBreedingAge = params[0];
        sharkLifeSpan = params[1];
    }

    @Override
    public void updateCells(){
        Cell[][] currentGrid = getGrid();

        setGrid(currentGrid);
    }
}
