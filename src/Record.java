import java.util.Hashtable;
import java.io.Serializable;

public class Record implements Serializable
{
	private Hashtable<String, Field> fields;

  /**
  Constructor
  **/
	public Record()
	{
		fields = new Hashtable<String, Field>();
	}
	
  /**
  Adds a field to the hashtable
  
  @param  colName String name of the Column the field is in
  @param  f       Field to be hashed
  **/
	public void addField(String colName, Field f)
	{
		this.fields.put(colName, f);
	}
  
  /**
  Adds a field to the hashtable
  
  @param  colName String name of the Column the field is in
  @return         Field in that Column
  **/
	public Field getField(String colName)
	{
	  if (fields.containsKey(colName))
    {        	 	
      return fields.get(colName);   
    }
    else
    {
      return null;
    }
  }
}
