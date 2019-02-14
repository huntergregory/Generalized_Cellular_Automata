package GridCell;

import javafx.scene.paint.Color;

/**
 * @author Connor Ghazaleh
 */
public abstract class Cell {
    private Color cellColor;
    private int state;
    private int age;
    private double energy;

    public Cell(){
        age = 0;
        energy = 0;
    }

    /**
     * Set color of cellBody
     * @param color color to change the fill to
     */
    abstract void setColor(Color color);


    /**
     * @return copy of this Cell
     */
    abstract Cell getCopy();


    /**
     * Return color of the cell
     * @return
     */
     abstract Color getColor();

    /**
     * Return state of cell
     * @return
     */
    public int getState() {
        return state;
    }

    /**
     * Set string for cell (for printing mostly)
     * @return
     */
    public String toString(){
        return String.format("Cell state: %d",state);
    }

    /**
     * Set state of cell
     * @param State integer representing state
     */
    public void setState(int State) {
        state = State;
    }

    /**
     * Return age property of cell
     * @return
     */
    public int getAge(){
        return age;
    }

    /**
     * Set age property of cell
     * @param newAge integer representing age
     */
    public void setAge(int newAge){
        age = newAge;
    }

    /**
     * Return energy property of cell
     * @return
     */
    public double getEnergy(){
        return energy;
    }

    /**
     * Set energy property of cell
     * @param newEnergy integer representing energy
     */
    public void setEnergy(double newEnergy){
        energy = newEnergy;
    }

    public abstract void setCellBorder(boolean addBorder);

}
