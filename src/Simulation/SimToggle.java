package Simulation;

import javafx.scene.control.ToggleButton;

/**
 * This class extends the JavaFx ToggleButton class and constructs ToggleButton objects with predefined styles and
 * effects.
 * @author Dhanush Madabusi
 */
class SimToggle extends ToggleButton {
    public SimToggle(String name, boolean isSelected, double xPos, double yPos){
        super(name);
        this.setSelected(isSelected);
        this.setLayoutX(xPos);
        this.setLayoutY(yPos);
    }
}
