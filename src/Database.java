import java.util.Hashtable;

public class Database implements DatabaseInterface
{
	private String DBname;
   private Hashtable database;
	
	
	public Database(String DBname)
	{
      this.DBname=DBname;
		this.database=initializeHashTable(); //constructor to create db
	}
	
  private Hashtable initializeHashTable() //creates the hashtable
  {
     Hashtable<String, Table> db = new Hashtable<String, Table>();
     return db;
  }
  
  
  /*
  creates the table, puts it in the database.
  Database is a hashtable of arraylists.
  Hashes table name and checks if it is there or 
  not and either successfully adds or doesn't. 
  */
  public void createTable(String name, Column def)
  {
      Table n = this.database.get(name);
      if (n != null) //creates the table
      {
         n=new Table ();//need table parameters
         this.database.put(name, n);
         System.out.println("success, table "+name+" added");         
      }
      else
      {
         System.out.println("error table exists");         
      }
  
  }
  
  /*
  Drops a table from the database.
  Checks is the name is in the hashtable.
  If it is, it is removed from the hashtable.
  If it's not, error is thrown. 
  */
	
 public void dropTable(String name)
 {
      Table n = this.database.get(name);
      if (n != null) //creates the table
      {
         this.database.remove(name);
         System.out.println("Successfully dropped "+ name);
      }
      else
      {
         System.out.println("error table doesn't exist");
      }
 }  
   	
}
