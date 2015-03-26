package sg.edu.nus.comp.cs4218.impl.token;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public abstract class AbstractToken {
	public enum TokenType {
		SPACES, NORMAL, SEMICOLON, PIPE, INPUT, OUTPUT, SINGLE_QUOTES, DOUBLE_QUOTES, BACK_QUOTES
	}

	protected final String parent;
	protected final int begin;
	protected int end;

	protected AbstractToken(String parent, int begin) {
		this.parent = parent;
		this.begin = begin;
		this.end = begin;
	}

	public abstract Boolean appendNext();

	public abstract TokenType getType();

	public abstract String value() throws ShellException,
			AbstractApplicationException;

	public abstract void checkValid() throws ShellException;

	@Override
	public String toString() {
		return parent.substring(begin, end + 1);
	}

	public static TokenType getType(Character firstChar) {
		switch (firstChar) {
		case ' ':
			return TokenType.SPACES;
		case '<':
			return TokenType.INPUT;
		case '>':
			return TokenType.OUTPUT;
		case ';':
			return TokenType.SEMICOLON;
		case '|':
			return TokenType.PIPE;
		case '"':
			return TokenType.DOUBLE_QUOTES;
		case '`':
			return TokenType.BACK_QUOTES;
		case '\'':
			return TokenType.SINGLE_QUOTES;
		default:
			return TokenType.NORMAL;
		}
	}

	// public String getParent() {
	// return parent;
	// }
	//
	// public int getBegin() {
	// return begin;
	// }

	public int getEnd() {
		return end;
	}
}
