import java.lang.reflect.Field;

/**
 * Created by jakewilson on 11/15/15.
 */
public class FieldDefinition {
    private FieldDefinition next;

    private String name;
    private FieldType type;

    private boolean notNull;
    private boolean primaryKey; // is this needed?

    public FieldDefinition(String name, FieldType type, boolean notNull) {
        this.name = name;
        this.type = type;
        this.notNull = notNull;
        this.next = null;
    }

    /**
     * Sets the next field definition
     * @param next the next field definition
     */
    public void setNext(FieldDefinition next) {
        this.next = next;
    }

    /**
     * Returns the next field definition
     * @return the next field definition
     */
    public FieldDefinition getNext() {
        return next;
    }

    /**
     * Returns the name
     * @return the name
     */
    private String getName() {
        return name;
    }

    /**
     * Returns the field type
     * @return the field type
     */
    private FieldType getType() {
        return type;
    }

    /**
     * Returns whether this field definition is not null
     * @return whether this field definition is not null
     */
    private boolean isNotNull() {
        return notNull;
    }
}

