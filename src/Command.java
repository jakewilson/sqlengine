/**
 * Created by jakewilson on 11/21/15.
 */
public abstract class Command {

    private CommandType type;

    public Command(CommandType type) {
        this.type = type;
    }

    /**
     * Returns the CommandType of the command
     * @return the CommandType of the command
     */
    public CommandType getType() {
        return type;
    }
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
    DELETE
}
