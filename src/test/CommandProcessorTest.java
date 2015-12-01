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
            assertEquals("", actual);

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
            assertEquals("", actual);

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
            assertEquals("", cp.execute(Parser.parse("CREATE DATABASE joe;")));
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
            assertEquals("", cp.execute(Parser.parse("CREATE TABLE atable (tabNum INTEGER NOT NULL, firstName CHARACTER(15));")));
            assertNotEquals(null, c.getCurrent().getTable("atable"));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testCreateTableInsertAndSelect() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("", cp.execute(Parser.parse("CREATE TABLE worker (wno INTEGER, name CHARACTER(25));")));
            assertNotEquals(null, c.getCurrent().getTable("worker"));

            Column col = c.getCurrent().getTable("worker").getFirst();
            assertEquals("wno", col.getName());
            assertEquals(FieldType.DEFAULT_PRECISION, col.getFieldType().getPrecision());
            assertEquals(Type.INTEGER, col.getFieldType().getType());

            col = col.getNext();
            assertEquals("name", col.getName());
            assertEquals(25, col.getFieldType().getPrecision());
            assertEquals(Type.CHARACTER, col.getFieldType().getType());

            assertEquals("", cp.execute(Parser.parse("INSERT INTO worker VALUES (0, 'jakewilson');")));

            String expected = "wno | name\n0 | jakewilson\n";
            assertEquals(expected, cp.execute(Parser.parse("SELECT * FROM worker;")));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testLoadDatabase() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("", cp.execute(Parser.parse("CREATE DATABASE martino;")));
            assertEquals("", cp.execute(Parser.parse("LOAD DATABASE martino;")));
            assertEquals("ERROR: database 'hello' does not exist.\n", cp.execute(Parser.parse("LOAD DATABASE hello;")));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testCreateAlreadyExistingDatabase() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("", cp.execute(Parser.parse("CREATE DATABASE martino;")));
            assertEquals("ERROR: database 'martino' already exists.\n", cp.execute(Parser.parse("CREATE DATABASE martino;")));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void updateRows() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("", cp.execute(Parser.parse("CREATE DATABASE testUpdate;")));
            assertEquals("", cp.execute(Parser.parse("LOAD DATABASE testUpdate;")));
            assertEquals("", cp.execute(Parser.parse("CREATE TABLE employee (eno INTEGER, name CHARACTER(20));")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (0, 'jake');")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (1, 'bob');")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (2, 'nick');")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (3, 'gabby');")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (4, 'zack');")));

            String expected = "eno | name\n";
            expected += "5 | jake\n5 | bob\n5 | nick\n5 | gabby\n5 | zack\n";
            assertEquals("", cp.execute(Parser.parse("UPDATE employee SET eno = 5;")));
            assertEquals(expected, cp.execute(Parser.parse("SELECT * FROM employee;")));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void deleteRows() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("", cp.execute(Parser.parse("CREATE DATABASE 1234;")));
            assertEquals("", cp.execute(Parser.parse("LOAD DATABASE 1234;")));
            assertEquals("", cp.execute(Parser.parse("CREATE TABLE employee (eno INTEGER, name CHARACTER(20));")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (0, 'jake');")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (1, 'bob');")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (2, 'nick');")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (3, 'gabby');")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (4, 'zack');")));

            String expected = "eno | name\n";
            expected += "0 | jake\n1 | bob\n2 | nick\n3 | gabby\n4 | zack\n";
            assertEquals(expected, cp.execute(Parser.parse("SELECT * FROM employee;")));
            assertEquals("", cp.execute(Parser.parse("DELETE FROM employee;")));
            assertEquals("", cp.execute(Parser.parse("SELECT * FROM employee;")));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void updateRowsWhere() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("", cp.execute(Parser.parse("CREATE DATABASE testUpdate;")));
            assertEquals("", cp.execute(Parser.parse("LOAD DATABASE testUpdate;")));
            assertEquals("", cp.execute(Parser.parse("CREATE TABLE employee (eno INTEGER, name CHARACTER(20), iscostco INTEGER);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (0, 'jake', 0);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (1, 'bob', 0);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (2, 'nick', 1);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (3, 'gabby', 0);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (4, 'zack', 1);")));

            String expected = "eno | name\n";
            expected += "5 | jake\n5 | bob\n2 | nick\n5 | gabby\n4 | zack\n";
            assertEquals("", cp.execute(Parser.parse("UPDATE employee SET eno = 5 WHERE iscostco = 0;")));
            assertEquals(expected, cp.execute(Parser.parse("SELECT (eno, name) FROM employee;")));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void updateRowsWhereComplex() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("", cp.execute(Parser.parse("CREATE DATABASE testUpdate;")));
            assertEquals("", cp.execute(Parser.parse("LOAD DATABASE testUpdate;")));
            assertEquals("", cp.execute(Parser.parse("CREATE TABLE employee (eno INTEGER, name CHARACTER(20), iscostco INTEGER);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (0, 'jake', 1);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (1, 'bob', 0);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (2, 'nick', 1);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (3, 'gabby', 0);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (4, 'zack', 1);")));

            String expected = "eno | name\n";
            expected += "0 | jake\n5 | bob\n5 | nick\n5 | gabby\n5 | zack\n";
            assertEquals("", cp.execute(Parser.parse("UPDATE employee SET eno = 5 WHERE eno > 1 OR iscostco <> 1;")));
            assertEquals(expected, cp.execute(Parser.parse("SELECT (eno, name) FROM employee;")));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void updateRowsWhereMoreComplex() {
        try {
            CommandProcessor cp = new CommandProcessor(c);
            assertEquals("", cp.execute(Parser.parse("CREATE DATABASE testUpdate;")));
            assertEquals("", cp.execute(Parser.parse("LOAD DATABASE testUpdate;")));
            assertEquals("", cp.execute(Parser.parse("CREATE TABLE employee (eno INTEGER, name CHARACTER(20), iscostco INTEGER, salary INTEGER(10));")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (0, 'jake', 1, 15000);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (1, 'bob', 0, 99999);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (2, 'nick', 1, 9999999);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (3, 'gabby', 0, 666);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (4, 'zack', 1, 34);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (5, 'joe', 1, 3400);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (6, 'martino', 0, 42000000);")));
            assertEquals("", cp.execute(Parser.parse("INSERT INTO employee VALUES (7, 'helo', 1, 38123);")));

            String expected = "eno | name | iscostco | salary\n";
            expected += "0 | jake | 1 | 15000\n";
            expected += "99 | bob | 0 | 99999\n";
            expected += "2 | nick | 1 | 9999999\n";
            expected += "99 | gabby | 0 | 666\n";
            expected += "4 | zack | 1 | 34\n";
            expected += "5 | joe | 1 | 3400\n";
            expected += "99 | martino | 0 | 42000000\n";
            expected += "99 | helo | 1 | 38123\n";
            assertEquals("", cp.execute(Parser.parse("UPDATE employee SET eno = 99 WHERE eno > 5 OR iscostco <> 1 AND salary <= 99999;")));
            assertEquals(expected, cp.execute(Parser.parse("SELECT * FROM employee;")));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }
}