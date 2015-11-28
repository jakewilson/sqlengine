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
        if (c.isDML())
            return executeDML((DMLCommand)c);
        else
            return executeDDL((DDLCommand)c);
    }

    private String executeDML(DMLCommand c) {
        Table t = current.getTable(c.getSubject());
        ArrayList<String> names = c.getColumnNames();
        switch (c.getType()) {
            case SELECT:
                if (names.isEmpty() && c.allColumns)
                    names = t.getColumnNames();

                return t.select(names);

            case INSERT:
                if (names.isEmpty())
                    names = t.getColumnNames();

                return t.insert(names, c.getInsertionValues());
        }

        return ""; // TODO
    }

    private String executeDDL(DDLCommand c) {
        return ""; // TODO
    }

}
