/**
 * Created by jakewilson on 11/15/15.
 */
public interface CatalogInterface {

    public static final String PATH = "Catalog";

    /**
     * Creates a new database
     * @param name the name of the new database
     * @return whether the creation was successful
     */
    public boolean createDatabase(String name);

    /**
     * Removes a database from the catalog
     * @param name the name of the database to remove
     * @return whether the removal was successful
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
     * Saves the current database
     * @return whether the operation was successful
     */
    public boolean saveDatabase();

}
