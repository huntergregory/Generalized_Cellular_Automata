import java.lang.reflect.Constructor;
import java.util.ArrayList;

public enum CA_TYPE {
    FIRE("fire.xsd") { public String toString() { return "PercolationGrid"; } },
    PERCOLATION("percolation.xsd") { public String toString() { return "PercolationGrid"; } };

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