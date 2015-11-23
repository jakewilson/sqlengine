import org.junit.Test;

import static org.junit.Assert.*;

public class RecordTest {

    @Test
    public void testAddField() throws Exception {
        Column eno  = new Column("ENO", new FieldType(Type.INTEGER), true);
        Column name = new Column("Name", new FieldType(Type.CHARACTER, 10), false);
        Column age  = new Column("Age", new FieldType(Type.INTEGER, 3), false);
        Column bday = new Column("Birthday", new FieldType(Type.DATE), false);

        Field f1 = new Field(eno);
        f1.setValue("0");
        Field f2 = new Field(name);
        f2.setValue("Jake");
        Field f3 = new Field(age);
        f3.setValue("22");
        Field f4 = new Field(bday);
        f4.setValue("5/23/92");

        Record r = new Record();
        r.addField(f1);
        r.addField(f2);
        r.addField(f3);
        r.addField(f4);

        assertEquals(f1.getValue(), r.getField("ENO").getValue());
        assertEquals(f2.getValue(), r.getField("Name").getValue());
        assertEquals(f3.getValue(), r.getField("AGE").getValue());
        assertEquals(f4.getValue(), r.getField("BiRthday").getValue());
    }

}