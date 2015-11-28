import java.util.ArrayList;

/**
 * Created by jakewilson on 11/23/15.
 */
public class CommandProcessor {

    private Database current;



    public CommandProcessor(Database current) {
        this.current = current;
    }

    public String execute(Command c) {
        switch (c.getType()) {
            case SELECT:
                Table t = current.getTable(c.getSubject());
                ArrayList<String> names = ((DMLCommand)c).getColumnNames();
                if (names.isEmpty() && ((DMLCommand) c).allColumns) {
                    // add all column names if the query was SELECT *
                    Column col = t.getFirst();
                    while (col != null) {
                        names.add(col.getName());
                        col = col.getNext();
                    }
                }

                return t.select(names);
            case CREATE_TABLE:
                break;

            case CREATE_DB:
                break;

            case INSERT:
                break;
        }

        return ""; // TODO
    }

}
