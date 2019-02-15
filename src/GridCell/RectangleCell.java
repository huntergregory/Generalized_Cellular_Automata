package GridCell;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This is a type of cell that inherits from the cell class but differentiates itself by its rectangular shape representation on the screen. The method largely contains getter and setter methods for properties of this type of cell, however the constructor also contains logic and calculations that determine where to draw the cell on the screen when it is created.
 * @author Connor Ghazaleh
 */
public class RectangleCell extends Cell{
    private Rectangle cellBody;
    private Color cellColor;
    private int state;
    private int age;
    private double energy;

    RectangleCell(double xPos, double yPos, double size){
        super();
        cellBody = new Rectangle(xPos,yPos,size, size);
        cellBody.setStroke(Color.BLACK);
    }

    /**
     * Set color of cellBody
     * @param color color to change the fill to
     */
    public void setColor(Color color){
        cellColor = color;
        cellBody.setFill(color);
    }

    /**
     * Set position of cellBody on the screen
     * @param xPos new x position
     * @param yPos new y position
     */
    public void setPos(double xPos, double yPos){
        cellBody.setX(xPos);
        cellBody.setY(yPos);
    }

    /**
     * Set size of cellBody
     * @param size new size
     */
    public void setSize(double size){
        cellBody.setHeight(size);
        cellBody.setWidth(size);
    }

    /**
     * @return copy of this Cell
     */
    public RectangleCell getCopy() {
        RectangleCell copiedCell = new RectangleCell(cellBody.getX(), cellBody.getY(), cellBody.getHeight());
        copiedCell.setColor(cellColor);
        copiedCell.setAge(age);
        copiedCell.setState(state);
        copiedCell.setEnergy(energy);
        return copiedCell;
    }

    /**
     * Return the rectangle representing the body of the cell
     * @return
     */
    public Rectangle getCellBody() {
        return cellBody;
    }

    /**
     * Return color of the cell
     * @return
     */
    public Color getColor(){
        return cellColor;
    }

    /**
     * Return state of cell
     * @return
     */
    public int getState() {
        return state;
    }

    /**
     * Set string representation of cell
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
     * Return value of age property of cell
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
     * Return value of energy property of cell
     * @return
     */
    public double getEnergy(){
        return energy;
    }

    /**
     * Set value of energy property
     * @param newEnergy integer representing energy
     */
    public void setEnergy(double newEnergy){
        energy = newEnergy;
    }

    /**
     * Method to add border lines on cell body
     * @param addBorder boolean representing whether or not the feature should be on
     */
    @Override
    public void setCellBorder(boolean addBorder) {
        if (addBorder){
            cellBody.setStrokeWidth(1.0);
        }
        else{
            cellBody.setStrokeWidth(0.0);
        }
    }

}
