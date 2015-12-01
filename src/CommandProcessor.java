import java.util.ArrayList;

/**
 * Created by jakewilson on 11/23/15.
 */
public class CommandProcessor {

    private Catalog catalog;

    public CommandProcessor(Catalog catalog) {
        this.catalog = catalog;
    }

    public String execute(Command c) {
        if (c.isDML())
            return executeDML((DMLCommand)c);
        else
            return executeDDL((DDLCommand)c);
    }

    private String executeDML(DMLCommand c) {
        Database d = catalog.getCurrent();
        if (d == null)
            return "ERROR: No database selected.\n";
        Table t = d.getTable(c.getSubject());

        if (t == null)
            return "ERROR: table '" + c.getSubject() + "' does not exist.\n";

        ArrayList<String> names = c.getColumnNames();
        switch (c.getType()) {
            case SELECT:
                if (names.isEmpty() && c.allColumns)
                    names = t.getColumnNames();

                return t.select(names, c.getCondition());

            case INSERT:
                if (names.isEmpty())
                    names = t.getColumnNames();

                return t.insert(names, c.getInsertionValues());

            case UPDATE:
                return t.update(c.getCondition(), c.getColumnNames().get(0), c.getInsertionValues().get(0));

            case DELETE:
                return t.delete(c.getCondition());
        }

        return ""; // TODO
    }

    private String executeDDL(DDLCommand c) {
        String subject = c.getSubject();
        switch (c.getType()) {
            case CREATE_TABLE:
                if (catalog.getCurrent() == null)
                    return "ERROR: No database selected.\n";

                if (!catalog.getCurrent().createTable(subject, c.getColumn()))
                    return "ERROR: Table '" + subject + "' already exists.\n";
                break;

            case CREATE_DB:
                if (!catalog.createDatabase(subject))
                    return "ERROR: database '" + subject + "' already exists.\n";
                break;

            case LOAD_DB:
                if (!catalog.loadDatabase(subject))
                    return "ERROR: database '" + subject + "' does not exist.\n";
                break;

            case DELETE_DB:
                if (!catalog.dropDatabase(subject))
                    return "ERROR: database '" + subject + "' does not exist.\n";
                break;

            case SAVE_DB:
                if (!catalog.saveDatabase())
                    return "ERROR: save did not work.\n";
                break;
        }

        return ""; // TODO
    }

}
