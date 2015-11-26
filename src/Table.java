import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Brandon
 * This is the table class for the xSQL engine
 * which will be implemented by a Catalog class
 * and will make use of a column class.  This 
 * function of this class is to act like the SQL 
 * engine with a CREATE and DROP command.  There 
 * are two constructors for this class.  One takes
 * no parameters, and the other takes two.  
 * 
 * The two functions will be used to create and 
 * drop a table.  Dropping a table means deleting
 * the table.  
 *
 */
public class Table implements Serializable
{
	private static final long serialVersionUID = 6599586814244628269L;
	private String name;
	private ArrayList<Record> records;
	private Column first;
	
	
	/**
	 * Constructor for a table with no parameters
	 */
	public Table()
	{
		this("", null);
	}
	
	/**
	 * Constructor to create a new table with set
	 * values
	 * @param name name of the Table
	 * @param first column to link to
	 */
	public Table(String name, Column first)
	{
		this.setName(name);
		this.records = new ArrayList<Record>();
		this.setFirst(first);
	}

	/**
	 * Adds a record to the table
	 * @param r the record to add
	 * @return whether the record was successfully added
	 */
	public boolean addRecord(Record r)
	{
		return records.add(r);
	}
	
	public Column getFirst()
	{
		return first;
	}
	
	/**
	 * @param first Column to be set
	 */
	public void setFirst(Column first)
	{
		this.first = first;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * This method is used to print 
	 * Column names and values of the records
	 * @param columnNames array list of column names
	 *
	 */
	public String select(ArrayList<String> columnNames)
	{
		String s = "";

		if(columnNames != null)
		{
			for(String name : columnNames)
			{
				s += name;
				// only add the pipe if it's not the last column
				s += (columnNames.indexOf(name) == columnNames.size() - 1) ? "" : " | ";
			}

			s += "\n";

			for(Record r : this.records)
			{
				for(String name : columnNames)
				{
					s += r.getField(name).getValue();
					// only add the pipe if it's not the last column
					s += (columnNames.indexOf(name) == columnNames.size() - 1) ? "" : " | ";
				}
				s += "\n";
			}
		}
		return s;
	}
	
	/**
	 * Calls select with no value
	 * TODO
	 */
	public void select()
	{
		select(null);
	}


	public boolean update(boolean wUpdate)
	{
		//wUpdate boolean
		return false;
	}

	
	public boolean insert(boolean wInsert)
	{

		//wSelect boolean 
		return false;	
	}


	public boolean delete()
	{
		return false;
	}
}
