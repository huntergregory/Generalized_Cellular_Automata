package XML;
import GridCell.Grid;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public enum CA_TYPE {
    FIRE("data/schemas/fire.xsd") { public String toString() { return "PercolationGrid"; } },
    PERCOLATION("data/schemas/percolation.xsd") { public String toString() { return "PercolationGrid"; } },
    GAME_OF_LIFE("data/schemas/game-of-life.xsd") { public String toString() { return "LifeGrid"; } },
    PREDATOR_PREY("data/schemas/predator-prey.xsd") { public String toString() { return "PredatorPreyGrid"; } },
    SEGREGATION("data/schemas/segregation.xsd") { public String toString() { return "SegregationGrid"; } };

    private File mySchemaFile;
    CA_TYPE(String schemaFile) {
        mySchemaFile = new File(schemaFile);
    }

    /**
     * @return String representation of schema file associated with this type
     */
    public File getSchemaFile() { return mySchemaFile; }

    /**
     * Uses reflection to return the constructor associated with a Grid that configures states randomly.
     * @return Grid constructor associated with this type. Use newInstance(Object...initargs) to invoke the constructor.
     */
    public Constructor<Grid> getRandomConstructor() {
        try {
            return getGridClass().getConstructor(int.class, double.class, double[].class, int[].class);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Uses reflection to return the constructor associated with a Grid that configures states based on explicit positions.
     * @return Grid constructor associated with this type. Use newInstance(Object...initargs) to invoke the constructor.
     */
    public Constructor<Grid> getConfiguredConstructor() {
        try {
            return getGridClass().getConstructor(int.class, double.class, ArrayList.class, int[].class);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Class<Grid> getGridClass() {
        try {
            @SuppressWarnings("unchecked")
            Class<Grid> gridClass = (Class<Grid>) Class.forName("src/" + this.toString());
            return gridClass;
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
}