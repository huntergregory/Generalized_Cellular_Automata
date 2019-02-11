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

    public TriangleCell(double xPos, double yPos, double size){
        super();
        cellSize = size;
        cellBody = new Polygon();
        vertices = new Double[]{
                xPos, yPos,
                xPos + size, yPos,
                xPos + size / 2, yPos + size / 2
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

//    /**
//     * Set size of cellBody
//     * @param size new size
//     */
//    public void setSize(double size){
//        cellBody.getPoints().setAll(new Double[]{
//                xPos,yPos,
//                xPos+cellSize,yPos,
//                xPos+cellSize/2,yPos+cellSize/2
//        });
//    }

    /**
     * @return copy of this Cell
     */
    public TriangleCell getCopy() {
        TriangleCell copiedCell = new TriangleCell(vertices[0], vertices[1], cellSize);
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


    public void rotateAroundCenter(double angle){
        cellBody.getTransforms().add(new Rotate(angle,cellBody.getBoundsInLocal().getCenterX(), cellBody.getBoundsInLocal().getCenterY()));
    }


}
