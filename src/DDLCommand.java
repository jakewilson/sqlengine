/**
 * Created by jakewilson on 11/21/15.
 */
public class DDLCommand extends Command {

    /** To be used only for CREATE_TABLE command */

    public DDLCommand(CommandType type, String name, Column column) {
        super(type, name, column);
    }

    public DDLCommand(CommandType type, String name) {
        super(type, name, null);
    }

    public DDLCommand(CommandType type) {
        super(type, null);
    }

    /**
     * Returns the column of the table or null if the commandtype != CREATE_TABLE
     * @return the column of the table or null if the commandtype != CREATE_TABLE
     */
    public Column getColumn() {
        if (getType() == CommandType.CREATE_TABLE)
            return column;

        return null;
    }

}
