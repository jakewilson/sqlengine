/**
 * Created by jakewilson on 11/15/15.
 */
public interface DatabaseInterface {

    /**
     * Creates a new table in the database
     * @param name the name of the new database
     * @param def the field definitions
     * @return whether the creation was successful
     */
    public boolean createTable(String name, Column def);

    /**
     * Removes the specified table from the database
     * @param name the name of the table to remove
     * @return whether the removal was successful
     */
    public boolean dropTable(String name);

    /**
     * Saves each table in the database
     * @return whether the saves were successful
     */
    public boolean save();

}
