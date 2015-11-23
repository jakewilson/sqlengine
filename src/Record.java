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
  
  	@param  f       Field to be hashed
  	**/
	public void addField(Field f)
	{
		this.fields.put(f.getColumn().getName().toLowerCase(), f);
	}
  
  	/**
  	Adds a field to the hashtable
  
  	@param  colName String name of the Column the field is in
  	@return         Field in that Column
  	**/
	public Field getField(String colName)
	{
		colName = colName.toLowerCase();
    	if (fields.containsKey(colName))
    		return fields.get(colName);

		return null;
  	}
}
