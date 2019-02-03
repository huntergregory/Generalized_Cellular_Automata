import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class Segregation extends Grid {

    private double happyPercent;
    private HashMap<Integer,Color> stateColorMap;

    public Segregation(int gridSize, double screenSize){
        super(gridSize,screenSize);
    }

    public void setGridStates(double[] composition){
        setGridRandom(composition);
    }

    @Override
    public void initStateColorMap(){
        HashMap<Integer, Color > colorMap = new HashMap<Integer,Color>();
        colorMap.put(0,Color.WHITE);
        colorMap.put(1,Color.RED);
        colorMap.put(2,Color.BLUE);
        stateColorMap = colorMap;
        setStateColorMap(colorMap);
    }

    @Override
    public void setAdditionalParams(double[] params){
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
            Integer[] person = unhappy.get(0);
            Integer[] emptySpot = empty.get(0);
            currentGrid[emptySpot[0]][emptySpot[1]].setState(person[2]);
            Integer[] vacatedSpot = {person[0],person[1]};
            empty.add(vacatedSpot);
        }
        setGrid(currentGrid);
    }


}
