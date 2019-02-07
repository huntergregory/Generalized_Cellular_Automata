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
    public static final int EMPTY = 0;
    public static final int PERSON_A = 1;
    public static final int PERSON_B = 2;

    private double happyPercent;

    public Segregation(int gridSize, double screenSize) {
        super(gridSize,screenSize);
    }

    @Override
    public void initStateColorMap(){
        HashMap<Integer, Color > colorMap = new HashMap<Integer,Color>();
        colorMap.put(EMPTY,Color.WHITE);
        colorMap.put(PERSON_A,Color.RED);
        colorMap.put(PERSON_B,Color.BLUE);
        setStateColorMap(colorMap);
    }

    @Override
    public void initSliderMap(){
        sliderMap = new LinkedHashMap<>();
        sliderMap.put("Similar", new Double[]{0.0, 0.1});
        sliderMap.put("Red/Blue", new Double[]{0.0, 0.1});
        sliderMap.put("Empty", new Double[]{0.0, 0.1});
    }

    @Override
    public void setAdditionalParams(Double[] params){
        happyPercent = params[0];
    }

    @Override
    public void updateCells(){
        Cell[][] currentGrid = getGrid();
        //locate empty cells and unhappy cells
        ArrayList<Integer[]> unhappy = new ArrayList<Integer[]>();
        ArrayList<Integer[]> empty = new ArrayList<Integer[]>();
        for (int i = 0; i < currentGrid.length; i++){
            for (int j = 0; j < currentGrid.length; j++){
                int myState = currentGrid[i][j].getState();
                if (myState!= EMPTY){
                    ArrayList<Integer[]> neighbors = getNeighbors(i,j,true);
                    int numSameState = 0;
                    for (Integer[] cell : neighbors){
                        if (cell[2] == myState){
                            numSameState++;
                        }
                    }
                    if (numSameState/8.0 < happyPercent){
                        Integer[] person = {i,j,myState};
                        unhappy.add(person);
                    }
                }else{
                    Integer[] location = {i,j};
                    empty.add(location);
                }
            }
        }
        //move unhappy cells
        while (!unhappy.isEmpty()){
            Random rand = new Random();
            //unhappy person and empty spot
            Integer[] person = unhappy.remove(0);
            int emptyIndex = rand.nextInt(empty.size()-1);
            Integer[] emptySpot = empty.remove(emptyIndex);
            //switch random empty spot
            currentGrid[emptySpot[0]][emptySpot[1]].setState(person[2]);
            currentGrid[emptySpot[0]][emptySpot[1]].setColor(getStateColorMap().get(person[2]));
            currentGrid[person[0]][person[1]].setState(EMPTY);
            currentGrid[person[0]][person[1]].setColor(getStateColorMap().get(EMPTY));
            Integer[] vacatedSpot = {person[0],person[1]};
            empty.add(vacatedSpot);
        }
        setGrid(currentGrid);
    }



}

