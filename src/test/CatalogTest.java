import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by David on 11/24/2015.
 */
public class CatalogTest {
    @Test
    public void testCreateDatabase() throws  Exception{
        Catalog c = new Catalog();
        boolean b1 =c.createDatabase("test");
        boolean b2 =c.createDatabase("test");
        boolean b3 = c.createDatabase("test2");
        assertEquals(true,b1);
        assertEquals(false,b2);
        assertEquals(true,b3);
    }
    @Test
    public void testDropDatabase() throws  Exception{
        Catalog c = new Catalog();
        boolean b1 =c.createDatabase("test");
        boolean b2 =c.createDatabase("test");
        boolean b3 = c.createDatabase("test2");

        boolean b4 = c.dropDatabase("test");
        boolean b5 = c.dropDatabase("test");
        boolean b6 =c.dropDatabase("test2");
        assertEquals(true,b4);
        assertEquals(false,b5);
        assertEquals(true,b6);
    }
    @Test
    public void testLoadDatabase() throws  Exception{
        Catalog  c = new Catalog();
        c.createDatabase("Test");

        boolean b1 = c.loadDatabase("Test");
        boolean b2 = c.loadDatabase("Test2");

        assertEquals(true,b1);
        assertEquals(false,b2);
    }
    @Test
    public void testSaveDatabase() throws  Exception{
        Catalog c = new Catalog();
        c.createDatabase("test");
        boolean b1 = c.loadDatabase("test");
        //boolean b2 = c.saveDatabase();


    }

}
