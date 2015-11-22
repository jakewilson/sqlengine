import java.util.Hashtable;

public class Database implements DatabaseInterface
{
	private String DBname;
	private Hashtable<String, Table> database;
	
	public Database(String DBname)
	{
      this.DBname=DBname;
      database = new Hashtable<String, Database>();

	}
  
  /*
  creates the table, puts it in the database.
  Database is a hashtable of arraylists.
  Hashes table name and checks if it is there or 
  not and either successfully adds or doesn't. 
  */
  public boolean createTable(String name, Column def)
  {
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
   	
}
