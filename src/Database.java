import java.util.Hashtable;
import java.io.Serializable;

public class Database implements Serializable
{
	private String name;
	private Hashtable<String, Table> database;
	
	public Database(String name)
	{
      this.name=name;
      database = new Hashtable<String, Table>();

	}
  
  /*
  creates the table, puts it in the database.
  Database is a hashtable of arraylists.
  Hashes table name and checks if it is there or 
  not and either successfully adds or doesn't. 
  */
  public boolean createTable(String name, Column def)
  {
      name = name.toLowerCase(); // ensure case insensitivity
      if (database.get(name) == null) //creates the table if doesn't exist
      {
         this.database.put(name, new Table());
         return true;
      }
      else
      {
         return false;       
      }
  
  }
  
  /*
  Drops a table from the database.
  Checks is the name is in the hashtable.
  If it is, it is removed from the hashtable.
  If it's not, error is thrown. 
  */
	
 public boolean dropTable(String name)
 {
     name = name.toLowerCase();
      //if table exists with the name it removes it
      if (database.get(name) != null)
      {
         this.database.remove(name);
         return true;
      }
      else
      {
         return false;
      }
 }

    /**
     * Returns the name of the database
     * @return the name of the database
     */
    public String getName()
    {
        return name;
    }
   	
}

