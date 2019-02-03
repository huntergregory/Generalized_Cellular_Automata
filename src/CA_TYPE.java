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
        return getGridClass().getConstructor(int.class, double.class, double[].class, int[].class);
    }

    public Constructor<Grid> getConfiguredConstructor() {
        return getGridClass().getConstructor(int.class, double.class, ArrayList.class, int[].class);
    }

    private Class<Grid> getGridClass() {
        return Class.forName("src/" + this.toString());
    }
}