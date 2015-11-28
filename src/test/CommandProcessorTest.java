import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CommandProcessorTest {

    private Catalog c;
    private Database d;
    private Table t;

    public static Column eno;
    public static Column name;
    public static Column age;
    public static Column bday;

    @Before
    public void setUp() {

        c = new Catalog();
        eno  = new Column("ENO", new FieldType(Type.INTEGER, 3), true);
        name = new Column("Name", new FieldType(Type.CHARACTER, 15), false);
        age  = new Column("Age", new FieldType(Type.INTEGER, 3), false);
        bday = new Column("Birthdate", new FieldType(Type.DATE), false);

        eno.setNext(name);
        name.setNext(age);
        age.setNext(bday);

        c.createDatabase("Work");
        d = c.getCurrent();
        d.createTable("Employee", eno);
        t = d.getTable("Employee");
    }

    @Test
    public void testExecuteSelectAll() throws Exception {
        try {
            Record r = new Record();
            Field f1 = new Field(eno, "0");
            Field f2 = new Field(name, "Jake");
            Field f3 = new Field(age, "22");
            Field f4 = new Field(bday, "6/6/94");
            r.addField(f1); r.addField(f2); r.addField(f3); r.addField(f4);
            t.addRecord(r);
            String expectedResult = eno.getName() + " | " + name.getName() + " | " + age.getName() + " | " + bday.getName() + "\n";
            expectedResult += "0 | Jake | 22 | 6/6/94\n";
            CommandProcessor cp = new CommandProcessor(c);
            String actual = cp.execute(Parser.parse("SELECT * FROM Employee;"));
            assertEquals(expectedResult, actual);
        } catch (ParseException pex) {
            assertEquals(false, true);
        }
    }

    @Test
    public void testExecuteSelectCertainColumns() throws Exception {
        try {
            Record r = new Record();
            Field f1 = new Field(eno, "0");
            Field f2 = new Field(name, "Jake");
            Field f3 = new Field(age, "22");
            Field f4 = new Field(bday, "6/6/94");
            r.addField(f1); r.addField(f2); r.addField(f3); r.addField(f4);
            t.addRecord(r);

            Record r1 = new Record();
            Field g1 = new Field(eno, "1");
            Field g2 = new Field(name, "MArtino");
            Field g3 = new Field(age, "87");
            Field g4 = new Field(bday, "9/22/87");
            r1.addField(g1); r1.addField(g2); r1.addField(g3); r1.addField(g4);
            t.addRecord(r1);

            String expectedResult = "birthdate | name | age\n";
            expectedResult += "6/6/94 | Jake | 22\n";
            expectedResult += "9/22/87 | MArtino | 87\n";

            CommandProcessor cp = new CommandProcessor(c);
            String actual = cp.execute(Parser.parse("SELECT (birthdate, name, age) FROM Employee;"));
            assertEquals(expectedResult, actual);
        } catch (ParseException pex) {
            assertEquals(false, true);
        }
    }

    @Test
    public void testInsert() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            String actual = cp.execute(Parser.parse("INSERT INTO employee VALUES (2, 'Joe', 92, 05/21/93);"));
            assertEquals("\n", actual);

            actual = cp.execute(Parser.parse("SELECT * FROM employee;"));
            String expected = eno.getName() + " | " + name.getName() + " | " + age.getName() + " | " + bday.getName() + "\n";
            expected += "2 | Joe | 92 | 05/21/93\n";
            assertEquals(expected, actual);
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testInsertCertainColumns() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            String actual = cp.execute(Parser.parse("INSERT INTO employee (eno, age) VALUES (2, 92);"));
            assertEquals("\n", actual);

            DMLCommand c = (DMLCommand)Parser.parse("SELECT * FROM employee;");
            ArrayList<String> names = c.getColumnNames();
            for (String s : names)
                System.out.println(s);
            actual = cp.execute(c);
            String expected = eno.getName() + " | " + name.getName() + " | " + age.getName() + " | " + bday.getName() + "\n";
            expected += "2 | null | 92 | null\n";
            assertEquals(expected, actual);
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testCreateDatabase() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("\n", cp.execute(Parser.parse("CREATE DATABASE joe;")));
            assertEquals(true, c.loadDatabase("joe"));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testCreateTable() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("\n", cp.execute(Parser.parse("CREATE TABLE atable (tabNum INTEGER NOT NULL, firstName CHARACTER(15));")));
            assertNotEquals(null, c.getCurrent().getTable("atable"));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }
}