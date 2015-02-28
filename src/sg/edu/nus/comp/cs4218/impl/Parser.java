package sg.edu.nus.comp.cs4218.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken;
import sg.edu.nus.comp.cs4218.impl.token.TokenFactory;

/**
 * Utility class to parse a string input into tokens based on quotes and other
 * special characters
 */
public final class Parser {
	static final Character WHITESPACE = ' ';
	static final Character DOUBLE_QUOTE = '"';
	static final Character SINGLE_QUOTE = '\'';
	static final Character BACK_QUOTE = '`';
	static final Character SEMICOLON = ';';
	static final Character IN_STREAM = '<';
	static final Character OUT_STREAM = '>';

	static final List<Character> SPECIALS = Arrays.asList(SEMICOLON, IN_STREAM, OUT_STREAM);

	private Parser() {
	}

	/**
	 * Split an input string into tokens based on quotes and some special
	 * characters. String inside matched quotes are preserved. The white spaces
	 * between tokens are removed.
	 * 
	 * @param input
	 *            The original string input
	 * @return A list of tokens
	 */
	public static List<AbstractToken> tokenize(String input) throws ShellException {
	  String trimmedInput = input.trim();
	  ArrayList<AbstractToken> tokens = new ArrayList<AbstractToken>();
	  AbstractToken currentToken = null;
    for (int i = 0; i < trimmedInput.length(); i++) {
      if (currentToken == null) {
        currentToken = TokenFactory.getToken(trimmedInput, i);
      } else if (!currentToken.appendNext()){
        tokens.add(currentToken);
        currentToken = TokenFactory.getToken(trimmedInput, i); 
      }
    }
    if (currentToken != null) {
      tokens.add(currentToken);
    }
    return tokens;
	}

	/**
	 * Check if a token is inside matched quote characters.
	 * 
	 * @param input
	 *            The original string input to check
	 * @return True if the token is a proper quote match, False otherwise.
	 */
	public static Boolean isQuoted(String input) {
		return isDoubleQuoted(input) || isSingleQuoted(input)
				|| isBackQuoted(input);
	}

	/**
	 * Check if a token is inside double quote characters.
	 * 
	 * @param input
	 *            The original string input to check
	 * @return True if the token is a proper double quote pair, False otherwise.
	 */
	public static Boolean isDoubleQuoted(String input) {
		int length = input.length();
		if (length < 2) {
			return false;
		}
		return (input.charAt(0) == DOUBLE_QUOTE)
				&& (input.charAt(length - 1) == DOUBLE_QUOTE);
	}

	/**
	 * Check if a token is inside single quote characters.
	 * 
	 * @param input
	 *            The original string input to check
	 * @return True if the token is a proper single quote pair, False otherwise.
	 */
	public static Boolean isSingleQuoted(String input) {
		int length = input.length();
		if (length < 2) {
			return false;
		}
		return (input.charAt(0) == SINGLE_QUOTE)
				&& (input.charAt(length - 1) == SINGLE_QUOTE);
	}

	/**
	 * Check if a token is inside back quote characters.
	 * 
	 * @param input
	 *            The original string input to check
	 * @return True if the token is a proper back quote pair, False otherwise.
	 */
	public static Boolean isBackQuoted(String input) {
		int length = input.length();
		if (length < 2) {
			return false;
		}
		return (input.charAt(0) == BACK_QUOTE)
				&& (input.charAt(length - 1) == BACK_QUOTE);
	}

	/**
	 * Check if a string represents a semicolon character
	 * 
	 * @param input
	 *            The original string input to check.
	 */
	public static Boolean isSemicolon(String input) {
		return input.length() == 1 && input.charAt(0) == SEMICOLON;
	}

	/**
	 * Check if a string represents a input redirection character
	 * 
	 * @param input
	 *            The original string input to check.
	 */
	public static Boolean isInStream(String input) {
		return input.length() == 1 && input.charAt(0) == IN_STREAM;
	}

	/**
	 * Check if a string represents a output redirection character
	 * 
	 * @param input
	 *            The original string input to check.
	 */
	public static Boolean isOutStream(String input) {
		return input.length() == 1 && input.charAt(0) == OUT_STREAM;
	}

	/**
	 * Check if a string represents a character which belongs to SPECIALS set.
	 * 
	 * @param input
	 *            The original string input to check.
	 */
	public static Boolean isSpecialCharacter(String input) {
		return input.length() == 1 && SPECIALS.contains(input.charAt(0));
	}

	/**
	 * Check if a string contains a back-quote pair.
	 * 
	 * @param input
	 *            The original string input to check.
	 */
	public static Boolean containsBackQuote(String input) {
		int length = input.length();
		if (length < 2) {
			return false;
		}
		return input.contains(BACK_QUOTE.toString());
	}

	public static Boolean isQuote(Character character) {
		return character.equals(DOUBLE_QUOTE) 
		    || character.equals(SINGLE_QUOTE)
				|| character.equals(BACK_QUOTE);
	}
	
	public static Boolean isNormalCharacter(Character character) {
	  return !isQuote(character) 
	      && !SPECIALS.contains(character) 
	      && !character.equals(WHITESPACE);
 	}
}
