import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by jakewilson on 11/25/15.
 */
public class ParserTest {

    @Test
    public void testSelect() {
        Command c = new DMLCommand(CommandType.SELECT);
        try {
            DMLCommand pc = (DMLCommand)Parser.parse("SELECT * FROM table;");
            assertEquals(c.getType(), pc.getType());
            assertEquals("table", pc.getSubject());
            assertEquals(true, pc.allColumns);
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            assertEquals("ParseException", "");
        }
    }

    @Test
    public void testSelectCaseInsensitive() {
        try {
            DMLCommand pc = (DMLCommand)Parser.parse("select * from NEWTABLE;"); // case does not matter
            assertEquals("NEWTABLE", pc.getSubject());
            assertEquals(CommandType.SELECT, pc.getType());
            assertEquals(true, pc.allColumns);
        } catch (ParseException pex) {
            assertEquals("ParseException", "");
        }
    }

    @Test
    public void testSelectCertainColumns() {
        try {
            DMLCommand pc = (DMLCommand)Parser.parse("select (name, eno) from table;"); // case does not matter
            ArrayList<String> names = pc.getColumnNames();

            assertEquals(CommandType.SELECT, pc.getType());
            assertEquals("table", pc.getSubject());

            assertEquals("name", names.get(0));
            assertEquals("eno", names.get(1));
        } catch (ParseException pex) {
            assertEquals("ParseException", "");
        }
    }

}
