package GridCell;

import java.util.ArrayList;
import java.util.Arrays;

public enum CELL_SHAPE {
    SQUARE(
          new Integer[]{-1, -1, 0, 1, 1, 1, 0, -1},
          new Integer[]{0, 1, 1, 1, 0, -1, -1, -1}
          ),
    TRIANGLE(
          new Integer[]{-1, -1, 0, 0, 1, 1, 1, 1, 1, 0, 0, -1},
          new Integer[]{0, 1, 1, 2, 2, 1, 0, -1, -2, -2, -1, -1}
          ),
    HEXAGON(
          new Integer[]{},
          new Integer[]{}
          );

    private Integer[] myDeltaR;
    private Integer[] myDeltaC;
    CELL_SHAPE(Integer[] deltaR, Integer[] deltaC) {
        myDeltaR = deltaR;
        myDeltaC = deltaC;
    }

    /**
     * Lets the XML parser know how to check for errors for a selectedNeighbors int array
     * @return max number of neighbors
     */
    public int getMaxNumNeighbors() { return myDeltaC.length; }

    /**
     * Gets the ordered delta values to add to the current column to reach
     * the specified neighbors in clockwise order.
     * Assumes
     * @param row
     * @param col
     * @param selectedNeighbors
     * @return delta column values in an Integer array
     */
    public Integer[] getDeltaC(int row, int col, int[] selectedNeighbors) {
        return getDelta(row, col, selectedNeighbors, myDeltaC);

    }

    /**
     * Gets the ordered delta values to add to the current row to reach
     * the specified neighbors in clockwise order.
     * @param row
     * @param col
     * @param selectedNeighbors
     * @return delta row values in an Integer array
     */
    public Integer[] getDeltaR(int row, int col, int[] selectedNeighbors) {
        return getDelta(row, col, selectedNeighbors, myDeltaR);
    }

    private Integer[] getDelta(int row, int col, int[] selectedNeighbors, Integer[] fullDelta) {
        if (this.equals(CELL_SHAPE.TRIANGLE) && (row + col) % 2 == 1)
            flipSign(fullDelta);
        // [-1] means include max possible neighbors
        if (selectedNeighbors.length == 1 && selectedNeighbors[0] == -1)
            return fullDelta;

        Arrays.sort(selectedNeighbors); // in case the selected neighbors weren't in increasing order
        ArrayList<Integer> newDelta = new ArrayList<>();
        for (int index : selectedNeighbors) {
            newDelta.add(fullDelta[index]);
        }
        return newDelta.toArray(new Integer[0]);
    }

    private void flipSign(Integer[] array) {
        for (int k=0; k<array.length; k++) {
            array[k] *= -1;
        }
    }
}
