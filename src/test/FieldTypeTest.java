import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by David on 11/22/2015.
 */
public class FieldTypeTest {
    @Test
    public void testGetType() throws  Exception{
        FieldType ft = new FieldType(Type.INTEGER);
        assertEquals(Type.INTEGER,ft.getType());
    }
    @Test
    public void testGetPrecision() throws Exception{
        FieldType ft1 = new FieldType(Type.NUMBER,2);
        assertEquals(Type.NUMBER,ft1.getType());
        assertEquals(2,ft1.getPrecision());
    }
    @Test
    public void testGetScale() throws  Exception{
        FieldType ft2 = new FieldType(Type.DATE,5,5);
        assertEquals(Type.DATE,ft2.getType());
        assertEquals(5,ft2.getPrecision());
        assertEquals(5,ft2.getScale());
    }

}

