import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class CommandProcessorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private Database d;
    private Table t;

    public static Column eno;
    public static Column name;
    public static Column age;
    public static Column bday;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));

        d = new Database("Work");
        eno  = new Column("ENO", new FieldType(Type.INTEGER, 3), true);
        name = new Column("Name", new FieldType(Type.CHARACTER, 15), false);
        age  = new Column("Age", new FieldType(Type.INTEGER, 3), false);
        bday = new Column("Birthdate", new FieldType(Type.DATE), false);

        eno.setNext(name);
        name.setNext(age);
        age.setNext(bday);

        d.createTable("Employee", eno);
        t = d.getTable("Employee");
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }

    @Test
    public void testExecute() throws Exception {
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
            CommandProcessor cp = new CommandProcessor(d);
            cp.execute(Parser.parse("SELECT * FROM Employee;"));

            assertEquals(expectedResult, outContent.toString());
        } catch (ParseException pex) {
            assertEquals(false, true);
        }
    }
}