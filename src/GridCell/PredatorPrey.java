package GridCell;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Connor Ghazaleh
 */

public class PredatorPrey extends Grid {
    private double fishBreedingAge;
    private double sharkBreedingAge;
    private double energyPerFish;
    private double sharkEnergy;
    private HashMap<Integer, Color> stateColorMap;

    public PredatorPrey(int gridSize, double screenSize, Double[] composition){
        super(gridSize,screenSize);
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
    public void initSliderMap(){

    }

    @Override
    public void setAdditionalParams(Double[] params){
        fishBreedingAge = params[0];
        sharkBreedingAge = params[1];
        sharkEnergy = 5;//params[2];
        energyPerFish = 2;//params[3];
        Cell[][] currentGrid = getGrid();
        for (Cell[] row : currentGrid){
            for (Cell cell : row){
                cell.setEnergy(sharkEnergy);
            }
        }
        setGrid(currentGrid);

    }

    @Override
    public void updateCells(){
        Cell[][] currentGrid = getGrid();
        ArrayList<Integer[]> sharks = findCellsWithState(currentGrid,2);
        ArrayList<Integer[]> fish = findCellsWithState(currentGrid,1);

        for (Integer[] fishy : fish) {
            ArrayList<Integer[]> neighbors = getNeighbors(fishy[0],fishy[1],false);
            ArrayList<Integer[]> emptyNeighbors = new ArrayList<Integer[]>();
            //try to reproduce
            reproduce(fishy, currentGrid, emptyNeighbors, neighbors);

            //try to move
            if (!emptyNeighbors.isEmpty()){
                Integer[] emptyCell = emptyNeighbors.get(0);
                switchSpots(currentGrid,emptyCell[0],emptyCell[1],1,fishy[0],fishy[1],0);
            }
            currentGrid[fishy[0]][fishy[1]].setAge(currentGrid[fishy[0]][fishy[1]].getAge()+1);
        }
        for (Integer[] shark : sharks) {
            ArrayList<Integer[]> neighbors = getNeighbors(shark[0],shark[1],false);
            ArrayList<Integer[]> emptyNeighbors = new ArrayList<Integer[]>();
            double sharkEnergy = currentGrid[shark[0]][shark[1]].getEnergy();
            //if not enough energy, expire
            if (sharkEnergy <= 0 && currentGrid[shark[0]][shark[1]].getState() == 2){
                currentGrid[shark[0]][shark[1]].setState(0);
                currentGrid[shark[0]][shark[1]].setColor(stateColorMap.get(0));
                currentGrid[shark[0]][shark[1]].setEnergy(sharkEnergy);
                currentGrid[shark[0]][shark[1]].setAge(0);
            }else {
                //decrease energy by one
                currentGrid[shark[0]][shark[1]].setEnergy(currentGrid[shark[0]][shark[1]].getEnergy()-1);
            }
            //try to reproduce
            reproduce(shark, currentGrid, emptyNeighbors, neighbors);

            //try to eat
            boolean foundFish = false;
            for (Integer[] neighbor : neighbors){
                if (currentGrid[neighbor[0]][neighbor[1]].getState() == 1){
                    //eat fish and replenish energy
                    switchSpots(currentGrid,neighbor[0],neighbor[1],2,shark[0],shark[1],0);
                    currentGrid[shark[0]][shark[1]].setEnergy(currentGrid[shark[0]][shark[1]].getEnergy()+energyPerFish);
                    foundFish = true;
                }else if (currentGrid[neighbor[0]][neighbor[1]].getState() == 0){
                    emptyNeighbors.add(neighbor);
                }
            }
            //set new age and move
            if (!foundFish){
                currentGrid[shark[0]][shark[1]].setEnergy(currentGrid[shark[0]][shark[1]].getEnergy()-1);
                if (!emptyNeighbors.isEmpty()){
                    Integer[] emptyCell = emptyNeighbors.get(0);
                    switchSpots(currentGrid,emptyCell[0],emptyCell[1],2,shark[0],shark[1],0);
                }
                if (currentGrid[shark[0]][shark[1]].getEnergy() == 0){
                    //currentGrid[shark[0]][shark[1]].setEnergy();
                }
            }
            currentGrid[shark[0]][shark[1]].setAge(currentGrid[shark[0]][shark[1]].getAge()+1);


        }
        setGrid(currentGrid);
    }

    private ArrayList<Integer[]> findCellsWithState(Cell[][] grid, int state){
        ArrayList<Integer[]> cells = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                int myState = grid[i][j].getState();
                if (myState == state) {
                    Integer[] cell = {i, j, myState};
                    cells.add(cell);
                }
            }
        }
        return cells;
    }

    private void reproduce(Integer[] fishy, Cell[][] currentGrid, ArrayList<Integer[]> emptyNeighbors, ArrayList<Integer[]> neighbors){
        if (currentGrid[fishy[0]][fishy[1]].getAge() >= fishBreedingAge){
            for (Integer[] neighbor : neighbors){
                if (currentGrid[neighbor[0]][neighbor[1]].getState() == 0){
                    emptyNeighbors.add(neighbor);
                }
            }
            if (!emptyNeighbors.isEmpty()){
                Integer[] spawnLocation = emptyNeighbors.get(0);
                currentGrid[spawnLocation[0]][spawnLocation[1]].setState(2);
                currentGrid[spawnLocation[0]][spawnLocation[1]].setColor(stateColorMap.get(2));
                currentGrid[spawnLocation[0]][spawnLocation[1]].setAge(0);
                currentGrid[fishy[0]][fishy[1]].setAge(0);
            }
        }
    }

    private void switchSpots(Cell[][] grid, int y1, int x1, int state1, int y2, int x2, int state2){
        grid[y1][x1].setState(state1);
        grid[y1][x1].setColor(stateColorMap.get(state1));
        grid[y2][x2].setState(state2);
        grid[y2][x2].setColor(stateColorMap.get(state2));
    }
}

