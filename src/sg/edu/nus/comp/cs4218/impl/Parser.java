package sg.edu.nus.comp.cs4218.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	static final Character PIPE = '|';
	static final Character IN_STREAM = '<';
	static final Character OUT_STREAM = '>';

	static final List<Character> SPECIALS = Arrays.asList(SEMICOLON, PIPE,
			IN_STREAM, OUT_STREAM);

	private Parser() {
	}

	/**
	 * Split an input string into tokens based on quotes and some special
	 * characters. String inside matched quotes are preserved.
	 * 
	 * @param input
	 *            The original string input
	 * @return A list of tokens
	 */
	public static List<AbstractToken> tokenize(String input) {
		ArrayList<AbstractToken> tokens = new ArrayList<AbstractToken>();
		AbstractToken currentToken = null;
		for (int i = 0; i < input.length(); i++) {
			if (currentToken == null) {
				currentToken = TokenFactory.getToken(input, i);
			} else if (!currentToken.appendNext()) {
				tokens.add(currentToken);
				currentToken = TokenFactory.getToken(input, i);
			}
		}
		if (currentToken != null) {
			tokens.add(currentToken);
		}
		return tokens;
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

	public static Boolean isQuote(Character character) {
		return character.equals(DOUBLE_QUOTE) || character.equals(SINGLE_QUOTE)
				|| character.equals(BACK_QUOTE);
	}

	public static Boolean isNormalCharacter(Character character) {
		return !isQuote(character) && !SPECIALS.contains(character)
				&& !character.equals(WHITESPACE);
	}
}
