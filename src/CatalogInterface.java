/**
 * Created by jakewilson on 11/15/15.
 */
public interface CatalogInterface {

    /**
     * Creates a new database
     * @param name the name of the new database
     * @return whether the creation was successful
     */
    public boolean createDatabase(String name);

    /**
     * Removes a database from the catalog
     * @param name the name of the database to delete
     * @return whether the deletion was successful
     */
    public boolean dropDatabase(String name);

    /**
     * Loads a database into the catalog. Sets the loaded
     * database as 'current'
     * @param name the name of the database to load
     * @return whether the load was successful
     */
    public boolean loadDatabase(String name);

    /**
     * Writes a database to disk
     * @param name the name of the database to save
     * @return whether the operation was successful
     */
    public boolean saveDatabase(String name);

}
