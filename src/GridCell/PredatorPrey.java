package GridCell;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;

/**
 * @author Connor Ghazaleh
 */

public class PredatorPrey extends Grid {
    public static final int EMPTY = 0;
    public static final int FISH = 1;
    public static final int SHARK = 2;

    private double fishBreedingAge;
    private double sharkBreedingAge;
    private double energyPerFish;
    private double sharkEnergy;
    private HashMap<Integer, Color> stateColorMap;

    /**
     * Create a PredatorPrey
     * @param gridSize
     * @param screenSize
     */
    public PredatorPrey(int gridSize, double screenSize) {
        super(gridSize, screenSize);
    }

    @Override
    public void initStateColorMap(){
        HashMap<Integer, Color > colorMap = new HashMap<Integer,Color>();
        colorMap.put(EMPTY,Color.WHITE); //empty
        colorMap.put(FISH,Color.GREEN); //fish
        colorMap.put(SHARK,Color.BLUE); //shark
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
        energyPerFish = params[2];
        sharkEnergy = params[3];
        Cell[][] currentGrid = getGrid();
        for (Cell[] row : currentGrid){
            for (Cell cell : row){
                if (cell.getState() == SHARK){
                    cell.setEnergy(sharkEnergy);
                }
            }
        }
        printEnergies(currentGrid);
        setGrid(currentGrid);
    }

    /**
     * Updates properties of cells to run simulation
     */
    @Override
    public void updateCells(){
        Cell[][] currentGrid = getGrid();
        updateEnergies(currentGrid);
        ArrayList<Integer[]> sharks = findCellsWithState(currentGrid,SHARK);
        for (Integer[] shark : sharks) {
            updateSharkProperties(currentGrid,shark);
        }
        ArrayList<Integer[]> fish = findCellsWithState(currentGrid,FISH);
        for (Integer[] fishy : fish) {
            updateFishProperties(currentGrid,fishy);
        }
        printEnergies(currentGrid);
        setGrid(currentGrid);
    }

    private void updateSharkProperties(Cell[][] currentGrid, Integer[] shark){
        ArrayList<Integer[]> neighbors = getNeighbors(shark[0],shark[1]);
        ArrayList<Integer[]> emptyNeighbors = new ArrayList<Integer[]>();
        findEmptyNeighbors(neighbors,emptyNeighbors,currentGrid);
        //try to reproduce
        reproduce(shark, currentGrid, emptyNeighbors, neighbors,SHARK,sharkBreedingAge);
        //try to eat
        feedMoveOrKillShark(neighbors,emptyNeighbors,currentGrid,shark);
    }

    private void updateFishProperties(Cell[][] currentGrid, Integer[] fishy){
        ArrayList<Integer[]> neighbors = getNeighbors(fishy[0],fishy[1]);
        ArrayList<Integer[]> emptyNeighbors = new ArrayList<Integer[]>();
        findEmptyNeighbors(neighbors,emptyNeighbors,currentGrid);
        //try to reproduce
        reproduce(fishy, currentGrid, emptyNeighbors, neighbors,FISH,fishBreedingAge);
        //try to move
        moveFish(emptyNeighbors,currentGrid,fishy);
    }

    private void moveFish(ArrayList<Integer[]> emptyNeighbors, Cell[][] currentGrid, Integer[] fishy){
        if (!emptyNeighbors.isEmpty()){
            Integer[] emptyCell = emptyNeighbors.get(0);
            switchSpots(currentGrid,emptyCell[0],emptyCell[1],FISH,fishy[0],fishy[1],EMPTY);
        }
        currentGrid[fishy[0]][fishy[1]].setAge(currentGrid[fishy[0]][fishy[1]].getAge()+1);
    }

    private void findEmptyNeighbors(ArrayList<Integer[]> neighbors, ArrayList<Integer[]> emptyNeighbors, Cell[][] currentGrid){
        for (Integer[] neighbor : neighbors){
            if (currentGrid[neighbor[0]][neighbor[1]].getState() == EMPTY) {
                emptyNeighbors.add(neighbor);
            }
        }
    }

    private void feedMoveOrKillShark(ArrayList<Integer[]> neighbors, ArrayList<Integer[]> emptyNeighbors, Cell[][] currentGrid, Integer[] shark){
        boolean foundFish = false;
        for (Integer[] neighbor : neighbors){
            if (currentGrid[neighbor[0]][neighbor[1]].getState() == FISH){
                //eat fish and replenish energy
                currentGrid[shark[0]][shark[1]].setEnergy(currentGrid[shark[0]][shark[1]].getEnergy()+energyPerFish);
                switchSpots(currentGrid,neighbor[0],neighbor[1],SHARK,shark[0],shark[1],EMPTY);
                foundFish = true;
            }else if (currentGrid[neighbor[0]][neighbor[1]].getState() == EMPTY){
                emptyNeighbors.add(neighbor);
            }
        }
        //set new age and move
        if (!foundFish){
            killOrMoveShark(currentGrid,shark,emptyNeighbors);
        }
        currentGrid[shark[0]][shark[1]].setAge(currentGrid[shark[0]][shark[1]].getAge()+1);
    }

    private void killOrMoveShark(Cell[][] currentGrid, Integer[] shark, ArrayList<Integer[]> emptyNeighbors){
        if (currentGrid[shark[0]][shark[1]].getEnergy() <= 0){
            currentGrid[shark[0]][shark[1]].setState(EMPTY);
            currentGrid[shark[0]][shark[1]].setColor(stateColorMap.get(EMPTY));
            currentGrid[shark[0]][shark[1]].setEnergy(0);
        }else {
            if (!emptyNeighbors.isEmpty()){
                Integer[] emptyCell = emptyNeighbors.get(0);
                switchSpots(currentGrid,emptyCell[0],emptyCell[1],SHARK,shark[0],shark[1],EMPTY);
            }
        }
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

    private void reproduce(Integer[] animal, Cell[][] currentGrid, ArrayList<Integer[]> emptyNeighbors, ArrayList<Integer[]> neighbors, int state, double breedingAge){
        if (currentGrid[animal[0]][animal[1]].getAge() >= breedingAge){
            for (Integer[] neighbor : neighbors){
                if (currentGrid[neighbor[0]][neighbor[1]].getState() == EMPTY){
                    emptyNeighbors.add(neighbor);
                }
            }
            if (!emptyNeighbors.isEmpty()){
                Integer[] spawnLocation = emptyNeighbors.get(0);
                currentGrid[spawnLocation[0]][spawnLocation[1]].setState(state);
                currentGrid[spawnLocation[0]][spawnLocation[1]].setColor(stateColorMap.get(state));
                currentGrid[spawnLocation[0]][spawnLocation[1]].setAge(0);
                currentGrid[animal[0]][animal[1]].setAge(0);
            }
        }
    }

    private void switchSpots(Cell[][] grid, int y1, int x1, int state1, int y2, int x2, int state2){
        grid[y1][x1].setState(state1);
        grid[y1][x1].setColor(stateColorMap.get(state1));
        grid[y2][x2].setState(state2);
        grid[y2][x2].setColor(stateColorMap.get(state2));
        //switch energies
        double cell1Energy = grid[y1][x1].getEnergy();
        double cell2Energy = grid[y2][x2].getEnergy();
        grid[y1][x1].setEnergy(cell2Energy);
        grid[y2][x2].setEnergy(cell1Energy);
    }

    private void updateEnergies(Cell[][] temp){
        for (Cell[] row : temp){
            for (Cell cell : row){
                if (cell.getState() == SHARK){
                    cell.setEnergy(cell.getEnergy()-1);
                }
            }
        }
    }
    private void printEnergies(Cell[][] temp){
        for (Cell[] row : temp){
            for (Cell cell : row){
                System.out.print(cell.getEnergy()+",");
            }
            System.out.println();
        }
        System.out.println();

    }
}

