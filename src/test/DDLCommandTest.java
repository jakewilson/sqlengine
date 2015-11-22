import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class DDLCommandTest {

    @org.junit.Test
    public void testGetSubject() throws Exception {
        DDLCommand ddl = new DDLCommand(CommandType.CREATE_TABLE, "name");
        assertEquals("name", ddl.getSubject());
    }

    @Test
    public void testGetColumn() throws Exception {
        FieldType ft = new FieldType(Type.INTEGER);
        Column c = new Column("ENO", ft, true);
        DDLCommand ddl = new DDLCommand(CommandType.CREATE_TABLE, "name", c);
        assertEquals(c, ddl.getColumn());

        DDLCommand ddl1 = new DDLCommand(CommandType.CREATE_DB, "name", c);
        assertEquals(null, ddl1.getColumn()); // should return null if type != CREATE_TABLE
    }

    @Test
    public void testGetType() throws Exception {
        FieldType ft = new FieldType(Type.INTEGER);
        Column c = new Column("ENO", ft, true);
        DDLCommand ddl = new DDLCommand(CommandType.CREATE_TABLE, "name", c);
        assertEquals(CommandType.CREATE_TABLE, ddl.getType());
    }

}