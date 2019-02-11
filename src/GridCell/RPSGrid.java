package GridCell;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

public class RPSGrid extends Grid {
    public static final int EMPTY = 0;
    public static final int ROCK = 1;
    public static final int PAPER = 2;
    public static final int SCISSORS = 3;

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
    /*
    FIXXX should check to see if a cell is eating and resetting gradient if moving on top of another
    one of its states.
    Also another problem with this way of choosing states
     */
    public void updateCells() {
        Cell[][] oldGrid = getGrid();
        Cell[][] newGrid = getGrid();

        for (int r=0; r<oldGrid.length; r++) {
            for (int c=0; c<oldGrid[0].length; c++) {
                Cell oldCell = oldGrid[r][c];
                if (oldCell.getState() == EMPTY)
                    continue;

                ArrayList<Integer[]> neighbors = getNeighbors(r,c);
                Integer[] selectedNeighbor = neighbors.get(getRandomInt(neighbors.size()));
                int selectedOriginalState = selectedNeighbor[STATE_INDEX];
                Cell newCell = newGrid[selectedNeighbor[ROW_INDEX]][selectedNeighbor[COL_INDEX]];
                System.out.printf("old cell at row %d, col %d, and state %d ", r, c, oldCell.getState());
                System.out.printf("chose new cell at %d, %d, and oldstate %d but new state %d\n", selectedNeighbor[ROW_INDEX], selectedNeighbor[COL_INDEX],
                        selectedNeighbor[STATE_INDEX], newGrid[selectedNeighbor[ROW_INDEX]][selectedNeighbor[COL_INDEX]].getState());

                if (selectedOriginalState == EMPTY && oldCell.getAge() < myMaxGradient - 1) {
                    System.out.println("trying to move to old white space");
                    if (newCell.getState() != EMPTY) { //deal with case where states move to the same spot simultaneously
                        if (canEatState(oldCell.getState(), newCell.getState())) {
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
                else if (canEatState(oldCell.getState(), selectedOriginalState)) {
                    replaceState(newCell, oldCell.getState(), 0); //oldCell eats enemy
                    System.out.println("eating enemy");
                }
            }
        }
        setGrid(newGrid);
    }

    private void replaceState(Cell newCell, int newState, int newAge) {
        newCell.setState(newState);
        newCell.setColor(getStateColorMap().get(newState));
        newCell.setAge(newAge);
    }

    private boolean canEatState(int currentState, int otherState) {
        return currentState == ROCK && otherState == SCISSORS ||
                currentState == SCISSORS && otherState == PAPER ||
                currentState == PAPER && otherState == ROCK;
    }
}
