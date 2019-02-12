package GridCell;

import java.util.ArrayList;
import java.util.Arrays;

public enum CELL_SHAPE {
    SQUARE(
          new Integer[]{-1, -1, 0, 1, 1, 1, 0, -1},
          new Integer[]{0, 1, 1, 1, 0, -1, -1, -1}
          ) {
        @Override
        public String toString() {
            return "square";
        }
    },
    TRIANGLE(
          new Integer[]{-1, -1, -1, 0, 0, 1, 1,  1,  0,  0, -1, -1},
          new Integer[]{ 0,  1,  2, 2, 1, 1, 0, -1, -1, -2, -2, -1}
          ) {
        @Override
        public String toString() {
            return "triangle";
        }
    },
    HEXAGON(
//                       U UR  R DR  D DL  L UL
          new Integer[]{-1,-1, 0, 1, 1, 1, 0,-1},
          new Integer[]{ 0, 1, 1, 1, 0,-1,-1,-1}
          ) {
        @Override
        public String toString() {
            return "hexagon";
        }
    };

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
    public Integer[] getDeltaC(int row, int col, Integer[] selectedNeighbors) {
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
    public Integer[] getDeltaR(int row, int col, Integer[] selectedNeighbors) {
        return getDelta(row, col, selectedNeighbors, myDeltaR);
    }

    private Integer[] getDelta(int row, int col, Integer[] selectedNeighbors, Integer[] fullDelta) {
        if (this.equals(CELL_SHAPE.TRIANGLE) && (row + col) % 2 == 0)
            flipSign(fullDelta);
        if (this.equals(CELL_SHAPE.HEXAGON)){
            if (row % 2 == 0){
                fullDelta = copyWithoutIndices(3,5,fullDelta);
            }else{
                fullDelta = copyWithoutIndices(1,7,fullDelta);
            }
        }
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

    private Integer[] copyWithoutIndices(int ind1, int ind2, Integer[] array){
        Integer[] result = new Integer[array.length-2];
        int difference = 0;
        for (int i = 0; i < array.length; i++){
            //System.out.println("Index: "+index);
            if (!(i == ind1 || i == ind2)){
                result[i-difference] = array[i];
            }else{
                difference++;
            }
        }
        return result;
    }
}
