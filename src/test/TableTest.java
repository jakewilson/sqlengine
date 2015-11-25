import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TableTest {

    public static Column eno;
    public static Column name;
    public static Column age;
    public static Column bday;
    public static Table table;

    @BeforeClass
    public static void setUp() {
         eno  = new Column("ENO", new FieldType(Type.INTEGER, 3), true);
         name = new Column("Name", new FieldType(Type.CHARACTER, 15), false);
         age  = new Column("Age", new FieldType(Type.INTEGER, 3), false);
         bday = new Column("Birthdate", new FieldType(Type.DATE), false);

        eno.setNext(name);
        name.setNext(age);
        age.setNext(bday);

        table = new Table("Employee", eno);
    }

    @Test
    public void testGetFirst() throws Exception {
        assertEquals(eno, table.getFirst());
    }

    @Test
    public void testSetFirst() throws Exception {

    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("Employee", table.getName());
    }

    @Test
    public void testSetName() throws Exception {
        table.setName("Does it work?");
        assertEquals("Does it work?", table.getName());
    }

    @Test
    public void testSelectJustColumns() throws Exception {
        Table table = new Table("Employee", eno);
        ArrayList<Column> cols = new ArrayList<Column>();
        cols.add(eno); cols.add(name); cols.add(age); cols.add(bday);

        String result = table.select(cols);

        assertEquals(eno.getName() + " | " + name.getName() + " | " + age.getName() + " | " + bday.getName() + "\n", result);
    }

    @Test
    public void testSelectWithRows() throws Exception {
        Table table = new Table("Employee", eno);
        ArrayList<Column> cols = new ArrayList<Column>();
        cols.add(eno); cols.add(name); cols.add(age); cols.add(bday);

        String expectedResult = eno.getName() + " | " + name.getName() + " | " + age.getName() + " | " + bday.getName() + "\n";
        Record r = new Record();
        Field f1 = new Field(eno, "0");
        Field f2 = new Field(name, "Jake");
        Field f3 = new Field(age, "22");
        Field f4 = new Field(bday, "6/6/94");
        r.addField(f1); r.addField(f2); r.addField(f3); r.addField(f4);

        expectedResult += "0 | Jake | 22 | 6/6/94\n";

        Record r1 = new Record();
        Field g1 = new Field(eno, "1");
        Field g2 = new Field(name, "Gabby");
        Field g3 = new Field(age, "21");
        Field g4 = new Field(bday, "3/21/15");
        r1.addField(g1); r1.addField(g2); r1.addField(g3); r1.addField(g4);

        expectedResult += "1 | Gabby | 21 | 3/21/15\n";

        table.addRecord(r); table.addRecord(r1);

        assertEquals(expectedResult, table.select(cols));
    }

    @Test
    public void testSelectOnlyCertainColumns() throws Exception {
        Table table = new Table("Employee", eno);
        ArrayList<Column> cols = new ArrayList<Column>();
        cols.add(age); cols.add(eno);

        String expectedResult = age.getName() + " | " + eno.getName() + "\n";
        Record r = new Record();
        Field f1 = new Field(eno, "0");
        Field f2 = new Field(name, "Jake");
        Field f3 = new Field(age, "22");
        Field f4 = new Field(bday, "6/6/94");
        r.addField(f1); r.addField(f2); r.addField(f3); r.addField(f4);

        expectedResult += "22 | 0\n";

        Record r1 = new Record();
        Field g1 = new Field(eno, "1");
        Field g2 = new Field(name, "Gabby");
        Field g3 = new Field(age, "21");
        Field g4 = new Field(bday, "3/21/15");
        r1.addField(g1); r1.addField(g2); r1.addField(g3); r1.addField(g4);

        expectedResult += "21 | 1\n";

        table.addRecord(r); table.addRecord(r1);
        assertEquals(expectedResult, table.select(cols));
    }

    @Test
    public void testUpdate() throws Exception {
        // TODO
    }

    @Test
    public void testInsert() throws Exception {
        // TODO
    }

    @Test
    public void testDelete() throws Exception {
        // TODO
    }

    @AfterClass
    public static void tearDown() {
        eno = name = age = bday = null;
    }
}