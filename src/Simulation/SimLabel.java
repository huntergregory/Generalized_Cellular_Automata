package Simulation;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This class extends the JavaFx Text class and constructs Text objects with predefined styles and effects.
 * @author Dhanush Madabusi
 */
class SimLabel extends Text {
    SimLabel(String name, double fontSize){
        super();
        this.setText(name);
        this.setFont(new Font(fontSize));
    }

    SimLabel(String name, double fontSize, double xPos, double yPos){
        this(name, fontSize);
        this.setX(xPos);
        this.setY(yPos);
    }
}
