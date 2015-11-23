/**LexicalAnalyzer.java
 * 
 * Performs Lexical Analysis for the wSQLx engine
 * 
 * Written by Michael Smith on 11/22/15
 */

import java.util.StringTokenizer;

public class LexicalAnalyzer{ //TODO Can add RENAME functionality later
	static final String[] KEYWORDS = {"CREATE", "DROP", "SAVE","COMMIT","LOAD","DATABASE","TABLE","INSERT", "VALUES","INTO",
								"DELETE","FROM","UPDATE","WUPDATE","SELECT","WSELECT","WHERE","AND","OR","AS"}; //list of all keywords defined in wSQLx
	static final int MAX_TOKEN_LENGTH = 128;
	/**
	 * Takes an input string and returns tokens as an array of strings
	 * @param inputString the wSQLx command to be tokenized
	 * @return tokens as an array of strings
	 */
	public static String[] tokenize(String inputString){
   /*
	* ----------------------
	* -----DECLARATIONS-----
	* ----------------------
	*/
		boolean inString = false;
		boolean inComment = false;
		char[] input = null;
		StringBuilder output = new StringBuilder();
		
		input = inputString.toCharArray();
		
		char[] token = new char[MAX_TOKEN_LENGTH]; //temporary workspace for building token strings
		boolean lineComment = false;
		boolean containsDecimal = false;
		boolean isDate = false;
		int charCount = 0;
		
		
   /*
	* -------------------
	* -----MAIN LOOP-----
	* -------------------
	*/
		/*
		 * -----------------
		 * -----STRINGS-----
		 * -----------------
		 */
		
		for(int i = 0; i < input.length; i++){//loop through the file character by character
			int j = 0; //indexer for token strings
			
			if(inString){
				token = new char[MAX_TOKEN_LENGTH];
			}
			
			while(inString){
				if((int)input[i] == 39){
					output.append("STRING " + (new String(token)).trim() + "\n");
					inString = false;
					i++;//skip the single quote on next iteration
					break;
				}//if end of string
				else if(i+1 == input.length)
					return null;//error if the input ends before terminating the string with a '
				else{
					token[j++] = input[i];
				}
				i++;
			}

			
			/*
			 * ------------------
			 * -----COMMENTS-----
			 * ------------------
			 */
			if(inComment){ //this if statement is here so that the continue statement applies to the main for loop
			
				while(!inComment && !lineComment){//while in a commented section, look for nested comments and the end of the commented section
					if(i+1 >= input.length){ //exit program if there is only one character left to read and it is commented
						i = input.length;
						break;
					}//if
					if(input[i] == '/'){
						if(input[i+1] == '*'){
							inComment = true;
							i++; //increment i to skip the '*' on next iteration
						}//if
					
					}//if '/' found, look for a following '/' or '*'
					
					else if(input[i] == '*'){ //else if '*' found, look for a following '/'
						if(input[i+1] == '/'){
							inComment = false;
							i++; //increment i to skip the '/' on next iteration
							break;
						}//if
					}//else if
					
					i++;
				}//while
			
				continue;
			}//if inComment
			if(input[i] == '/' && !inComment && !lineComment){
				if((i+1) < input.length)
					if(input[i+1] == '*'){
						inComment = true;
						continue;
					}//if '/*'
			}//if
			
			if(input[i] == '-' && !inComment && !lineComment){ //look for an initial comment
				if((i+1) < input.length){
					if(input[i+1]== '-'){
						lineComment = true;
						i++;
						while(lineComment && i < input.length){//look for the next line
							if((int)input[i] == 10 || (int)input[i] == 13) //if the next character is a new line or carriage return
								lineComment = false;
							else if((int)input[i] == 3 || (int)input[i] == 4){//if the end of file is reached, break the loop
								lineComment = false;
								break;
							}//else if
							i++;
						}//while
						i--;
					}//else if
					else{
						output.append("-\n");
					}//else
				}//if
				else{
					output.append("-\n");
				}//else
			}//if
			
			/*
			 * --------------------------------	
			 * -----KEYWORDS & IDENTIFIERS-----	
			 * --------------------------------		
			 */
			
			else if(isText(input[i], false)){ //if the next character is alphabetical or an underscore, check if it is a keyword or identifier
				while(isText(input[i], true)){//while reading alphanumeric characters and underscores, put them into a character array, the true includes numeric
					token[j] = input[i];
					i++; //This is not under the if statement so this program will ignore anything after the first 8 characters in an identifier
					j++;
					if(i >= input.length)
						break;
				}//while
				
				if(checkKeywords(token) > -1){ //if the token is a keyword, print it. otherwise label it as an identifier
					output.append("" + (new String(token)).trim().toUpperCase() + "\n"); //used for parser					
				}//if
				
				else{
					output.append("ID " + new String(token).trim()+ "\n"); //used for parser					
				}//else
				
				token = new char[MAX_TOKEN_LENGTH]; //reset the token array
				i--; // decrement i so the next character is not skipped
				
			}//else if
			
			/*
			 * --------------------------------------------
			 * --------INTEGERS, NUMBERS, AND DATES--------
			 * --------------------------------------------
			 */
			
			else if((int)input[i] > 47 && (int)input[i] < 58){ //if the next character is numerical
				while((int)input[i] > 47 && (int)input[i] < 58){ //while reading numerical characters
					token[j] = input[i];
					j++;
					charCount++;
					
					if(i+1 >= input.length){
						i++;
						break;
					}//if
					
					if(charCount == 2 && input[i+1] == '/' && !containsDecimal){
						isDate = true;
						token[j] = ' ';
						i++;
						j++;
					}
					
					else if(charCount == 4 && isDate){
						if(containsDecimal || input[i+1] != '/'){
							isDate = false; //false assumption made, revert and treat as separate tokens
							for(int k = 2; k < token.length; k++)
								token[k] = ' ';
							i -= 3; //after the i++ statement below input[i] will be the '/' and the program will treat it as a symbol rather than part of a date
						}//if not actually a date
						else if(input[i+1] == '/'){
							token[j] = ' ';
							i++;
							j++;
						}//else if
					}//else if
					
					else if(charCount == 8 && isDate){
						if(i+1 < input.length){
							if((int)input[i+1] > 47 && (int)input[i+1] < 58)
								return null;//Error if more than 4 numbers in year spot (ie input looks like ##/##/#####)
						}//if
					}//else if
					
					if(input[i+1] == '.' && !containsDecimal && (int)input[i + 2] > 47 && (int)input[i + 2] < 58){ //if the next character is a decimal with at least one number past it
						containsDecimal = true;
						token[j] = '.';
						j++;
						i++; //increment i to skip the decimal point on next iteration
					}//if decimal
					i++;
				}//while
				
				token = trimToken(token);
				
				if(containsDecimal){
					output.append("NUMBER " + (new String(token)).trim() + "\n");//parser
				}//if NUMBER
				
				else if(isDate){
					if(charCount != 6 && charCount != 8)
						return null;
					output.append("DATE " + (new String(token)).trim() + "\n");//parser
				}//else if DATE
				
				else{
					output.append("INTEGER " + (new String(token)).trim() + "\n");//parser
				}//else (INTEGER)
				
				containsDecimal = false;
				isDate = false;
				charCount = 0;
				i--;// decrement i so the next character is not skipped
				token = new char[MAX_TOKEN_LENGTH]; //reset the token array
			}//else if
			
			/*
			 * -----------------
			 * -----SYMBOLS-----
			 * -----------------
			 */
			
			else if((int)input[i] > 32){ //excluding spaces, new lines, etc.
				switch((int)input[i]){
				case 39:// '
					if(!inString)
						inString = true;
					break;
				case 40:
					output.append("(\n");				
					break;
				case 41:
					output.append(")\n");
					break;
				case 42:
					output.append("*\n");
					break;
				case 43:
					output.append("+\n");
					break;
				case 44:
					output.append(",\n");
					break;
				case 46:
					output.append(".\n");
					break;
			    case 47:
			    	output.append("/\n");
					break;
				case 59:
					output.append(";\n");
					break;
				case 60:
					if(i+1 < input.length){
						if(input[i+1] == '='){
							output.append("<=\n");
							i++; //increment i to skip the '=' symbol on next iteration of the loop
						}//if
						else if(input[i + 1] == '>'){
							output.append("<>\n");
							i++; //increment i to skip the '=' symbol on next iteration of the loop
						}//else if
						else{
							output.append("<\n");
						}//else
					}//if
					else{
						output.append("<\n");
					}//else
					break;
				case 61:
					output.append("=\n");
					break;
				case 62:
					if(i+1 < input.length){
						if(input[i+1] == '='){
							output.append(">=\n");
							i++; //increment i to skip the '=' symbol on next iteration of the loop
						}//if
						else{
							output.append(">\n");
						}//else
					}//if
					else{
						output.append(">\n");
					}//else
					break;
				case 91:
					output.append("[\n");
					break;
				case 93:
					output.append("]\n");
					break;
				case 123:
					output.append("{\n");					
					break;
				case 125:
					output.append("}\n");					
					break;
				default:
					return null;
				}//switch
				
				
			}//else if
		}//for
		
		/*
		 * ------------------------
		 * ---------OUTPUT---------
		 * ------------------------
		 */

		StringTokenizer tokenizer = new StringTokenizer(output.toString(), ("\n"));
		String[] tokens = new String[tokenizer.countTokens()];
		
		for(int i = 0; i < tokens.length; i++){
			tokens[i] = tokenizer.nextToken();
		}
		return tokens;
	}//main
	
	/*
	 * ------------------------------
	 * -----ADDITIONAL FUNCTIONS-----
	 * ------------------------------
	 */
	
	static int checkKeywords(char[] tokenArray){ //takes a character array and returns true if it is a keyword or false if it isn't
		/*
		for(int i = 0; i < tokenArray.length; i++){
			if((int)tokenArray[i] < 65 || (int)tokenArray[i] > 122)
				tokenArray[i] = ' ';
			
		}//for
		*/
		
		String token = (new String(tokenArray)).trim().toUpperCase();
		
		for(int i = 0; i < KEYWORDS.length; i++){
			if(token.equals(KEYWORDS[i]))
				return i;
		}//for
		
		return -1;
	}//checkKeywords
	
	static char[] trimToken(char[] rawToken){//gets rid of unused space on the given token
		char[] token = null;
		int i = 0;
		while((int)rawToken[i] != 0 && i < rawToken.length){
			i++;
		}//while
		
		token = new char[i];
		i = 0;
		while(i < token.length){
			token[i] = rawToken[i];
			i++;
		}//while
		
		return token;
	}//trimToken
	
	static boolean isText(char character, boolean numeric){//returns true if the given character can be used in an identifier
		if((int)character > 64 && (int)character < 91)//uppercase letters
			return true;
		else if((int)character > 96 && (int)character < 123)//lowercase letters
			return true;
		else if(numeric && (int)character > 47 && (int)character < 58)//numeric
			return true;
		
		switch((int)character){
		case 95: //_ underscore
			return true;
		default:
			return false;
		}//switch
	}//isText

}//class LexicalAnalyzer
