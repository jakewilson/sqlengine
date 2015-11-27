import java.util.ArrayList;

/**
 * Created by jakewilson on 11/22/15.
 */
public class DMLCommand extends Command {

    private ArrayList<String> columnNames;
    private ArrayList<String> insertionValues;
    public boolean allColumns = false;

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

    /**
     * Sets the columnNames of the command
     * @param columnNames the new column names of the command
     */
    public void setColumnNames(ArrayList<String> columnNames) {
        this.columnNames = columnNames;
    }

    /**
     * Returns the column names of the command
     * @return the column names of the command
     */
    public ArrayList<String> getColumnNames() {
        return columnNames;
    }

    public void setInsertionValues(ArrayList<String> values) {
        this.insertionValues = values;
    }

    public ArrayList<String> getInsertionValues() {
        return insertionValues;
    }

}
