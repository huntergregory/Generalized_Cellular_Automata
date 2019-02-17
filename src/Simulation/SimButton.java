package Simulation;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;

/**
 * This class extends the JavaFx button class and constructs Button objects with predefined styles and effects.
 * @author Dhanush Madabusi
 */
class SimButton extends Button{
    SimButton(String name, double width, double height){
        super(name);
        this.setPrefWidth(width);
        this.setPrefHeight(height);
        this.setFocusTraversable(false);
        this.setOnMouseEntered(mouseEvent -> this.setEffect(new DropShadow()));
        this.setOnMouseExited(mouseEvent -> this.setEffect(null));
        this.setStyle("-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), " +
                "radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%); " +
                "-fx-background-radius: 30; -fx-background-insets: 0,1,1; -fx-text-fill: black; -fx-font-size: 14;");
    }
}
