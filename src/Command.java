/**
 * This class and its subclasses will be used to easily
 * store commands and execute them
 *
 * Created by jakewilson on 11/21/15.
 */
public abstract class Command {

    private CommandType type;

    protected Column column;
    private String subject;

    public Command(CommandType type, String subject, Column column) {
        this.type = type;
        this.subject = subject;
        this.column = column;
    }

    public Command(CommandType type, String subject) {
        this(type, subject, null);
    }

    public Command(CommandType type) {
        this(type, null);
    }

    /**
     * Returns the CommandType of the command
     * @return the CommandType of the command
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Returns the subject that is the object of the command
     * @return the subject that is the object of the command
     */
    public String getSubject() {
        return subject;
    }

    public abstract Column getColumn();

}

enum CommandType {
    CREATE_TABLE,
    CREATE_DB,
    DELETE_DB,
    DROP_TABLE,
    LOAD_DB,
    SAVE_DB,
    INSERT,
    UPDATE,
    DELETE,
    SELECT,
    WSELECT
}
