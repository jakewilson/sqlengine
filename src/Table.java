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
	 * @param col ArrayList<Column> that is used to print values
	 *
	 */
	public String select(ArrayList<Column> col)
	{
		String s = null;

		if(col != null)
		{
			for(Column c : col)
				s += c.getName() + " | ";
			s += "\n";

			for(Record r : this.records)
			{
				for(Column c : col)
					s += r.getField(c.getName()).getValue() + " | ";
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
