package GridCell;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

/**
 * @author Connor Ghazaleh
 */
public class RectangleCell extends Cell{
    private Rectangle cellBody;
    private Color cellColor;
    private int state;
    private int age;
    private double energy;

    public RectangleCell(double xPos, double yPos, double size){
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

    public int getState() {
        return state;
    }

    public String toString(){
        return String.format("Cell state: %d",state);
    }

    public void rotateAroundCenter(double angle){
        cellBody.getTransforms().add(new Rotate(angle,cellBody.getBoundsInLocal().getCenterX(), cellBody.getBoundsInLocal().getCenterY()));
    }
    public void setState(int State) {
        state = State;
    }

    public int getAge(){
        return age;
    }

    public void setAge(int newAge){
        age = newAge;
    }

    public double getEnergy(){
        return energy;
    }

    public void setEnergy(double newEnergy){
        energy = newEnergy;
    }



}
