import java.util.Hashtable;
import java.io.*;

public class Catalog
{   
   private Hashtable<String, Database> databaseNames;
   private String currentName;
   private String filePath;      
   private Database current;
   
   /**
   Basic Constructor
   **/
   public Catalog()
   {
      this.databaseNames = new Hashtable<String, Database>();               
   }
   
   /**
   Filepath Constructor
   
   @param String with filepath for load/save operations
   **/
   public Catalog(String fp)
   {
      this.databaseNames = new Hashtable<String, Database>();
      this.filePath = fp;
   }
   
   /**
   Checks hashtable and adds database
   
   @param String name of Database
   @return true if able to create Database of that name, false otherwise
   **/
   public boolean createDatabase(String name)
   {
      if (!databaseNames.containsKey(name))
      {
         this.databaseNames.put(name, new Database());
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
   
   @param String name of Database
   @return true if able to drop Database of that name, false otherwise
   **/
   public boolean dropDatabase(String name)
   {
      if (databaseNames.containsKey(name))
      {
         databaseNames.remove(name);
         return true;   
      }
      else
      {
         return false;
      }
   }
   
   /**
   Checks the hashtable for the corrosponding database, if it is not found in the hash then it attempts to load a .ser file
   
   @param String name of Database
   @return true if able to load Database of that name, false otherwise
   **/
   public boolean loadDatabase(String name)
   {
      if (databaseNames.containsKey(name))
      {
         this.current = databaseNames.get(name);
         this.currentName = name;
         return true;   
      }
      else
      {
         return false;
      }
   }
   
   
   /**
   Saves the current database to the programs directory/catalog/"database name".ser   
   **/
   public boolean saveDatabase()
   {
      try
      {
         FileOutputStream fileOut = new FileOutputStream("/catalog/" + currentName + ".ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(current);
         out.close();
         fileOut.close();
         return true;
      }catch(IOException i)
      {
         return false;
      }
   }
}
