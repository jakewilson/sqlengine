import java.io.Serializable;

/**
 * FieldType
 *
 * Class for cleanly storing field types from the statement
 *
 * field-type -> integer[(n)] |
 *               number[(w[,d])] |
 *               character[(n)] |
 *               date
 *
 * Created by jakewilson on 11/15/15.
 */
public class FieldType implements Serializable {

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

    /**
     * Returns the type
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the precision
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Returns the scale
     * @return the scale
     */
    public int getScale() {
        return scale;
    }
}

/**
 * Attribute types
 * Just bare types without any modifiers - FieldType stores
 * the precision and scale
 */
enum Type {
    INTEGER,
    NUMBER,
    CHARACTER,
    DATE
}
