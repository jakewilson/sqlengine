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
			{"TABLE", "DATABASE"},
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
			{"SELECT"},
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
	static String[] input;
	static final int DEFAULT_PRECISION = 4;
	static final int DEFAULT_SCALE = 12;
	
	static int line = 0;//iterator

	static boolean debug = false;
	static boolean allColumns = true;

	static ArrayList<String> fieldNames = new ArrayList<String>();
	
	//Below variables are used as temporary holding spaces to piece together a command object
	static Command command = null;
	static Column column = null;
	static boolean notNull = false;
	static String columnName;
	static String lastType;
	static FieldType dataType;
	static int scale;
	static int precision;
	
	/*public static void main(String[] args){		//----This method is for testing only and will not appear in the final version----
		if(args.length == 0){
			System.err.println("No arguments given.");
			return;
		}//if no args given
		try{
			parse(args[0]);							//----This method is for testing only and will not appear in the final version----
		}
		catch(ParseException e){
			e.printStackTrace();
			System.out.println("\nREJECT");
			return;
		}
		System.out.println("\nACCEPT");
		return;
	}//main*/										//----This method is for testing only and will not appear in the final version----
	
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
		
		printTokens();//debug
		
		commands();//Start state
		
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
		else if(inFirst(38))
			select();
		else if(inFirst(39))
			wSelect();
		return;
	}//command
	
	static void createA() throws ParseException{// 1
		checkToken("CREATE");
		createB();
		return;
	}//deleteStatement
	
	static void createB() throws ParseException{// 2
		if(inFirst(5))
			database(true);
		else if(inFirst(8))
			createTable();
		return;
	}//deleteStatement
	
	static void dropA() throws ParseException{// 3
		checkToken("DROP");
		dropB();
		return;
	}//deleteStatement
	
	static void dropB() throws ParseException{// 4
		if(inFirst(5))
			database(false);			
		
		else if(inFirst(19))
			dropTable();
		return;
	}//deleteStatement
	
	static void database(boolean createCommand) throws ParseException{// 5
		checkToken("DATABASE");
		checkToken("NAME");
		
		String DBName = getLastValue();
		if(createCommand)
			command = new DDLCommand(CommandType.CREATE_DB, DBName);
		else
			command = new DDLCommand(CommandType.DELETE_DB, DBName);
		
		checkToken(";");
		return;
	}//deleteStatement
	
	static void save() throws ParseException{// 6
		if(input[line].equals("SAVE") || input[line].equals("COMMIT")){//I did not use the checkToken method here because either SAVE or COMMIT is valid
			line++;
			command = new DDLCommand(CommandType.SAVE_DB);
			checkToken(";");
		}//if
		else
			reject();
		return;
	}//deleteStatement
	
	static void load() throws ParseException{// 7
		checkToken("LOAD");
		checkToken("DATABASE");
		checkToken("NAME");
		
		String DBName = getLastValue();

		command = new DDLCommand(CommandType.LOAD_DB, DBName);
		
		checkToken(";");
		return;
	}//deleteStatement
	
	static void createTable() throws ParseException{// 8
		checkToken("TABLE");
		checkToken("NAME");
		
		String tableName = getLastValue();
		
		fieldDef();
		command = new DDLCommand(CommandType.CREATE_TABLE, tableName, column);
		checkToken(";");
		return;
	}//deleteStatement
	
	static void fieldDefList() throws ParseException{// 9
		if(inFollow(9))
			return;
		checkToken(",");
		fieldDef();
		return;
	}//deleteStatement
	
	static void fieldDef() throws ParseException{// 10
		checkToken("NAME");
		
		columnName = getLastValue();
		
		type();
		constraints();
		if(getLastValue().equals("NULL"))
			notNull = true;
		
		appendColumn();
		
		fieldDefList();
		return;
	}//deleteStatement
	
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
	}//deleteStatement
	
	static void integer() throws ParseException{// 12
		checkToken("INTEGER");
		lastType = "INTEGER";
		param1();
		return;
	}//deleteStatement
	
	static void character() throws ParseException{// 13
		checkToken("CHARACTER");
		lastType = "CHARACTER";
		param1();
		return;
	}//deleteStatement
	
	static void param1() throws ParseException{// 14
		if(inFollow(14))
			return;
		checkToken("(");
		checkToken("INTEGER_CONSTANT");
		scale = Integer.parseInt(getLastValue());
		checkToken(")");
		return;
	}//deleteStatement
	
	static void number() throws ParseException{// 15
		checkToken("NUMBER");
		lastType = "NUMBER";
		param2A();
		return;
	}//deleteStatement
	
	static void param2A() throws ParseException{// 16
		if(inFollow(16))
			return;
		checkToken("(");
		checkToken("INTEGER_CONSTANT");
		scale = Integer.parseInt(getLastValue());
		param2B();
		return;
	}//deleteStatement
	
	static void param2B() throws ParseException{// 17
		if(input[line].equals(")")){
			line++;
			return;
		}
		checkToken(",");
		checkToken("INTEGER_CONSTANT");
		precision = Integer.parseInt(getLastValue());
		return;
	}//deleteStatement
	
	static void constraints() throws ParseException{// 18
		if(inFollow(18))
			return;
		checkToken("NOT");
		checkToken("NULL");
		return;
	}//deleteStatement
	
	static void dropTable() throws ParseException{// 19
		checkToken("TABLE");
		checkToken("NAME");
		String tableName = getLastValue();
		command = new DDLCommand(CommandType.DROP_TABLE, tableName);
		checkToken(";");
		return;
	}//deleteStatement
	
	static void insert() throws ParseException{// 20
		checkToken("INSERT");
		checkToken("INTO");
		String tableName = input[line++];
		fieldList();
		checkToken("VALUES");
		checkToken("(");
		literals();
		checkToken(")");
		checkToken(";");
		command = new DMLCommand(CommandType.INSERT, tableName);//TODO Add insert params
		return;
	}//deleteStatement
	
	static void fieldList() throws ParseException{// 21
		if(!inFirst(16))
			return;
		fieldNames.clear();
		checkToken("(");
		fields();
		while (inFirst(23)) {
			line++;
			fields();
		}
		checkToken(")");
		return;
	}
	
	static void fields() throws ParseException{// 22
		fieldNames.add(input[line++]);
	}
	
	static void nextField() throws ParseException{// 23
		if(inFollow(23))
			return;
		checkToken("NAME");
		fields();
		return;
	}//deleteStatement
	
	static void literals() throws ParseException{// 24
		literal();
		nextLiteral();
		return;
	}//deleteStatement
	
	static void literal() throws ParseException{// 25
		String currentToken = null;
		
		if(input[line].contains(" ")){//This is for tokens like DATE which is in the format "DATE MM DD [YY]YY"
			StringTokenizer tokenizer = new StringTokenizer(input[line], " ");
			currentToken = tokenizer.nextToken();
		}//if
		
		else
			currentToken = input[line];
		
		if(currentToken.equals("INTEGER_CONSTANT"))
			line++;
		else if(currentToken.equals("NUMBER_CONSTANT"))
			line++;
		else if(currentToken.equals("CHARACTER_CONSTANT"))
			line++;
		else if(currentToken.equals("DATE_CONSTANT"))
			line++;
		else
			reject();
		return;
	}//deleteStatement
	
	static void nextLiteral() throws ParseException{// 26
		if(inFollow(26))
			return;
		checkToken(",");
		literals();
		return;
	}//deleteStatement

	static void delete() throws ParseException{// 27
		checkToken("DELETE");
		checkToken("FROM");
		checkToken("NAME");
		String tableName = getLastValue();
		where();
		command = new DMLCommand(CommandType.DELETE, tableName); //TODO Add params
		checkToken(";");
		return;
	}//deleteStatement
	
	static void where() throws ParseException{// 28
		if(!inFirst(28))
			return;
		checkToken("WHERE");
		condition();
		return;
	}//deleteStatement
	
	static void condition() throws ParseException{// 29
		checkToken("NAME");
		relop();
		operand();
		conditionList();
		return;
	}//deleteStatement
	
	static void relop() throws ParseException{// 30
		if(input[line].equals("="))
			line++;
		else if(input[line].equals("<"))
			line++;
		else if(input[line].equals(">"))
			line++;
		else if(input[line].equals("<="))
			line++;
		else if(input[line].equals(">="))
			line++;
		else if(input[line].equals("<>"))
			line++;
		else
			reject();
		return;
	}//deleteStatement
	
	static void operand() throws ParseException{// 31
		String currentToken = null;
		
		if(input[line].contains(" ")){//This is for tokens like DATE which is in the format "DATE MM DD [YY]YY"
			StringTokenizer tokenizer = new StringTokenizer(input[line], " ");
			currentToken = tokenizer.nextToken();
		}//if
		
		else
			currentToken = input[line];
		
		if(currentToken.equals("INTEGER_CONSTANT"))
			line++;
		else if(currentToken.equals("NUMBER_CONSTANT"))
			line++;
		else if(currentToken.equals("CHARACTER_CONSTANT"))
			line++;
		else if(currentToken.equals("DATE_CONSTANT"))
			line++;
		else if(currentToken.equals("NAME"))
			line++;
		else
			reject();
		return;
	}//deleteStatement
	
	static void conditionList() throws ParseException{// 32
		if(inFollow(32))
			return;
		if(input[line].equals("OR"))
			line++;
		else if(input[line].equals("AND"))
			line++;
		else
			reject();
		condition();
		return;
	}//deleteStatement
	
	static void update() throws ParseException{// 33
		checkToken("UPDATE");
		checkToken("NAME");
		String tableName = getLastValue();
		checkToken("SET");
		setList();
		where();		
		command = new DMLCommand(CommandType.UPDATE, tableName);//TODO Add params
		checkToken(";");
		return;
	}//deleteStatement
	
	static void expression() throws ParseException{// 34
		operand();
		return;
	}//deleteStatement
	
	static void setList() throws ParseException{// 35
		checkToken("");
		checkToken("");
		expression();
		nextSet();
		return;
	}//deleteStatement
	
	static void nextSet() throws ParseException{// 36
		if(inFollow(36))
			return;
		checkToken(",");
		setList();
		return;
	}//deleteStatement
	
	static void wUpdate() throws ParseException{// 37
		checkToken("WUPDATE");
		checkToken("NAME");
		String tableName = getLastValue();
		checkToken("SET");
		setList();
		where();
		command = new DMLCommand(CommandType.UPDATE, tableName);//TODO Add params, specify as wUpdate?
		checkToken(";");
		return;
	}//deleteStatement

	static void select() throws ParseException{// 38
		checkToken("SELECT");
		selectParams();
		checkToken("FROM");
		String tableName = input[line++];
		where();
		command = new DMLCommand(CommandType.SELECT, tableName);//TODO Add params
		((DMLCommand)command).setColumnNames(fieldNames);
		((DMLCommand)command).allColumns = allColumns;
		allColumns = false;
		checkToken(";");
		return;
	}//deleteStatement
	
	static void wSelect() throws ParseException{// 39
		checkToken("WSELECT");
		selectParams();
		checkToken("FROM");
		checkToken("NAME");
		String tableName = getLastValue();
		where();
		command = new DMLCommand(CommandType.SELECT, tableName);//TODO Add params, specify as wSelect?
		checkToken(";");
		return;
	}//deleteStatement
	
	static void selectParams() throws ParseException{// 40
		if(input[line].equals("*")){
			allColumns = true;
			line++;
			return;
		}
		else
			fieldList();

		return;
	}//deleteStatement
	
	/*
	 * --------------------------------------
	 * 			Auxiliary Functions
	 * --------------------------------------
	 */	
	
	private static boolean inFirst(int nonTerminal){//returns true if the current token is in the first set of the given non-terminal
		String valueToCheck = null;
		
		if(input[line].contains(" ")){//This is for tokens like DATE which is in the format "DATE MM DD [YY]YY"
			StringTokenizer tokenizer = new StringTokenizer(input[line], " ");
			valueToCheck = tokenizer.nextToken();
		}//if
		
		else
			valueToCheck = input[line];
		
		for(int i = 0; i < firstSets[nonTerminal].length; i++){
			if(firstSets[nonTerminal][i].equalsIgnoreCase(valueToCheck))
				return true;
		}//for
		
		return false;
	}//inFirst
	
	private static boolean inFollow(int nonTerminal){//returns true if the current token is in the follow set of the given non-terminal
		String valueToCheck = null;
		
		if(input[line].contains(" ")){//This is for tokens like DATE which is in the format "DATE MM DD [YY]YY"
			StringTokenizer tokenizer = new StringTokenizer(input[line], " ");
			valueToCheck = tokenizer.nextToken();
		}//if
		
		else
			valueToCheck = input[line];
		
		for(int i = 0; i < followSets[nonTerminal].length; i++){
			if(followSets[nonTerminal][i].equalsIgnoreCase(valueToCheck))
				return true;
		}//for
		return false;
	}//inFollow
	
	private static void printTokens(){//This function is used to test the Lexer
		if (debug)
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
		throw new ParseException("Expected: " + expected + " actual: " + actual);
	}
	
	private static void checkToken(String expectedValue) throws ParseException{
		String valueToCheck = null;
		
		if(input[line].contains(" ")){//This is for tokens like DATE which is in the format "DATE MM DD [YY]YY"
			StringTokenizer tokenizer = new StringTokenizer(input[line], " ");
			valueToCheck = tokenizer.nextToken();
		}//if
		
		else
			valueToCheck = input[line];
		
		if(valueToCheck.equalsIgnoreCase(expectedValue))
			line++;
		else
			reject(expectedValue, valueToCheck);
		
		return;
	}//checkToken

	private static String getLastValue(){//returns the value of the last token
		StringBuilder output = new StringBuilder();
		
		if(input[line-1].contains(" ")){//This is for tokens with labels
			StringTokenizer tokenizer = new StringTokenizer(input[line-1], " ");
			tokenizer.nextToken();//skip the token label
			
			for(int i = 1; i < tokenizer.countTokens(); i++)
				output.append(tokenizer.nextToken());
		}//if
		
		else
			output.append(input[line-1]);
		
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
		precision = DEFAULT_PRECISION;
		scale = DEFAULT_SCALE;
	}//resetColumnData
	
	private static void setFieldType(){//sets the class variable dataType according to other class variables
		if(lastType.equals("CHARACTER"))
			dataType = new FieldType(Type.CHARACTER, 0, scale);
		else if(lastType.equals("INTEGER"))
			dataType = new FieldType(Type.INTEGER, 0, scale);
		else if(lastType.equals("NUMBER"))
			dataType = new FieldType(Type.NUMBER, precision, scale);
		else if(lastType.equals("DATE"))
			dataType = new FieldType(Type.DATE);
		else
			System.out.println("Error determining type " + lastType);
		return;
	}//setFieldType
	
}//class Parser
