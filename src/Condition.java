
/**
 * Created by jakewilson on 11/30/15.
 */
public class Condition {

    public String operand1, operand2;
    public Operator operator;

    public LogicalOperator logicalOperator;

    private Condition next;

    public Condition(String operand1, String operand2, Operator operator, LogicalOperator logicalOperator) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operator = operator;
        this.logicalOperator = logicalOperator;
    }

    public void setNext(Condition c) {
        this.next = c;
    }
    
    public Condition getNext(){
    	return this.next;
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
