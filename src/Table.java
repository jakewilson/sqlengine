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

		if (this.records.isEmpty())
			return "";

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
					Field f = r.getField(name);
					if (f == null)
						return "ERROR: Bad column name '" + name + "'.";
					s += f.getValue();
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

	/**
	 * Returns the names of all columns in an array list
	 * @return the names of all columns in an array list
	 */
	public ArrayList<String> getColumnNames() {
		ArrayList<String> names = new ArrayList<String>();
		Column c = first;
		while (c != null) {
			names.add(c.getName());
			c = c.getNext();
		}

		return names;
	}

	/**
	 * Returns the column with the specified name
	 * @param name the name of the column to find
	 * @return the column with the specified name
	 */
	private Column findColumn(String name) {
		Column c = first;
		while (c != null) {
			if (c.getName().equalsIgnoreCase(name))
				return c;
			c = c.getNext();
		}

		return null;
	}

	
	public String insert(ArrayList<String> columnNames, ArrayList<String> values)
	{
		if (columnNames.size() != values.size())
			return "ERROR: Wrong number of values\n";

		ArrayList<String> allNames = getColumnNames();

		for (int i = 0; i < columnNames.size(); i++)
			columnNames.set(i, columnNames.get(i).toLowerCase());

		for (String s : allNames) {
			if (!columnNames.contains(s.toLowerCase())) {
				columnNames.add(s.toLowerCase());
				values.add(null);
			}
		}


		Record r = new Record();
		Field[] f = new Field[columnNames.size()];

		for (int i = 0; i < columnNames.size(); i++)
			if (!(f[i] = new Field(findColumn(columnNames.get(i)))).setValue(values.get(i)))
				return "ERROR: Wrong type: " + values.get(i) + " for column '" + columnNames.get(i) + "'\n";

		for (int i = 0; i < f.length; i++)
			r.addField(f[i]);

		this.addRecord(r);
		return "";
	}

	// TODO wInsert


	public boolean delete()
	{
		return false;
	}
}
