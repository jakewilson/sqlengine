import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by David on 11/24/2015.
 */
public class DatabaseTest {
    @Test
    public void testCreateTable() throws  Exception{
        Database d = new Database("test");
        Column c = new Column("testColumn",new FieldType(Type.NUMBER),false);
        boolean b1 =d.createTable("testTable",c);
        boolean b2 = d.createTable("testTable",c);
        boolean b3 = d.createTable("testTable2",c);
        assertEquals(true,b1);
        assertEquals(false,b2);
        assertEquals(true,b3);
    }
    @Test
    public void testDropTable() throws  Exception{
        Database d = new Database("test");
        Column c = new Column("testColumn",new FieldType(Type.NUMBER),false);
        d.createTable("testTable",c);
        d.createTable("testTable2",c);
        boolean b1 = d.dropTable("testTable");
        boolean b2 = d.dropTable("testTable");
        boolean b3 = d.dropTable("testTable2");
        assertEquals(true,b1);
        assertEquals(false,b2);
        assertEquals(true,b3);
    }
    @Test
    public void testGetName() throws Exception{
        Database d = new Database("test");
        assertEquals("test", d.getName());
    }

    @Test
    public void testGetTable() {
        Database d = new Database("testdatabase");
        Column c = new Column("Column",new FieldType(Type.NUMBER),false);
        d.createTable("btable",c);
        assertNotEquals(null, d.getTable("btable"));
        assertEquals(null, d.getTable("random"));
    }
}


