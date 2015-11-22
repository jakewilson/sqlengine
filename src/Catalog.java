import java.util.Hashtable;
import java.io.*;

public class Catalog
{   
   private Hashtable<String, Database> databaseNames = new Hashtable<String, Database>();
   private String currentName;      
   private Database current;
   
   /*
   Maybe attempt to load related database files from an expected path in the constructor, and hash them so that loadDatabase
   can work faster. 
   */
   public Catalog()
   {
      //constructor stuff            
   }
   
   /*
   Change filepath constructor, probably not needed
   */
   public Catalog(String filePath)
   {
      //overloaded constructor stuff
   }
   
   /*
   Checks hashtable and adds database
   */
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
   
   /*
   Drops a database from the hashtable
   May need to add a check for and delete files section to keep already saved databases from reappearing
   */
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
   
   /*
   Checks the hashtable for the corrosponding database, if it is not found in the hash then it attempts to load a .ser file
   Serialization code needs checking and modifying for the entire database, maybe each object in the database being saved appends to the same file?
   */
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
         try
         {
            FileInputStream fileIn = new FileInputStream("/catalog/" + name + ".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            this.current = (Database) in.readObject();
            this.currentName = name;
            in.close();
            fileIn.close();
         }catch(IOException i)
         {
            System.out.println("Failed to load: File not found");
            return false;
         }catch(ClassNotFoundException c)
         {
            System.out.println("Failed to load: Incorrect object type");                       
            return false;
         }
         return true;
      }
   }
   
   
   /*
   Saves the current database to the programs director/catalog/"database name".ser
   Serialization code needs checking and modifying for the entire database, maybe each object in the database being saved appends to the same file?
   */
   public boolean saveDatabase()
   {
      try
      {
         FileOutputStream fileOut = new FileOutputStream("/catalog/" + currentName + ".ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(current);
         out.close();
         fileOut.close();
         System.out.println("Data saved in /catalog/" + currentName + ".ser");
      }catch(IOException i)
      {
          System.out.println("Failed to save");
      }

      return true;
   }
}
