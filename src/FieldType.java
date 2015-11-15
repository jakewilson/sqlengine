/**
 * Created by jakewilson on 11/15/15.
 */
public class FieldType {
    private Type type;
    private int precision;
    private int scale;

    // TODO possibly add mm/dd/yyyy for dates ?

    public FieldType(Type type) {
        this(type, 0);
    }

    public FieldType(Type type, int precision) {
        this(type, precision, 0);
    }

    public FieldType(Type type, int precision, int scale) {
        this.type = type;
        this.precision = precision;
        this.scale = scale;
    }
}

/**
 * Attribute types
 */
enum Type {
    INTEGER,
    NUMBER,
    CHARACTER,
    DATE
}
