import java.util.Hashtable;
import java.io.Serializable;
import java.text.*;

public class Field implements Serializable
{
	private Column col;
	private  String value;
	private FieldType ft;
   
   /**
   Constructor takes a Column and retrieves it's FieldType. setType must be used to input the value.
   
   @param c Column
   **/
	public Field(Column c)
	{
		this.col = c;
		ft = col.getFieldType();		
	}

	public boolean setType(String val)
	{
		if(ensureType(val))
		{
			value = val;
			return true;
		}
		else
		{
			return false;
		}
	}
   
   /**
   Checks data types to ensure that the entered type is valid for that Column
   
   @param val String data value that needs to be checked
   @return    boolean
   **/
	private boolean ensureType(String val)
	{
		Type t = ft.getType();
		int decimal;
		switch(t)
		{
			case INTEGER:	try
					{
						Integer.parseInt(val);
						if(val.length() <= ft.getPrecision())
						{
							return true;
						}
						else
						{
							return false;
						}
					}catch(NumberFormatException nfex)
					{
						return false;
					}
			case NUMBER:	try
					{
						Double.parseDouble(val);
						decimal = val.indexOf('.');
						if (decimal != -1)
						{
							if(val.length() <= ft.getPrecision() + 1)
							{
								if(val.substring(decimal).length() <= ft.getScale())
								{
									return true;
								}
								return false;
							}
							else
							{
								return false;
							}
						}
						else
						{
							if(val.length() <= ft.getPrecision())
							{
								return true;
							}
							else
							{
								return false;
							}
						}
					}catch(NumberFormatException nfex)
					{
						return false;
					}
			case CHARACTER:	if(val.length() <= ft.getPrecision())
					{
						return true;
					}
					else
					{
						return false;
					}
			case DATE:
						if (new SimpleDateFormat().parse(val, new ParsePosition(0)) != null)
							return true;
						return false;
			default:	return false;		
		}
	}

	public String getValue()
	{
		return value;
	}

	public Column getColumn()
	{
		return col;
	}
}
