package Simulation;

import javafx.scene.control.Slider;

/**
 * This class extends the JavaFx Slider class and constructs Slider objects with predefined styles and effects.
 * @author Dhanush Madabusi
 */
class SimSlider extends Slider {
    private static final double SLIDER_WIDTH = 175.0;

    SimSlider(int startVal, int endVal, int initialVal){
        super(startVal, endVal, initialVal);
        this.setMajorTickUnit(1);
        this.setSnapToTicks(true);
        this.setMaxWidth(SLIDER_WIDTH);
    }
}
