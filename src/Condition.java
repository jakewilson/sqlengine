import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;

/**
 * Created by jakewilson on 11/30/15.
 */
public class Condition {

    public String operand1, operand2;
    public Operator operator;

    public Type operand1Type, operand2Type;

    public LogicalOperator logicalOperator;

    private Condition next;

    public Condition(String operand1, String operand2, Operator operator, LogicalOperator logicalOperator) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operator = operator;
        this.logicalOperator = logicalOperator;
        operand1Type = determineType(operand1);
        operand2Type = determineType(operand2);
    }

    public void setNext(Condition c) {
        this.next = c;
    }
    
    public Condition getNext(){
    	return this.next;
    }

    /**
     * Determines the type of an operand
     * @param op the operand whose type to determine
     * @return the type of the operand
     */
    public Type determineType(String op) {
        try {
            Double.parseDouble(op);
            return Type.NUMBER;
        } catch (NumberFormatException nfex) {}

        try {
            Integer.parseInt(op);
            return Type.INTEGER;
        } catch (NumberFormatException nfex) {}

        return Type.CHARACTER;
    }

    /**
     * Evaluates a condition for a record r
     * @param r the record to evaluate the condition on
     * @return the result of the condition
     */
    public boolean evaluate(Record r) {
        return true; // TODO
    }

}

enum LogicalOperator {
    AND,
    OR
}

enum Operator {
    GREATER_THAN,
    LESS_THAN,
    EQUAL_TO,
    NOT_EQUAL_TO,
    GREATER_THAN_OR_EQUAL_TO,
    LESS_THAN_OR_EQUAL_TO
}
