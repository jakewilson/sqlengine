import java.io.Serializable;

/**
 * Column.java
 *
 * Class for cleanly storing the field definitions from the statement
 *
 * CREATE TABLE table-name (field-def [,field-def] ... );
 *
 * Created by jakewilson on 11/15/15.
 */
public class Column implements Serializable {

    private Column next;

    private String name;
    private FieldType type;

    private boolean notNull;

    public Column(String name, FieldType type, boolean notNull) {
        this.name = name.toLowerCase();
        this.type = type;
        this.notNull = notNull;
        this.next = null;
    }

    /**
     * Sets the next field definition
     * @param next the next field definition
     */
    public void setNext(Column next) {
        this.next = next;
    }

    /**
     * Returns the next field definition
     * @return the next field definition
     */
    public Column getNext() {
        return next;
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the field type
     * @return the field type
     */
    public FieldType getFieldType() {
        return type;
    }

    /**
     * Returns whether this field definition is not null
     * @return whether this field definition is not null
     */
    public boolean isNotNull() {
        return notNull;
    }
}

