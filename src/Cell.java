import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class Cell {
    private Rectangle cellBody;
    private Color cellColor;
    private int state;

    public Cell(double xPos, double yPos, double size, Color color, int State){
        cellBody = new Rectangle(xPos,yPos,size, size);
        cellBody.setFill(color);
        cellColor = color;
        state = State;
    }

    /**
     * Set color of cellBody
     * @param color color to change the fill to
     */
    public void setColor(Color color){
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
     * Return color of the cell
     * @return
     */
    public Color getColor(){
        return cellColor;
    }

    public int getState() {
        return state;
    }

    public void setState(int State) {
        state = State;
    }

}
