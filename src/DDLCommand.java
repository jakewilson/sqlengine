/**
 * Created by jakewilson on 11/21/15.
 */
public class DDLCommand extends Command {

    /** To be used only for CREATE_TABLE command */
    private Column column;
    private String name;

    public DDLCommand(CommandType type, String name, Column column) {
        super(type);
        this.name = name;
        this.column = column;
    }

    public DDLCommand(CommandType type, String name) {
        this(type, name, null);
    }

    public DDLCommand(CommandType type) {
        this(type, null);
    }

    /**
     * Returns the name that is the object of the command
     * @return the name that is the object of the command
     */
    public String getName() {
        return name;
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
