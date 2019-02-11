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
                int selectedState = selectedNeighbor[STATE_INDEX];
                int selectedRow = selectedNeighbor[ROW_INDEX];
                int selectedCol = selectedNeighbor[COL_INDEX];
                if (selectedState == EMPTY && oldCell.getAge() < myMaxGradient - 1) {
                    replaceCell(newGrid, selectedRow, selectedCol, oldCell.getState());
                    newGrid[selectedRow][selectedCol].setAge(oldCell.getAge() + 1);
                }
                else if (canEatCell(oldCell.getState(), selectedState)) {
                    replaceCell(newGrid, selectedRow, selectedCol, oldCell.getState());
                    newGrid[selectedRow][selectedCol].setAge(0);
                }
            }
        }
    }

    private void replaceCell(Cell[][] newGrid, int r, int c, int state) {
        newGrid[r][c].setState(state);
        newGrid[r][c].setColor(getStateColorMap().get(state));
    }

    private boolean canEatCell(int oldState, int selectedState) {
        return oldState == ROCK && selectedState == SCISSORS ||
                oldState == SCISSORS && selectedState == PAPER ||
                oldState == PAPER && selectedState == ROCK;
    }
}
