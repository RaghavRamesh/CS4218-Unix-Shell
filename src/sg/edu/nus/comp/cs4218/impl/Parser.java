package sg.edu.nus.comp.cs4218.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.ShellException;

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
	static final Character PIPE = '|';
	static final Character IN_STREAM = '<';
	static final Character OUT_STREAM = '>';

	static final List<Character> SPECIALS = Arrays.asList(SEMICOLON, PIPE,
			IN_STREAM, OUT_STREAM);

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
	public static List<String> parseCommandLine(String input)
			throws ShellException {
		String trimmedInput = input.trim();
		StringBuilder currentToken = new StringBuilder();
		ArrayList<String> tokens = new ArrayList<String>();
		Stack<CharacterPosition> quoteStack = new Stack<CharacterPosition>();
		for (int i = 0; i < trimmedInput.length(); i++) {
			Character currentChar = trimmedInput.charAt(i);
			if (currentChar.equals(WHITESPACE) && quoteStack.isEmpty()) {
				addNonEmptyToList(tokens, currentToken.toString());
				currentToken = new StringBuilder();
				continue;
			}
			currentToken.append(currentChar);
			if (isQuote(currentChar)) {
				if (quoteStack.isEmpty()) {
					// Add the previous part excluding the quote character
					addNonEmptyToList(tokens, currentToken.substring(0,
							currentToken.length() - 1));
					currentToken = new StringBuilder(currentChar.toString());
					quoteStack.push(new CharacterPosition(currentChar, i));
				} else if (quoteStack.peek().getCharacter().equals(currentChar)) {
					quoteStack.pop();
					if (quoteStack.isEmpty()) {
						addNonEmptyToList(tokens, currentToken.toString());
						currentToken = new StringBuilder();
					}
				} else {
					quoteStack.push(new CharacterPosition(currentChar, i));
				}
			} else if (quoteStack.isEmpty() && SPECIALS.contains(currentChar)) {
				// Add the current token without the last character
				addNonEmptyToList(tokens,
						currentToken.substring(0, currentToken.length() - 1));
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
	 * Check if a string represents a pipe character
	 * 
	 * @param input
	 *            The original string input to check.
	 */
	public static Boolean isPipe(String input) {
		return input.length() == 1 && input.charAt(0) == PIPE;
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
