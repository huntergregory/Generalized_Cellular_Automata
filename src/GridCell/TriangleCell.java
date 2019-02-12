package GridCell;

import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.Polygon;

/**
 * @author Connor Ghazaleh
 */
public class TriangleCell extends Cell{
    private Polygon cellBody;
    private Color cellColor;
    private double cellSize;
    private Double[] vertices;
    private boolean flippedCell;
    private double xPosition;
    private double yPosition;

    public TriangleCell(double xPos, double yPos, double size, boolean flipped){
        super();
        xPosition = xPos;
        yPosition = yPos;
        cellSize = size;
        flippedCell = flipped;
        cellBody = new Polygon();
        vertices = new Double[]{
                xPos, yPos,
                xPos + cellSize, yPos,
                xPos + cellSize *.5, yPos + cellSize * .75
        };
        if (flipped){
            vertices = new Double[]{
                    xPos, yPos + cellSize*.75,
                    xPos + cellSize, yPos + cellSize*.75,
                    xPos + cellSize * .5, yPos
            };
        }
        cellBody.getPoints().addAll(vertices);
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
        vertices = new Double[]{
                xPos,yPos,
                xPos+cellSize,yPos,
                xPos+cellSize/2,yPos+cellSize/2
        };
        cellBody.getPoints().setAll(vertices);
    }


    /**
     * @return copy of this Cell
     */
    public TriangleCell getCopy() {
        TriangleCell copiedCell = new TriangleCell(xPosition, yPosition, cellSize, flippedCell);
        copiedCell.setColor(cellColor);
        copiedCell.setAge(this.getAge());
        copiedCell.setState(this.getState());
        copiedCell.setEnergy(this.getEnergy());
        return copiedCell;
    }

    public Polygon getCellBody() {
        return cellBody;
    }

    /**
     * Return color of the cell
     * @return
     */
    public Color getColor(){
        return cellColor;
    }




}