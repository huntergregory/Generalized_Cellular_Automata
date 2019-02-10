package XML;
import GridCell.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public enum CA_TYPE {
    GAME_OF_LIFE("data/schemas/game-of-life.xsd", LifeGrid.class),
    FIRE("data/schemas/fire.xsd", FireGrid.class),
    //PERCOLATION("data/schemas/percolation.xsd", PercolationGrid.class),
    PREDATOR_PREY("data/schemas/predator-prey.xsd", PredatorPrey.class),
    SEGREGATION("data/schemas/segregation.xsd", Segregation.class);

    private File mySchemaFile;
    private Class<? extends Grid> myGridClass;
    CA_TYPE(String schemaFile, Class<? extends Grid> gridClass) {
        mySchemaFile = new File(schemaFile);
        myGridClass = gridClass;
    }

    /**
     * @return String representation of schema file associated with this type
     */
    public File getSchemaFile() { return mySchemaFile; }

    /**
     * Uses reflection to return the constructor associated with a Grid subclass.
     * Use newInstance(int, double, CELL_SHAPE, int[]) to invoke the constructor.
     * @return Grid constructor or null if an error occurred.
     */
    public Constructor<? extends Grid> getConstructor() {
        try {
            return myGridClass.getConstructor(int.class, double.class, CELL_SHAPE.class, Integer[].class);
        }
        catch (NoSuchMethodException e) {
            //should never happen
            return null;
        }
    }
}