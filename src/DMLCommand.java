/**
 * Created by jakewilson on 11/22/15.
 */
public class DMLCommand extends Command {

    public DMLCommand(CommandType type, String subject, Column column) {
        super(type, subject, column);
    }

    public DMLCommand(CommandType type, String tableName) {
        super(type, tableName, null);
    }

    public DMLCommand(CommandType type) {
        super(type, null);
    }

    /**
     * Returns the column of the command
     * @return the column of the command
     */
    public Column getColumn() {
        return column;
    }

}
