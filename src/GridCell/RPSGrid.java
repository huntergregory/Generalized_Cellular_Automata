package GridCell;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Rocks Paper Scissors Automaton. Non-empty states grow outwards until they reach max gradient level.
 * During each simulation step, cells are picked at random in the grid, and each selected cell chooses
 * a random neighbor. If the current cell is empty and it chooses a non-empty cell with gradient less than max,
 * the empty cell is replaced by that cell and given the other cell's gradient level plus one. A colored cell
 * will also overtake an empty cell and increase its age if it selects one. Each cell's choice affects the others
 * within a simulation step, e.g. a rock may move onto a nearby scissor that moved onto a paper right next to the rock,
 * all in one simulation step.
 * Internally extendable to more than 3 states by simply updating the private canEatOther method and the color map.
 * 'Age' corresponds to 'gradient' for cells in this sim.
 * @author Hunter Gregory
 */
public class RPSGrid extends Grid {
    private static final int EMPTY = 0;
    private static final int ROCK = 1;
    private static final int PAPER = 2;
    private static final int SCISSORS = 3;

    private int myMaxGradient;

    /**
     * Create a Rocks Paper Scissors Grid
     * @param gridSize
     * @param screenSize
     */
    public RPSGrid(int gridSize, double screenSize) {
        super(gridSize, screenSize);
    }

    @Override
    public void initStateColorMap() {
        HashMap<Integer, Color> colorMap = new HashMap<>();
        colorMap.put(EMPTY,Color.WHITE);
        colorMap.put(ROCK,Color.RED);
        colorMap.put(PAPER, Color.GREEN);
        colorMap.put(SCISSORS,Color.BLUE);
        setStateColorMap(colorMap);
    }

    @Override
    public void initSliderMap() {

    }

    @Override
    public void setAdditionalParams(Double[] params) {
        myMaxGradient = (int) Math.round(params[0]);
    }

    @Override
    public void updateCells() {
        Cell[][] newGrid = getGrid();
        int width = newGrid.length;
        int height = newGrid[0].length;

        ArrayList<Integer[]> positions = getAllPositions(width, height);
        for (int r=0; r<width; r++) {
            for (int c=0; c<height; c++){
                Integer[] coord = getRandomPosition(positions);
                Cell currentCell = newGrid[coord[0]][coord[1]];
                ArrayList<Integer[]> neighbors = getNeighbors(coord[0], coord[1]);
                Integer[] randomNeighbor = neighbors.get(getRandomInt(neighbors.size()));
                Cell neighborCell = newGrid[randomNeighbor[0]][randomNeighbor[1]];
                //System.out.printf("old cell at row %d, col %d, and state %d ", coord[0], coord[1], currentCell.getState());
                //System.out.printf("chose new cell at %d, %d, and oldstate %d but new state %d\n", randomNeighbor[ROW_INDEX], randomNeighbor[COL_INDEX],
                //              randomNeighbor[STATE_INDEX], newGrid[randomNeighbor[ROW_INDEX]][randomNeighbor[COL_INDEX]].getState());

                updateEmptyCell(currentCell, neighborCell);
                updateNonEmptyCell(currentCell, neighborCell);

            }
        }
        setGrid(newGrid);
    }

    private void updateEmptyCell(Cell emptyCell, Cell nonEmptyCell) {
        if (shouldReplace(emptyCell, nonEmptyCell)) {
            replaceState(emptyCell, nonEmptyCell.getState(), nonEmptyCell.getState() + 1);
            System.out.println("colorwashed!");
        }
    }

    private boolean shouldReplace(Cell emptyCell, Cell nonEmptyCell) {
        return emptyCell.getState() == EMPTY
                && nonEmptyCell.getState() != EMPTY
                && nonEmptyCell.getAge() < myMaxGradient - 1;
    }

    private void updateNonEmptyCell(Cell cell, Cell neighbor) {
        if (shouldReplace(neighbor, cell)) {
            replaceState(neighbor, cell.getState(), cell.getState() + 1);
            System.out.println("spread my color!");
        }
        else if (canEatOther(cell, neighbor)) {
            replaceState(neighbor, cell.getState(), 0);
            System.out.println("munchin!");
        }
    }

    private ArrayList<Integer[]> getAllPositions(int width, int height) {
        ArrayList<Integer[]> result = new ArrayList<>();
        for (int r=0; r<width; r++) {
            for (int c=0; c< height; c++) {
                result.add(new Integer[]{r,c});
            }
        }
        return result;
    }

    private Integer[] getRandomPosition(ArrayList<Integer[]> positions) {
        int randomIndex = getRandomInt(positions.size());
        Integer[] coords = positions.get(randomIndex);
        positions.remove(randomIndex);
        return coords;
    }

    private void replaceState(Cell cell, int newState, int newAge) {
        cell.setState(newState);
        cell.setColor(getStateColorMap().get(newState));
        cell.setAge(newAge);
    }

    private boolean canEatOther(Cell cell, Cell other) {
        int currentState = cell.getState();
        int otherState = other.getState();
        return currentState == ROCK && otherState == SCISSORS ||
                currentState == SCISSORS && otherState == PAPER ||
                currentState == PAPER && otherState == ROCK;
    }

    /*

        if (currentCell.getState() == EMPTY && oldCell.getAge() < myMaxGradient - 1) {
            System.out.println("trying to move to old white space");
            if (newCell.getState() != EMPTY) { //deal with case where states move to the same spot simultaneously
                if (canEatOther(oldCell.getState(), newCell.getState())) {
                    replaceState(newCell, oldCell.getState(), 0); //oldCell eats enemy
                    System.out.println("got eaten...");
                }
                else {
                    newCell.setAge(0); // enemy eats oldCell
                    System.out.println("eating enemy");
                }
            }
            else {
                System.out.println("no conflict");
                replaceState(newCell, oldCell.getState(), oldCell.getAge() + 1); //oldCell expands to empty cell
            }
        }
        else if (canEatOther(oldCell.getState(), selectedOriginalState)) {
            replaceState(newCell, oldCell.getState(), 0); //oldCell eats enemy
            System.out.println("eating enemy");
        }*/
}
