import java.util.Date;
import java.util.Hashtable;
import java.io.Serializable;
import java.text.*;

public class Field implements Serializable
{
	private Column col;
	private String value;
	private FieldType ft;
   
   /**
   Constructor takes a Column and retrieves it's FieldType. setType must be used to input the value.
   
   @param c Column
   **/
	public Field(Column c)
	{
		this.col = c;
		this.ft = col.getFieldType();
		this.value = c.isNotNull() ? "" : null;
	}

	public Field(Column c, String value)
	{
		this.col = c;
		this.ft = col.getFieldType();
		this.value = c.isNotNull() ? "" : null;
		setValue(value);
	}

	public boolean setValue(String val)
	{
		if(ensureType(val))
		{
			value = val;
			return true;
		}

		return false;
	}
   
   /**
   Checks data types to ensure that the entered type is valid for that Column
   
   @param val String data value that needs to be checked
   @return    boolean
   **/
	private boolean ensureType(String val)
	{
		Type t = ft.getType();

		switch(t)
		{
			case INTEGER:
				try
				{
					Integer.parseInt(val);
					if(val.length() <= ft.getPrecision())
						return true;
				} catch (NumberFormatException nfex) {}
				return false;

			case NUMBER:
				try
				{
					Double.parseDouble(val);
					int decimal = val.indexOf('.');
					if (decimal != -1) // if the number contains a decimal point
					{
						if (val.length() <= ft.getPrecision() + 1)
							if (val.substring(decimal + 1).length() <= ft.getScale())
								return true;
					}
					else
						if(val.length() <= ft.getPrecision())
							return true;
				}catch(NumberFormatException nfex)
				{
					/* fall through */
				}
				return false;

			case CHARACTER:
				if(val.length() <= ft.getPrecision())
					return true;

				return false;

			case DATE:
				return isDateValid(val);
		}

		return false;
	}

	/**
	 * Returns whether a date is valid or not
	 * @param date the date to check
	 * @return whether the date is valid or not
	 */
	private boolean isDateValid(String date)
	{
		int slash = date.indexOf('/');
		try
		{
			int month = Integer.parseInt(date.substring(0, slash));
			if (month < 1 || month > 12)
				return false;

			int secondSlash = date.indexOf('/', slash + 1);
			int day = Integer.parseInt(date.substring(slash + 1, secondSlash));
			if (day < 1 || day > 31)
				return false;

			if (month == 2 && day > 29)
				return false;

			int year = Integer.parseInt(date.substring(secondSlash + 1));
			if (year < 0)
				return false;
		} catch (NumberFormatException nfex)
		{
			return false;
		}

		return true;
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
