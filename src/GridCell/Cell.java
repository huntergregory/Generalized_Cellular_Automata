package GridCell;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * Superclass that manages the cell objects that represent each grid element. The super class outlines the methods that the subclasses for different cell shapes will need. It also defines some common variables in the constructor such as the ones that track cell properties like age and energy.
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
     * Returns shape object that Cell class contains
     * @return shape object that Cell class contains
     */
    public abstract Shape getCellBody();

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

    /**
     * Method to set whether cell borders are drawn or not
     * @param addBorder boolean determining whether to set the borders or not
     */
    public abstract void setCellBorder(boolean addBorder);

}
