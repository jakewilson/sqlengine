/**
 * Written by Michael Smith on 11/24/2015
 * Parses given input using an LL(1) parse with the wSQLx grammar shown in Grammar.txt
 * Throws a ParseException if an error is detected
 * Returns a command object if no error is detected
 */
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Parser {
	static final String[][] firstSets = {
			{},
			{"CREATE"},
			{"TABLE", "DATABASE"},
			{"DROP"},
			{"DATABASE"},
			{"DATABASE"},
			{"SAVE", "COMMIT"},
			{"LOAD"},
			{"TABLE"},
			{","},
			{"NAME"},
			{"INTEGER", "NUMBER", "CHARACTER", "DATE"},
			{"INTEGER"},
			{"CHARACTER"},
			{"(", ""},
			{"NUMBER"},
			{"("}, // 16
			{")", ","},
			{"NOT"},
			{"TABLE"},
			{"INSERT"},	
			{"("},
			{"NAME"},	
			{","}, // 23
			{"DATE_CONSTANT", "NUMBER_CONSTANT", "INTEGER_CONSTANT", "CHARACTER_CONSTANT"},
			{"DATE_CONSTANT", "NUMBER_CONSTANT", "INTEGER_CONSTANT", "CHARACTER_CONSTANT"},
			{","},
			{"DELETE"},
			{"WHERE"},
			{"NAME"},
			{"=", ">", "<", "<=", ">=", "<>"},
			{"NAME", "DATE_CONSTANT", "NUMBER_CONSTANT", "INTEGER_CONSTANT", "CHARACTER"},
			{"AND", "OR", ""},
			{"UPDATE"},
			{"NAME", "DATE_CONSTANT", "NUMBER_CONSTANT", "INTEGER_CONSTANT", "CHARACTER_CONSTANT"},
			{"NAME"},
			{","},	
			{"WUPDATE"},
			{"SELECT"}, // 38
			{"WSELECT"},
			{"NAME", "*"}
	};
	static final String[][] followSets = {
			{},//						0
			{},//						1
			{},//						2
			{},//						3
			{},//						4
			{},//						5
			{},//						6
			{},//						7
			{},//						8
			{")"},//					9
			{},//						10
			{},//						11
			{},//						12
			{},//						13
			{"NOT", ",", ")"},//		14
			{},//						15
			{"NOT", ",", ")"},//		16
			{},//						17
			{",", ")"},//				18
			{},//						19
			{},//						20
			{"VALUES"},//				21
			{},//						22
			{")"},//					23
			{},//						24
			{},//						25
			{")"},//					26
			{},//						27
			{";"},//					28
			{},//						29
			{},//						30
			{},//						31
			{";"},//					32
			{},//						33
			{},//						34
			{},//						35
			{"WHERE", ";"},//			36
			{},//						37
			{},//						38
			{},//						39
			{}//						40
	};
	static final int DEFAULT_PRECISION = 4;
	static final int DEFAULT_SCALE = 12;
	static final boolean debug = false;
	
	static String[] input;	
	static int line = 0;//iterator

	static boolean allColumns = true;
	static boolean inCondition = false;

	static ArrayList<String> fieldNames = new ArrayList<String>();
	static ArrayList<String> insertionValues = new ArrayList<String>();
	
	//Below variables are used as temporary holding spaces to piece together a command object
	static Command command = null;
	static Column column = null;
	static boolean notNull = false;
	static String columnName;
	static String lastType;
	static FieldType dataType;
	static int scale = FieldType.DEFAULT_SCALE;
	static int precision = FieldType.DEFAULT_PRECISION;
	static Condition condition = null;
	static String operandA = null;
	static String operandB = null;
	static Operator relOp = null;
	static LogicalOperator logicOp = null;
	static String setColumn = null;
	
	/**
	 * Tokenizes the command input string and performs an LL(1) parse using a grammar for wSQLx (listed in Grammar.txt)
	 * @param inputString the string to be parsed
	 * @return command object representing the input string command
	 * @throws ParseException
	 */
	public static Command parse(String inputString) throws ParseException{
		input = LexicalAnalyzer.tokenize(inputString);
		if(input == null) //LexicalAnalyzer will return null if it detects an error in the input tokens
			throw new ParseException("bad input");

		line = 0;
		column = null;
		fieldNames.clear();
		insertionValues.clear();
		scale = FieldType.DEFAULT_SCALE;
		precision = FieldType.DEFAULT_PRECISION;
		condition = null;
		relOp = null;
		logicOp = null;
		setColumn = null;

		if(debug)
			printTokens();//debug
		
		commands();//Start state
		if(command.isDML())
			((DMLCommand)command).setCondition(condition);
		
		return command;
	}//parse
	
	/*
	 * -------------------------------
	 * 			Grammar Rules
	 * -------------------------------
	 */
	
	static void commands() throws ParseException{// 0 Start symbol
		if(inFirst(1))
			createA();
		else if(inFirst(3))
			dropA();
		else if(inFirst(6))
			save();
		else if(inFirst(7))
			load();
		else if(inFirst(20))
			insert();
		else if(inFirst(27))
			delete();
		else if(inFirst(33))
			update();
		else if(inFirst(37))
			wUpdate();
		else if(inFirst(38) || inFirst(39))
			select();
		else
			throw new ParseException("Unrecognized command: " + getToken(line));
		return;
	}//command
	
	static void createA() throws ParseException{// 1
		checkToken("CREATE");
		createB();
		return;
	}//createA
	
	static void createB() throws ParseException{// 2
		if(inFirst(5))
			database(true);
		else if(inFirst(8))
			createTable();
		return;
	}//createB
	
	static void dropA() throws ParseException{// 3
		checkToken("DROP");
		dropB();
		return;
	}//dropA
	
	static void dropB() throws ParseException{// 4
		if(inFirst(5))
			database(false);			
		
		else if(inFirst(19))
			dropTable();
		return;
	}//dropB
	
	static void database(boolean createCommand) throws ParseException{// 5
		checkToken("DATABASE");

		String DBName = getToken(line++);

		if(createCommand)
			command = new DDLCommand(CommandType.CREATE_DB, DBName);
		else
			command = new DDLCommand(CommandType.DELETE_DB, DBName);

		checkToken(";");
		return;
	}//database
	
	static void save() throws ParseException{// 6
		if(getToken(line).equalsIgnoreCase("SAVE") || getToken(line).equalsIgnoreCase("COMMIT")){//I did not use the checkToken method here because either SAVE or COMMIT is valid
			line++;
			command = new DDLCommand(CommandType.SAVE_DB);
			checkToken(";");
		}//if
		else
			reject();
		return;
	}//save
	
	static void load() throws ParseException{// 7
		checkToken("LOAD");
		checkToken("DATABASE");

		String DBName = getToken(line++);

		command = new DDLCommand(CommandType.LOAD_DB, DBName);
		
		checkToken(";");
		return;
	}//load
	
	static void createTable() throws ParseException{// 8
		checkToken("TABLE");

		String tableName = getToken(line++);

		checkToken("(");
		fieldDefList();
		checkToken(")");
		command = new DDLCommand(CommandType.CREATE_TABLE, tableName, column);
		checkToken(";");
		return;
	}//createTable
	
	static void fieldDefList() throws ParseException{// 9
		parseColumn();
		while (inFirst(23)) {
			line++;
			parseColumn();
		}//while
		return;
	}//fieldDefList

	static void parseColumn() throws ParseException {
		columnName = getToken(line++);

		type();
		constraints();
		if(getLastValue().equals("NULL"))
			notNull = true;

		appendColumn();
	}//parseColumn
	
	static void fieldDef() throws ParseException{// 10

		fieldDefList();
		return;
	}//fieldDef
	
	static void type() throws ParseException{// 11
		if(inFirst(12))
			integer();
		else if(inFirst(13))
			character();
		else if(inFirst(15))
			number();
		else{
			checkToken("DATE");
			lastType = "DATE";
		}
		return;
	}//type
	
	static void integer() throws ParseException{// 12
		checkToken("INTEGER");
		lastType = "INTEGER";
		param1();
		return;
	}//integer
	
	static void character() throws ParseException{// 13
		checkToken("CHARACTER");
		lastType = "CHARACTER";
		param1();
		return;
	}//character
	
	static void param1() throws ParseException{// 14
		if(inFollow(14))
			return;
		checkToken("(");
		checkToken("INTEGER_CONSTANT");
		precision = Integer.parseInt(getLastValue());
		checkToken(")");
		return;
	}//param1
	
	static void number() throws ParseException{// 15
		checkToken("NUMBER");
		lastType = "NUMBER";
		param2A();
		return;
	}//number
	
	static void param2A() throws ParseException{// 16
		if(inFollow(16))
			return;
		checkToken("(");
		checkToken("INTEGER_CONSTANT");
		precision = Integer.parseInt(getLastValue());
		param2B();
		return;
	}//param2A
	
	static void param2B() throws ParseException{// 17
		if(getToken(line).equals(")")){
			line++;
			return;
		}
		checkToken(",");
		checkToken("INTEGER_CONSTANT");
		scale = Integer.parseInt(getLastValue());
		checkToken(")");
		return;
	}//param2B
	
	static void constraints() throws ParseException{// 18
		if(inFollow(18))
			return;
		checkToken("NOT");
		checkToken("NULL");
		return;
	}//constraints
	
	static void dropTable() throws ParseException{// 19
		checkToken("TABLE");
		String tableName = getToken(line++);
		command = new DDLCommand(CommandType.DROP_TABLE, tableName);
		checkToken(";");
		return;
	}//dropTable
	
	static void insert() throws ParseException{// 20
		checkToken("INSERT");
		checkToken("INTO");
		String tableName = getToken(line++);
		fieldList();
		checkToken("VALUES");
		checkToken("(");
		insertionValues.clear();
		literals();
		checkToken(")");
		checkToken(";");
		command = new DMLCommand(CommandType.INSERT, tableName);
		((DMLCommand)command).setColumnNames(fieldNames);
		((DMLCommand)command).setInsertionValues(insertionValues);
		return;
	}//insert
	
	static void fieldList() throws ParseException{// 21
		fieldNames.clear();
		if(!inFirst(16))
			return;
		checkToken("(");
		fields();
		while (inFirst(23)) {
			line++;
			fields();
		}
		checkToken(")");
		return;
	}//fieldList
	
	static void fields() throws ParseException{// 22
		fieldNames.add(getToken(line++).toLowerCase());
	}//fields
	
	static void nextField() throws ParseException{// 23
		if(inFollow(23))
			return;
		checkToken("NAME");
		fields();
		return;
	}//nextField
	
	static void literals() throws ParseException{// 24
		literal();
		nextLiteral();
		return;
	}//literals
	
	static void literal() throws ParseException{// 25
		String currentToken = null;
		StringTokenizer tokenizer = new StringTokenizer(getToken(line), " ");

		if(getToken(line).contains(" ")){//This is for tokens like DATE which is in the format "DATE MM DD [YY]YY"
			currentToken = tokenizer.nextToken();
		}//if
		
		else
			currentToken = getToken(line);
		
		if  (currentToken.equals("INTEGER_CONSTANT") ||
			(currentToken.equals("NUMBER_CONSTANT"))    ||
			(currentToken.equals("CHARACTER_CONSTANT"))) {
			String value = tokenizer.nextToken();

			if (currentToken.equals("CHARACTER_CONSTANT"))
				while (tokenizer.hasMoreElements())
					value += " " + tokenizer.nextToken();

			insertionValues.add(value);
		}
		else if (currentToken.equalsIgnoreCase("DATE_CONSTANT")) {
			String date = tokenizer.nextToken();
			date += "/" + tokenizer.nextToken();
			date += "/" + tokenizer.nextToken();
			insertionValues.add(date);
		}
		else
			reject();
		line++;
		return;
	}//literal
	
	static void nextLiteral() throws ParseException{// 26
		if(inFollow(26))
			return;
		checkToken(",");
		literals();
		return;
	}//nextLiteral

	static void delete() throws ParseException{// 27
		checkToken("DELETE");
		checkToken("FROM");
		String tableName = getToken(line++);
		where();
		command = new DMLCommand(CommandType.DELETE, tableName);
		((DMLCommand)command).setColumnNames(fieldNames);
		checkToken(";");
		return;
	}//delete
	
	static void where() throws ParseException{// 28
		if(!inFirst(28))
			return;
		checkToken("WHERE");
		condition();
		while (!inFollow(32)) {
			condition();
		}
		return;
	}//where
	
	static void condition() throws ParseException{// 29
		inCondition = true;
		operand();
		relop();
		operand();
		inCondition = false;
		assignLogicOp();
		addCondition();
		return;
	}//condition
	
	static void relop() throws ParseException{// 30
		if(getToken(line).equals("=")){
			line++;
			if(inCondition)
				relOp = Operator.EQUAL_TO;
		}//if
		else if(getToken(line).equals("<")){
			line++;
			if(inCondition)
				relOp = Operator.LESS_THAN;
		}//else if
		else if(getToken(line).equals(">")){
			line++;
			if(inCondition)
				relOp = Operator.GREATER_THAN;
		}//else if
		else if(getToken(line).equals("<=")){
			line++;
			if(inCondition)
				relOp = Operator.LESS_THAN_OR_EQUAL_TO;
		}//else if
		else if(getToken(line).equals(">=")){
			line++;
			if(inCondition)
				relOp = Operator.GREATER_THAN_OR_EQUAL_TO;
		}//else if
		else if(getToken(line).equals("<>")){
			line++;
			if(inCondition)
				relOp = Operator.NOT_EQUAL_TO;
		}//else if
		else
			reject();
		return;
	}//relop
	
	static void operand() throws ParseException{// 31
		String currentToken = null;

		if(getToken(line).contains(" ")){//Only literals will contain a space
			StringTokenizer tokenizer = new StringTokenizer(getToken(line), " ");
			currentToken = tokenizer.nextToken();
		}//if
		else{
			currentToken = getToken(line++);
		}//else
		
		if(currentToken.equals("INTEGER_CONSTANT")){
			line++;
			currentToken = getLastValue();
		}//if
		else if(currentToken.equals("NUMBER_CONSTANT")){
			line++;
			currentToken = getLastValue();
		}//else if
		else if(currentToken.equals("CHARACTER_CONSTANT"))
			currentToken = formatString(getToken(line++));
		else if(currentToken.equals("DATE_CONSTANT"))
			currentToken = formatDate(getToken(line++));

		if (!inCondition)
			setColumn = currentToken;

		if(inCondition && operandA == null)
			operandA = currentToken;
		
		else if(inCondition && operandB == null)
			operandB = currentToken;

		return;
	}//operand
	
	static void assignLogicOp() throws ParseException{// 32
		if(getToken(line).equals("OR")){
			line++;
			logicOp = LogicalOperator.OR;
		}//if
		else if(getToken(line).equals("AND")){
			line++;
			logicOp = LogicalOperator.AND;
		}//else if
		return;
	}//conditionList
	
	static void update() throws ParseException{// 33
		checkToken("UPDATE");
		String tableName = getToken(line++);
		checkToken("SET");
		setList();
		where();		
		command = new DMLCommand(CommandType.UPDATE, tableName);
		((DMLCommand)command).setColumnNames(fieldNames);
		((DMLCommand)command).setInsertionValues(insertionValues);
		checkToken(";");
		return;
	}//update
	
	static void expression() throws ParseException{// 34
		operand();
		return;
	}//expression
	
	static void setList() throws ParseException{// 35
		fieldNames.add(getToken(line++));
		checkToken("=");
		expression();
		insertionValues.add(setColumn);
		return;
	}//setList
	
	static void wUpdate() throws ParseException{// 37
		checkToken("WUPDATE");
		String tableName = getToken(line++);
		checkToken("SET");
		setList();
		where();
		command = new DMLCommand(CommandType.UPDATE, tableName);
		((DMLCommand)command).setColumnNames(fieldNames);
		((DMLCommand)command).setInsertionValues(insertionValues);
		checkToken(";");
		return;
	}//wUpdate

	static void select() throws ParseException{// 38
		CommandType type = CommandType.SELECT;
		if (getToken(line).equalsIgnoreCase("WSELECT"))
			type = CommandType.WSELECT;

		checkToken(getToken(line));
		selectParams();
		checkToken("FROM");
		String tableName = getToken(line++);
		where();
		command = new DMLCommand(type, tableName);
		((DMLCommand)command).setColumnNames(fieldNames);
		((DMLCommand)command).allColumns = allColumns;
		allColumns = false;
		checkToken(";");
		return;
	}//select
	
	static void wSelect() throws ParseException{// 39
		checkToken("WSELECT");
		selectParams();
		checkToken("FROM");
		checkToken("NAME");
		String tableName = getLastValue();
		where();
		command = new DMLCommand(CommandType.SELECT, tableName);
		((DMLCommand)command).setColumnNames(fieldNames);
		((DMLCommand)command).allColumns = allColumns;
		checkToken(";");
		return;
	}//wSelect
	
	static void selectParams() throws ParseException{// 40
		if(getToken(line).equals("*")){
			allColumns = true;
			line++;
			return;
		}
		else {
			allColumns = false;
			fieldList();
		}

		return;
	}//selectParams
	
	/*
	 * --------------------------------------
	 * 			Auxiliary Functions
	 * --------------------------------------
	 */	
	
	private static boolean inFirst(int nonTerminal) throws ParseException {//returns true if the current token is in the first set of the given non-terminal
		String valueToCheck = null;
		
		if(getToken(line).contains(" ")){//This is for tokens like DATE which is in the format "DATE MM DD [YY]YY"
			StringTokenizer tokenizer = new StringTokenizer(getToken(line), " ");
			valueToCheck = tokenizer.nextToken();
		}//if
		
		else
			valueToCheck = getToken(line);
		
		for(int i = 0; i < firstSets[nonTerminal].length; i++){
			if(firstSets[nonTerminal][i].equalsIgnoreCase(valueToCheck))
				return true;
		}//for
		
		return false;
	}//inFirst
	
	private static boolean inFollow(int nonTerminal) throws ParseException {//returns true if the current token is in the follow set of the given non-terminal
		String valueToCheck = null;
		
		if(getToken(line).contains(" ")){//This is for tokens like DATE which is in the format "DATE MM DD [YY]YY"
			StringTokenizer tokenizer = new StringTokenizer(getToken(line), " ");
			valueToCheck = tokenizer.nextToken();
		}//if
		
		else
			valueToCheck = getToken(line);
		
		for(int i = 0; i < followSets[nonTerminal].length; i++){
			if(followSets[nonTerminal][i].equalsIgnoreCase(valueToCheck))
				return true;
		}//for
		return false;
	}//inFollow
	
	private static void printTokens(){//This method is used to test the Lexer
		for(int i = 0; i < input.length; i++)
			System.out.println(input[i]);
	}//printInput

	private static String formatDate(String dateToken){//converts date string from "DATE MM DD YY[YY]" to "MM/DD/[YY]YY"
		StringBuilder formattedString = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(dateToken, " ");
		tokenizer.nextToken();//skip the "DATE" token
		formattedString.append(tokenizer.nextToken() + "/");// MM/
		formattedString.append(tokenizer.nextToken() + "/");// MM/DD/
		formattedString.append(tokenizer.nextToken());		// MM/DD/[YY]YY

		return formattedString.toString();
	}//formatDate

	private static void reject() throws ParseException{
		throw new ParseException();
	}//reject

	private static void reject(String expected, String actual) throws ParseException {
		throw new ParseException("Syntax Error\nExpected: " + expected + " actual: " + actual);
	}//reject String String

	private static void checkToken(String expectedValue) throws ParseException{
		String valueToCheck = null;

		if (line >= input.length)
			throw new ParseException("expected '"+ expectedValue + "'");
		
		if(getToken(line).contains(" ")){//This is for tokens like DATE which is in the format "DATE MM DD [YY]YY"
			StringTokenizer tokenizer = new StringTokenizer(getToken(line), " ");
			valueToCheck = tokenizer.nextToken();
		}//if
		
		else
			valueToCheck = getToken(line);
		
		if(valueToCheck.equalsIgnoreCase(expectedValue))
			line++;
		else
			reject(expectedValue, valueToCheck);
		
		return;
	}//checkToken

	private static String getLastValue() throws ParseException {//returns the value of the last token
		StringBuilder output = new StringBuilder();
		
		if(getToken(line - 1).contains(" ")){//This is for tokens with labels
			StringTokenizer tokenizer = new StringTokenizer(getToken(line - 1), " ");
			tokenizer.nextToken();//skip the token label
			
			for(int i = 1; i <= tokenizer.countTokens(); i++)
				output.append(tokenizer.nextToken());
		}//if
		
		else
			output.append(getToken(line - 1));
		
		return output.toString();
	}//getLastCharacterSequence
	
	private static Column getLastColumn(){//returns the last column in the linked list
		Column lastColumn = column;
		while(lastColumn.getNext() != null){
			lastColumn = lastColumn.getNext();
		}//while
		return lastColumn;
	}//getLastColumn
	
	private static void appendColumn(){//adds a column to the end of the linked list
		setFieldType();
		
		if(column == null)
			column = new Column(columnName, dataType, notNull);
		else
			getLastColumn().setNext(new Column(columnName, dataType, notNull));
		
		resetColumnData();		
		return;
	}//appendColumn
	
	private static void resetColumnData(){//resets all column values to default
		columnName = null;
		dataType = null;
		notNull = false;
		precision = FieldType.DEFAULT_PRECISION;
		scale = FieldType.DEFAULT_SCALE;
	}//resetColumnData
	
	private static void setFieldType(){//sets the class variable dataType according to other class variables
		if(lastType.equals("CHARACTER"))
			dataType = new FieldType(Type.CHARACTER, precision);
		else if(lastType.equals("INTEGER"))
			dataType = new FieldType(Type.INTEGER, precision);
		else if(lastType.equals("NUMBER"))
			dataType = new FieldType(Type.NUMBER, precision, scale);
		else if(lastType.equals("DATE"))
			dataType = new FieldType(Type.DATE);
		else
			System.out.println("Error determining type " + lastType);
		return;
	}//setFieldType

	private static String getToken(int line) throws ParseException {
		if (line < 0 || line >= input.length)
			throw new ParseException();

		return input[line];
	}//getToken
	
	private static String formatString(String rawString){//adds single quotes to string tokens, does nothing to other tokens
		StringTokenizer tokenizer = new StringTokenizer(rawString, " ");
		if(tokenizer.nextToken().equals("CHARACTER_CONSTANT")){
			String retVal = "";
			for(int i = 1; i < tokenizer.countTokens(); i++)
				retVal += tokenizer.nextToken();
			return retVal;
		}//if
		else
			return rawString;
	}//formatString
	
	private static void addCondition(){//adds the current condition to the end of the linked list
		if(condition == null)
			condition = new Condition(operandA, operandB, relOp, logicOp);
		else{
			Condition currentCondition = condition;
			while(currentCondition.getNext() != null)
				currentCondition = currentCondition.getNext();
			Condition nextCondition = new Condition(operandA, operandB, relOp, logicOp);
			currentCondition.setNext(nextCondition);
		}//else
		operandA = null;
		operandB = null;
		relOp = null;
		logicOp = null;
		return;
	}//addCondition
	
}//class Parser
