import java.util.Hashtable;
import java.io.*;

public class Catalog implements Serializable
{   
   private Hashtable<String, Database> databaseNames;
   private Database current;

   private String filePath;
   
   /**
   Basic Constructor
   **/
   public Catalog()
   {
      this.databaseNames = new Hashtable<String, Database>();               
   }
   
   /**
   Filepath Constructor
   
   @param   fp String with filepath for load/save operations
   **/
   public Catalog(String fp)
   {
      this.databaseNames = new Hashtable<String, Database>();
      this.filePath = fp;
   }
   
   /**
   Checks hashtable and adds database
   
   @param   name  String name of Database
   @return        boolean
   **/
   public boolean createDatabase(String name)
   {
      if (!databaseNames.containsKey(name))
      {
         Database d = new Database(name);
         if (this.databaseNames.isEmpty())
            current = d;

         this.databaseNames.put(name, d);
         return true;   
      }
      else
      {
         return false;
      }      
   }
   
   /**
   Drops a database from the hashtable
   May need to add a check for and delete files section to keep already saved databases from reappearing
   
   @param   name  String name of Database
   @return        boolean
   **/
   public boolean dropDatabase(String name)
   {
      if (databaseNames.containsKey(name))
      {
         if (current.equals(databaseNames.get(name)))
            current = null;

         databaseNames.remove(name);
         return true;   
      }

      return false;
   }
   
   /**
   Checks the hashtable for the corrosponding database, if it is not found in the hash then it attempts to load a .ser file
   
   @param   name  String name of Database
   @return        boolean
   **/
   public boolean loadDatabase(String name)
   {
      if (databaseNames.containsKey(name))
      {
         this.current = databaseNames.get(name);
         return true;
      }

      return false;
   }

   public void setPath(String fp) {
      filePath = fp;
   }
   
   
   /**
   Saves the current database to the programs directory/catalog/"database name".ser
   
   @return        boolean
   **/
   public boolean saveDatabase()
   {
      try
      {
         FileOutputStream fileOut = new FileOutputStream(filePath);
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(this);
         out.close();
         fileOut.close();
      } catch (IOException i)
      {
         return false;
      }

      return true;
   }

   /**
    * Returns the current database
    * @return the current database
    */
   public Database getCurrent()
   {
      return current;
   }
}
