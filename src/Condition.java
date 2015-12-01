
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
    private Type determineType(String op) {
        op = op.trim();
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
        String o1 = operand1, o2 = operand2;
        if (operand1Type == Type.CHARACTER) {
            if (r.getField(operand1) == null) {
                System.out.println("ERROR: bad column name: " + operand1);
                return false;
            }

            o1 = r.getField(operand1).getValue();
        }

        if (operand2Type == Type.CHARACTER) {
            if (r.getField(operand2) == null) {
                System.out.println("ERROR: bad column name: " + operand2);
                return false;
            }

            o2 = r.getField(operand2).getValue();
        }

        boolean ret = false;

        switch (operator) {
            case LESS_THAN:
                ret = o1.compareTo(o2) < 0;
                break;

            case LESS_THAN_OR_EQUAL_TO:
                ret = o1.compareTo(o2) <= 0;
                break;

            case GREATER_THAN:
                ret = o1.compareTo(o2) > 0;
                break;

            case GREATER_THAN_OR_EQUAL_TO:
                ret = o1.compareTo(o2) >= 0;
                break;

            case EQUAL_TO:
                ret = o1.compareTo(o2) == 0;
                break;

            case NOT_EQUAL_TO:
                ret = o1.compareTo(o2) != 0;
                break;
        }

        if (getNext() == null)
            return ret;
        else if (logicalOperator == LogicalOperator.AND)
            return ret && getNext().evaluate(r);
        else if (logicalOperator == LogicalOperator.OR)
            return ret || getNext().evaluate(r);

        return true;
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
