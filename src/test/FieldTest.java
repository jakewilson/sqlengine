import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class FieldTest {

    private static FieldType intType, numType, charType, dateType;
    private static int intPrecision = 5, numPrecision = 7, charPrecision = 8;
    private static int numScale = 3;
    private static Column intColumn, numColumn, charColumn, dateColumn;

    @BeforeClass
    public static void setUp() {
        intType = new FieldType(Type.INTEGER, intPrecision);
        numType = new FieldType(Type.NUMBER, numPrecision, numScale);
        charType = new FieldType(Type.CHARACTER, charPrecision);
        dateType = new FieldType(Type.DATE);

        intColumn = new Column("salary", intType, true);
        numColumn = new Column("wage", numType, true);
        charColumn = new Column("name", charType, true);
        dateColumn = new Column("birthdate", dateType, true);
    }

    @Test
    public void testSetValueIntValid() throws Exception {
        Field f = new Field(intColumn);

        assertEquals(true, f.setValue("99999"));
    }

    @Test
    public void testSetValueInvalidIntPrecision() throws Exception {
        Field f = new Field(intColumn);

        assertEquals(false, f.setValue("100000"));
    }

    @Test
    public void testSetValueInvalidInt() throws Exception {
        Field f = new Field(intColumn);

        assertEquals(false, f.setValue("12.5"));
    }

    @Test
    public void testSetValueNumber() throws Exception {
        Field f = new Field(numColumn);

        assertEquals(true, f.setValue("12.5"));
    }

    @Test
    public void testSetValueNumberInvalidScale() throws Exception {
        Field f = new Field(numColumn);

        assertEquals(false, f.setValue("1.1234"));
    }

    @Test
    public void testSetValueNumberValidScale() throws Exception {
        Field f = new Field(numColumn);

        assertEquals(true, f.setValue("112.123"));
    }

    @Test
    public void testSetValueNumberInvalidPrecision() throws Exception {
        Field f = new Field(numColumn);

        assertEquals(false, f.setValue("11234.123")); // one digit too many
    }

    @Test
    public void testSetValueNumberValidPrecision() throws Exception {
        Field f = new Field(numColumn);

        assertEquals(true, f.setValue("1123.123")); // one digit too many
    }

    @Test
    public void testSetValueCharacter() throws Exception {
        Field f = new Field(charColumn);

        assertEquals(true, f.setValue("lenis= 7"));
    }

    @Test
    public void testSetValueCharacterInvalidPrecision() throws Exception {
        Field f = new Field(charColumn);

        assertEquals(false, f.setValue("lenis = 7"));
    }

    @Test
    public void testSetValueDate() throws Exception {
        Field f = new Field(dateColumn);

        f.setValue("12/31/00");
        assertEquals(true, f.setValue("12/31/00"));
    }

    @Test
    public void testSetValueMonthInvalid() throws Exception {
        Field f = new Field(dateColumn);

        assertEquals(false, f.setValue("13/22/99"));
    }

    @Test
    public void testSetValueDayInvalid() throws Exception {
        Field f = new Field(dateColumn);

        assertEquals(false, f.setValue("12/33/99"));
    }

    @Test
    public void testSetValueMonthDayInvalid() throws Exception {
        Field f = new Field(dateColumn);

        assertEquals(false, f.setValue("2/30/93"));
    }

    @Test
    public void testSetValueMonthValid() throws Exception {
        Field f = new Field(dateColumn);

        assertEquals(true, f.setValue("2/29/93"));
    }

    @Test
    public void testGetValue() throws Exception {
        Field f = new Field(intColumn);

        f.setValue("74");
        assertEquals("74", f.getValue());
    }

    @Test
    public void testGetValueNull() throws Exception {
        Field f = new Field(charColumn);

        f.setValue("hello there!");
        assertEquals("", f.getValue());
    }

    @Test
    public void testGetColumn() throws Exception {
        Field f = new Field(intColumn);

        assertEquals(intColumn, f.getColumn());
    }

    @Test
    public void testIntegerSetValue() {
        Field f = new Field(new Column("col", new FieldType(Type.INTEGER), true));

        assertEquals(true, f.setValue("0"));
    }

    @AfterClass
    public static void tearDown() {
        intType = numType = charType = dateType = null;
        intColumn = numColumn = charColumn = dateColumn = null;
    }
}