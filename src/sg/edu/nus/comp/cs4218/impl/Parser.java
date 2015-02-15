package sg.edu.nus.comp.cs4218.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.ShellException;

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

	public static List<String> parseCommandLine(String input) throws ShellException {
		String trimmedInput = input.trim();
		StringBuilder currentToken = new StringBuilder();
		ArrayList<String> tokens = new ArrayList<String>();
		Stack<CharacterPosition> quoteStack = new Stack<CharacterPosition>();
		for (int i = 0; i < trimmedInput.length(); i++) {
			Character currentChar = trimmedInput.charAt(i);
			if (currentChar.equals(WHITESPACE) && quoteStack.isEmpty()) {
				addNonEmptyToList(tokens, currentToken.toString());
				continue;
			}
			currentToken.append(currentChar);
			if (isQuote(currentChar)) {
				if (quoteStack.isEmpty() || !quoteStack.peek().getCharacter().equals(currentChar)) {
					quoteStack.push(new CharacterPosition(currentChar, i));
				} else {
					quoteStack.pop();
					if (quoteStack.isEmpty()) {
						addNonEmptyToList(tokens, currentToken.toString());
						currentToken = new StringBuilder();
					}
				}
			} else if (quoteStack.isEmpty() && SPECIALS.contains(currentChar)) {
			  // Add the current token without the last character
			  addNonEmptyToList(tokens, currentToken.substring(0, currentToken.length() - 1));
			  // Add the last character
			  addNonEmptyToList(tokens, currentChar.toString());
			  currentToken = new StringBuilder();
			}
		}
		if (!quoteStack.isEmpty()) {
		  throw new ShellException(Consts.Messages.QUOTE_MISMATCH);
		} 
		addNonEmptyToList(tokens, currentToken.toString());
		return tokens;
	}

	public static Boolean isQuoted(String input) {
	  return isDoubleQuoted(input)
	      || isSingleQuoted(input)
	      || isBackQuoted(input);
	}
	
  public static Boolean isDoubleQuoted(String input) {
    int length = input.length();
    if (length < 2) {
      return false;
    }
    return (input.charAt(0) == DOUBLE_QUOTE)
        && (input.charAt(length - 1) == DOUBLE_QUOTE);
  }
	
	public static Boolean isSingleQuoted(String input) {
	  int length = input.length();
    if (length < 2) {
      return false;
    }
    return (input.charAt(0) == SINGLE_QUOTE)
        && (input.charAt(length - 1) == SINGLE_QUOTE);
	}
	
	public static Boolean isBackQuoted(String input) {
	  int length = input.length();
	  if (length < 2) {
	    return false;
	  }
	  return (input.charAt(0) == BACK_QUOTE)
	      && (input.charAt(length - 1) == BACK_QUOTE);
	}
	
	public static Boolean isSemicolon(String input) {
	  return input.length() == 1
	      && input.charAt(0) == SEMICOLON;
	}
	
	public static Boolean isPipe(String input) {
	  return input.length() == 1
	      && input.charAt(0) == PIPE;
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
