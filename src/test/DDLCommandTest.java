import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class DDLCommandTest {

    @org.junit.Test
    public void testGetName() throws Exception {
        DDLCommand ddl = new DDLCommand(CommandType.CREATE_TABLE, "name");
        assertEquals("name", ddl.getName());
    }

    @Test
    public void testGetColumn() throws Exception {
        FieldType ft = new FieldType(Type.INTEGER);
        Column c = new Column("ENO", ft, true);
        DDLCommand ddl = new DDLCommand(CommandType.CREATE_TABLE, "name", c);
        assertEquals(c, ddl.getColumn());
    }
}