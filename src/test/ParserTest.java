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

    @Test
    public void testBadSelect() {
        try {
            Parser.parse("SELECT ** From table;");
            assertEquals(false, true); // should fail
        } catch (ParseException pex) {
            assertEquals(true, true); // should fail
        }
    }

    @Test
    public void testInsert() {
        try {
            Parser.parse("INSERT INTO table VALUES ('hello', 'there', 56, 06/06/94);");
        } catch (ParseException pex) {
            assertEquals(false, true);
        }
    }

    @Test
    public void testInsertBad() {
        try {
            Parser.parse("INSERT INTO table VALUES ('hello, 'there', 56, 06/06/94);"); // missing single quote after hello
            assertEquals(false, true);
        } catch (ParseException pex) {
            assertEquals(true, true);
        }
    }

    @Test
    public void testInsertCertainColumns() {
        try {
            Parser.parse("INSERT INTO table (eno, name, age) VALUES (67, 89, 'hi', 'ok');"); // missing single quote after hello
            assertEquals(true, true);
        } catch (ParseException pex) {
            assertEquals(false, true);
        }
    }
}
