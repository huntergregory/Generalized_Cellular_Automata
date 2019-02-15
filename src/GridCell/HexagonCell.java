package GridCell;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * This is a type of cell that inherits from the cell class but differentiates itself by its hexagonal shape representation on the screen. The method largely contains getter and setter methods for properties of this type of cell, however the constructor also contains logic and calculations that determine where to draw the cell on the screen when it is created.
 * @author Connor Ghazaleh
 */
public class HexagonCell extends Cell{
    private Polygon cellBody;
    private Color cellColor;
    private double cellSize;
    private Double[] vertices;
    private boolean flippedCell;
    private double xPosition;
    private double yPosition;

    HexagonCell(double xPos, double yPos, double size){
        super();
        xPosition = xPos;
        yPosition = yPos;
        cellSize = size;
        cellBody = new Polygon();
        vertices = new Double[]{
                xPos+ cellSize/3, yPos,
                xPos+cellSize*2/3, yPos,
                xPos + cellSize, yPos+cellSize/2,
                xPos+cellSize*2/3, yPos+cellSize,
                xPos+ cellSize/3, yPos+cellSize,
                xPos, yPos+cellSize/2
        };
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
    public HexagonCell getCopy() {
        HexagonCell copiedCell = new HexagonCell(xPosition, yPosition, cellSize);
        copiedCell.setColor(cellColor);
        copiedCell.setAge(this.getAge());
        copiedCell.setState(this.getState());
        copiedCell.setEnergy(this.getEnergy());
        return copiedCell;
    }

    /**
     * Return the polygon representing the body of the cell
     * @return
     */
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
