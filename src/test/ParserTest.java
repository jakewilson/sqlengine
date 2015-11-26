import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jakewilson on 11/25/15.
 */
public class ParserTest {

    @Test
    public void testSelect() {
        Command c = new DMLCommand(CommandType.SELECT);
        try {
            Command pc = Parser.parse("SELECT * FROM table;");
            assertEquals(c.getType(), pc.getType());
            assertEquals("table", pc.getSubject());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            assertEquals("ParseException", "");
        }
    }

    @Test
    public void testSelectCaseInsensitive() {
        try {
            Command pc = Parser.parse("select * from NEWTABLE;"); // case does not matter
            assertEquals("NEWTABLE", pc.getSubject());
        } catch (ParseException pex) {
            assertEquals("ParseException", "");
        }
    }

}
