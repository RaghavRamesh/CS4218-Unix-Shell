package sg.edu.nus.comp.cs4218.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

// Utility class to parse a string input into tokens based on quotes.
// The quotes are not removed from the tokens.
public final class Parser {
	static final Character WHITESPACE = ' ';
	static final Character DOUBLE_QUOTE = '"';
	static final Character SINGLE_QUOTE = '\'';
	static final Character BACK_QUOTE = '`';
	
	static final Character SEMICOLON = ';';
	static final Character PIPE = '|';
	static final Character IN_STREAM = '<';
	static final Character OUT_STREAM = '>';
	
	static final List<Character> SPECIALS = Arrays.asList(SEMICOLON, PIPE, IN_STREAM, OUT_STREAM);

	private Parser() {
	}

	public static List<String> parseCommandLine(String input) {
		String trimmedInput = input.trim();
		String currentToken = "";
		ArrayList<String> tokens = new ArrayList<String>();
		Stack<CharacterPosition> quoteStack = new Stack<CharacterPosition>();
		for (int i = 0; i < trimmedInput.length(); i++) {
			Character currentChar = trimmedInput.charAt(i);
			if (currentChar.equals(WHITESPACE) && quoteStack.isEmpty()) {
				addNonEmptyToList(tokens, currentToken);
				currentToken = "";
				continue;
			}
			currentToken += currentChar;
			if (isQuote(currentChar)) {
				if (quoteStack.isEmpty()
						|| !quoteStack.peek().getCharacter()
								.equals(currentChar)) {
					quoteStack.push(new CharacterPosition(currentChar, i));
				} else {
					quoteStack.pop();
					if (quoteStack.isEmpty()) {
						addNonEmptyToList(tokens, currentToken);
						currentToken = "";
					}
				}
			} else if (quoteStack.isEmpty() 
			           && SPECIALS.contains(currentChar)) {
			  // Add the current token without the last character
			  addNonEmptyToList(tokens, currentToken.substring(0, currentToken.length() - 1));
			  // Add the last character
			  addNonEmptyToList(tokens, currentChar.toString());
			  currentToken = "";
			}
		}
		addNonEmptyToList(tokens, currentToken);
		return tokens;
	}

	public static Boolean isQuoted(String input) {
	  return isDoubleQuoted(input)
	      || isSingleQuoted(input)
	      || isBackQuoted(input);
	}
	
  public static Boolean isDoubleQuoted(String input) {
    int n = input.length();
    if (n < 2) {
      return false;
    }
    return (input.charAt(0) == DOUBLE_QUOTE)
        && (input.charAt(n - 1) == DOUBLE_QUOTE);
  }
	
	public static Boolean isSingleQuoted(String input) {
	  int n = input.length();
    if (n < 2) {
      return false;
    }
    return (input.charAt(0) == SINGLE_QUOTE)
        && (input.charAt(n - 1) == SINGLE_QUOTE);
	}
	
	public static Boolean isBackQuoted(String input) {
	  int n = input.length();
	  if (n < 2) {
	    return false;
	  }
	  return (input.charAt(0) == BACK_QUOTE)
	      && (input.charAt(n - 1) == BACK_QUOTE);
	}
	
	public static Boolean isSemicolon(String input) {
	  return input.length() == 1
	      && input.charAt(0) == SEMICOLON;
	}
	
	public static Boolean isInStream(String input) {
	  return input.length() == 1
	      && input.charAt(0) == IN_STREAM;
	}
	
	public static Boolean isOutStream(String input) {
	  return input.length() == 1
	      && input.charAt(0) == OUT_STREAM;
	}
	 
	private static Boolean addNonEmptyToList(List<String> list, String str) {
		if (str.trim().equals("")) {
			return false;
		} else {
			return list.add(str);
		}
	}

	private static Boolean isQuote(Character character) {
		return character.equals(DOUBLE_QUOTE) || character.equals(SINGLE_QUOTE)
				|| character.equals(BACK_QUOTE);
	}
}
