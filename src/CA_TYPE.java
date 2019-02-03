import java.lang.reflect.Constructor;
import java.util.ArrayList;

public enum CA_TYPE {
    FIRE("schemas/fire.xsd") { public String toString() { return "PercolationGrid"; } },
    PERCOLATION("schemas/percolation.xsd") { public String toString() { return "PercolationGrid"; } },
    GAME_OF_LIFE("schemas/game-of-life.xsd") { public String toString() { return "LifeGrid"; } },
    PREDATOR_PREY("schemas/predator-prey.xsd") { public String toString() { return "PredatorPreyGrid"; } },
    SEGREGATION("schemas/segregation.xsd") { public String toString() { return "SegregationGrid"; } };

    private String schemaFile;
    CA_TYPE(String schemaFile) {
        this.schemaFile = schemaFile;
    }

    public String getSchemaFile() { return schemaFile; }

    public Constructor<Grid> getRandomConstructor() {
        try {
            return getGridClass().getConstructor(int.class, double.class, double[].class, int[].class);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

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
            @SuppressWarnings("unchecked") //should always be Grid class
            Class<Grid> gridClass = (Class<Grid>) Class.forName("src/" + this.toString());
            return gridClass;
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
}