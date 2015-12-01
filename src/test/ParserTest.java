import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by jakewilson on 11/25/15.
 */
public class ParserTest {

    @Test
    public void testSelect() {
        Command c = new DMLCommand(CommandType.SELECT);
        try {
            DMLCommand pc = (DMLCommand)Parser.parse("SELECT * FROM table;");
            assertEquals(c.getType(), pc.getType());
            assertEquals("table", pc.getSubject());
            assertEquals(true, pc.allColumns);
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            assertEquals("ParseException", "");
        }
    }

    @Test
    public void testSelectCaseInsensitive() {
        try {
            DMLCommand pc = (DMLCommand)Parser.parse("select * from NEWTABLE;"); // case does not matter
            assertEquals("NEWTABLE", pc.getSubject());
            assertEquals(CommandType.SELECT, pc.getType());
            assertEquals(true, pc.allColumns);
        } catch (ParseException pex) {
            assertEquals("ParseException", "");
        }
    }

    @Test
    public void testSelectCertainColumns() {
        try {
            DMLCommand pc = (DMLCommand)Parser.parse("select (name, eno) from table;"); // case does not matter
            ArrayList<String> names = pc.getColumnNames();

            assertEquals(CommandType.SELECT, pc.getType());
            assertEquals("table", pc.getSubject());

            assertEquals("name", names.get(0));
            assertEquals("eno", names.get(1));
        } catch (ParseException pex) {
            assertEquals("ParseException", "");
        }
    }

    @Test
    public void testBadSelect() {
        try {
            Parser.parse("SELECT ** From table;");
            assertEquals(false, true); // should fail
        } catch (ParseException pex) {
            assertEquals(true, true); // should fail
        }
    }

    @Test
    public void testInsert() {
        try {
            DMLCommand c = (DMLCommand)Parser.parse("INSERT INTO table VALUES ('hello', 'there', 56, 06/06/94);");
            assertEquals(CommandType.INSERT, c.getType());
            assertEquals(true, c.getColumnNames().isEmpty());

            assertEquals("hello", c.getInsertionValues().get(0));
            assertEquals("there", c.getInsertionValues().get(1));
            assertEquals("56", c.getInsertionValues().get(2));
            assertEquals("06/06/94", c.getInsertionValues().get(3));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            //assertEquals(false, true);
        }
    }

    @Test
    public void testInsertBad() {
        try {
            DMLCommand c = (DMLCommand)Parser.parse("INSERT INTO table VALUES ('hello, 'there', 56, 06/06/94);"); // missing single quote after hello
            assertEquals(false, true);
        } catch (ParseException pex) {
            assertEquals(true, true);
        }
    }

    @Test
    public void testInsertCertainColumns() {
        try {
            DMLCommand c = (DMLCommand)Parser.parse("INSERT INTO table (eno, name, age) VALUES (67, 89, 'hi', 'ok');"); // missing single quote after hello
            assertEquals(CommandType.INSERT, c.getType());

            assertEquals("eno", c.getColumnNames().get(0));
            assertEquals("name", c.getColumnNames().get(1));
            assertEquals("age", c.getColumnNames().get(2));

            assertEquals("67", c.getInsertionValues().get(0));
            assertEquals("89", c.getInsertionValues().get(1));
            assertEquals("hi", c.getInsertionValues().get(2));
            assertEquals("ok", c.getInsertionValues().get(3));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            assertEquals(false, true);
        }

    }

    @Test
    public void testCreateDatabase() {
        try {
            DDLCommand c = (DDLCommand)Parser.parse("CREATE dAtaBase job;");
            assertEquals(CommandType.CREATE_DB, c.getType());
            assertEquals("job", c.getSubject());
        } catch (ParseException pex) {
            assertEquals(false, true);
        }
    }

    @Test
    public void testCreateDatabaseInvalid() {
        try {
            DDLCommand c = (DDLCommand)Parser.parse("CREATE dAtaBase job");
            fail();
        } catch (ParseException pex) {
            assertEquals(true, true);
        }
    }

    @Test
    public void testCreateTable() {
        try {
            DDLCommand c = (DDLCommand)Parser.parse("CREATE table table(eno INTEGER(23));");
            assertEquals(CommandType.CREATE_TABLE, c.getType());
            assertEquals("table", c.getSubject());
            Column col = c.getColumn();
            assertEquals("eno", col.getName());
            assertEquals(Type.INTEGER, col.getFieldType().getType());
            assertEquals(23, col.getFieldType().getPrecision());
        } catch (ParseException pex) {
            fail();
        }
    }

    @Test
    public void testCreateTableMultipleParams() {
        try {
            DDLCommand c = (DDLCommand)Parser.parse("CREATE table table(eno1 INTEGER(15), name CHARACTER(10), age INTEGER);");
            assertEquals(CommandType.CREATE_TABLE, c.getType());
            assertEquals("table", c.getSubject());
            Column col = c.getColumn();
            assertEquals("eno1", col.getName());
            assertEquals(Type.INTEGER, col.getFieldType().getType());
            assertEquals(15, col.getFieldType().getPrecision());

            col = col.getNext();
            assertNotEquals(null, col);
            assertEquals("name", col.getName());
            assertEquals(Type.CHARACTER, col.getFieldType().getType());
            assertEquals(10, col.getFieldType().getPrecision());

            col = col.getNext();
            assertNotEquals(null, col);
            assertEquals("age", col.getName());
            assertEquals(Type.INTEGER, col.getFieldType().getType());
            assertEquals(5, col.getFieldType().getPrecision());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testCreateTableNumberAndDate() {
        try {
            DDLCommand c = (DDLCommand)Parser.parse("CREATE table employee (id INTEGER, salary NUMBER(10, 5), birthdate DATE);");
            assertEquals(CommandType.CREATE_TABLE, c.getType());
            assertEquals("employee", c.getSubject());

            Column col = c.getColumn();
            assertEquals("id", col.getName());
            assertEquals(Type.INTEGER, col.getFieldType().getType());
            assertEquals(FieldType.DEFAULT_PRECISION, col.getFieldType().getPrecision());

            col = col.getNext();
            assertNotEquals(null, col);
            assertEquals("salary", col.getName());
            assertEquals(Type.NUMBER, col.getFieldType().getType());
            assertEquals(10, col.getFieldType().getPrecision());
            assertEquals(5, col.getFieldType().getScale());

            col = col.getNext();
            assertNotEquals(null, col);
            assertEquals("birthdate", col.getName());
            assertEquals(Type.DATE, col.getFieldType().getType());
            assertEquals(5, col.getFieldType().getPrecision());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testLoadDatabase() {
        try {
            Command c = Parser.parse("LOAD DATABASE db;");
            assertEquals(CommandType.LOAD_DB, c.getType());
            assertEquals("db", c.getSubject());
            assertEquals(null, c.getColumn());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testLoadDatabaseBad() {
        try {
            Command c = Parser.parse("LOAD DATABASE");
        } catch (ParseException pex) {
            assertEquals("Syntax Error occurred.\n", pex.getMessage());
        }
    }

    @Test
    public void testSave() {
        try {
            Command c = Parser.parse("SAVE;");
            assertEquals(CommandType.SAVE_DB, c.getType());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }


    @Test
    public void testSaveCaseInsensitive() {
        try {
            Command c = Parser.parse("sAvE;");
            assertEquals(CommandType.SAVE_DB, c.getType());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testSaveBad() {
        try {
            Command c = Parser.parse("SAVE");
            fail();
        } catch (ParseException pex) {
        }
    }

    @Test
    public void testCommit() {
        try {
            Command c = Parser.parse("COMMIT;");
            assertEquals(CommandType.SAVE_DB, c.getType());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testCommitCaseInsensitive() {
        try {
            Command c = Parser.parse("cOMMit;");
            assertEquals(CommandType.SAVE_DB, c.getType());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testCommitBad() {
        try {
            Command c = Parser.parse("COMMIT");
            fail();
        } catch (ParseException pex) {
        }
    }

    @Test
    public void testDropDatabase() {
        try {
            Command c = Parser.parse("DROP DATABASE this;");
            assertEquals(CommandType.DELETE_DB, c.getType());
            assertEquals("this", c.getSubject());
            assertEquals(null, c.getColumn());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testDropDatabaseCaseInsensitive() {
        try {
            Command c = Parser.parse("dRop dataBASE next;");
            assertEquals(CommandType.DELETE_DB, c.getType());
            assertEquals("next", c.getSubject());
            assertEquals(null, c.getColumn());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testDropDatabaseBad() {
        try {
            Command c = Parser.parse("DROP DATABASE");
            fail();
        } catch (ParseException pex) {
        }
    }

    @Test
    public void testDropTable() {
        try {
            Command c = Parser.parse("DROP TABLE newtable;");
            assertEquals(CommandType.DROP_TABLE, c.getType());
            assertEquals("newtable", c.getSubject());
            assertEquals(null, c.getColumn());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testDropTableCaseInsensitive() {
        try {
            Command c = Parser.parse("dRop tabLE secondtable;");
            assertEquals(CommandType.DROP_TABLE, c.getType());
            assertEquals("secondtable", c.getSubject());
            assertEquals(null, c.getColumn());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testDropTableBad() {
        try {
            Command c = Parser.parse("DROP table");
            fail();
        } catch (ParseException pex) {
        }
    }

    @Test
    public void testWSelect() {
        try {
            Command c = Parser.parse("WSELECT * FROM selection;");
            assertEquals(CommandType.WSELECT, c.getType());
            assertEquals("selection", c.getSubject());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testUpdate() {
        try {
            Command c = Parser.parse("UPDATE table SET col = 5;");
            assertEquals(CommandType.UPDATE, c.getType());
            assertEquals("table", c.getSubject());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testDelete() {
        try {
            Command c = Parser.parse("DELETE FROM ok;");
            assertEquals(CommandType.DELETE, c.getType());
            assertEquals("ok", c.getSubject());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }


    @Test
    public void testUpdateWithCondition() {
        try {
            DMLCommand c = (DMLCommand)Parser.parse("UPDATE ok SET col3 = 16 WHERE col = col2;");
            assertEquals(CommandType.UPDATE, c.getType());
            assertEquals("ok", c.getSubject());
            assertEquals(Operator.EQUAL_TO, c.getCondition().operator);
            assertEquals("col", c.getCondition().operand1);
            assertEquals("col2", c.getCondition().operand2);
            assertEquals("col3", c.getColumnNames().get(0));
            assertEquals("16", c.getInsertionValues().get(0));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testDeleteWithCondition() {
        try {
            DMLCommand c = (DMLCommand)Parser.parse("DELETE FROM ok WHERE col >= col2;");
            assertEquals(CommandType.DELETE, c.getType());
            assertEquals("ok", c.getSubject());
            assertEquals(Operator.GREATER_THAN_OR_EQUAL_TO, c.getCondition().operator);
            assertEquals("col", c.getCondition().operand1);
            assertEquals("col2", c.getCondition().operand2);
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testUpdateWithConditionComplex() {
        try {
            DMLCommand c = (DMLCommand)Parser.parse("UPDATE ok SET col3 = 16 WHERE col = col2 AND col4 > 72000;");
            assertEquals(CommandType.UPDATE, c.getType());
            assertEquals("ok", c.getSubject());
            assertEquals(Operator.EQUAL_TO, c.getCondition().operator);
            assertEquals("col", c.getCondition().operand1);
            assertEquals("col2", c.getCondition().operand2);
            assertEquals("col3", c.getColumnNames().get(0));
            assertEquals("16", c.getInsertionValues().get(0));
            assertEquals(LogicalOperator.AND, c.getCondition().logicalOperator);

            Condition cond = c.getCondition().getNext();
            assertNotEquals(null, cond);
            assertEquals(Operator.GREATER_THAN, cond.operator);
            assertEquals("col4", cond.operand1);
            assertEquals("72000", cond.operand2);
            assertEquals(null, cond.getNext());
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

    @Test
    public void testUpdateWithConditionVeryComplex() {
        try {
            DMLCommand c = (DMLCommand)Parser.parse("UPDATE oiwk SET attr = 5 WHERE pno <> col21 AND joe < 42 OR bo <= 38.9;");
            assertEquals(CommandType.UPDATE, c.getType());
            assertEquals("oiwk", c.getSubject());
            assertEquals(Operator.NOT_EQUAL_TO, c.getCondition().operator);
            assertEquals("pno", c.getCondition().operand1);
            assertEquals("col21", c.getCondition().operand2);
            assertEquals("attr", c.getColumnNames().get(0));
            assertEquals("5", c.getInsertionValues().get(0));
            assertEquals(LogicalOperator.AND, c.getCondition().logicalOperator);

            Condition cond = c.getCondition().getNext();
            assertNotEquals(null, cond);
            assertEquals(Operator.LESS_THAN, cond.operator);
            assertEquals("joe", cond.operand1);
            assertEquals("42", cond.operand2);
            assertEquals(LogicalOperator.OR, cond.logicalOperator);

            cond = cond.getNext();
            assertNotEquals(null, cond);
            assertEquals(Operator.LESS_THAN_OR_EQUAL_TO, cond.operator);
            assertEquals("bo", cond.operand1);
            assertEquals("38.9", cond.operand2);
            assertEquals(null, cond.getNext());
            assertEquals(null, cond.logicalOperator);

        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
            fail();
        }
    }

}
