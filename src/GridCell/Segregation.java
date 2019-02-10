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
     * @param cellShape
     * @param neighborConfig
     */
    public Segregation(int gridSize, double screenSize, CELL_SHAPE cellShape, Integer[] neighborConfig) {
        super(gridSize, screenSize, cellShape, neighborConfig);
    }

    public void setGridStates(Double[] composition){
        setGridRandom(composition);
    }

    @Override
    public void initStateColorMap(){
        HashMap<Integer, Color > colorMap = new HashMap<Integer,Color>();
        colorMap.put(0,Color.WHITE);
        colorMap.put(1,Color.RED);
        colorMap.put(2,Color.BLUE);
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
                if (myState!= 0){
                    ArrayList<Integer[]> neighbors = getNeighbors(i,j);
//                    System.out.println("<NEIGHBORS>");
//                    System.out.println("["+i+","+j+"] : ");
//                    for(Integer[] n : neighbors){
//                        System.out.println(n[0]+","+n[1]+","+n[2]);
//                    }
//                    System.out.println("<NEIGHBORS>");
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
                    //System.out.println("PCT SAME STATE: "+((double)numSameState)/((double)neighbors.size()));
                    if (numOccupied == 0){
                        Integer[] person = {i,j,myState};
                        unhappy.add(person);
                    }else if (((double)numSameState)/((double)numOccupied) < happyPercent){
                        Integer[] person = {i,j,myState};
                        unhappy.add(person);
                    }
                }else{
                    Integer[] location = {i,j};
                    empty.add(location);
                }
            }
        }
//        System.out.println("<UNHAPPY>");
//        for(Integer[] i : unhappy){
//            System.out.println(i[0]+","+i[1]);
//        }
//        System.out.println("<UNHAPPY>");

        //move unhappy cells
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
        setGrid(currentGrid);
    }



}

