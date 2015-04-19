package sg.edu.nus.comp.cs4218.impl.token;

import java.util.List;
import java.util.Stack;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;

public class DoubleQuoteToken extends AbstractToken {
	private final Stack<Character> quoteStack;

	public DoubleQuoteToken(String parent, int begin) {
		super(parent, begin);
		quoteStack = new Stack<Character>();
		quoteStack.push(parent.charAt(begin));
	}

	public DoubleQuoteToken(String parent, int begin, int end) {
		super(parent, begin, end);
		quoteStack = new Stack<Character>();
	}

	@Override
	public Boolean appendNext() {
		if (end >= parent.length() - 1) {
			return false;
		}

		if (quoteStack.isEmpty()) {
			return false;
		}

		Character nextChar = parent.charAt(end + 1);
		if (nextChar == '`') {
			if (quoteStack.contains(nextChar)) {
				while (quoteStack.pop() != nextChar) {
				}
			} else {
				quoteStack.push(nextChar);
			}
		} else if (nextChar == '"') {
			if (quoteStack.peek() == nextChar) {
				quoteStack.pop();
			} else {
				quoteStack.push(nextChar);
			}
		}
		end++;
		return true;
	}

	@Override
	public TokenType getType() {
		return TokenType.DOUBLE_QUOTES;
	}

	@Override
	public String value() throws ShellException, AbstractApplicationException {
		checkValid();

		String content = parent.substring(begin + 1, end);
		List<AbstractToken> tokens = Parser.tokenize(content);
		String result = "\"";
		for (AbstractToken token : tokens) {
			if (token.getType() == TokenType.BACK_QUOTES) {
				result += chomp(token.value());
			} else {
				result += token.toString();
			}
		}
		result += "\"";
		return result;
	}

	// Remove newline characters at the end of the string
	public static String chomp(String input) {
		int lastIndex = input.length() - 1;
		int newlineLength = System.lineSeparator().length();
		while (lastIndex >= newlineLength - 1) {
			String last = input.substring(lastIndex - newlineLength + 1,
					lastIndex + 1);
			if (last.equals(System.lineSeparator())) {
				lastIndex -= newlineLength;
			} else {
				break;
			}
		}

		if (lastIndex < 0) {
			return "";
		} else {
			return input.substring(0, lastIndex + 1);
		}
	}

	@Override
	public void checkValid() throws ShellException {
		if (end > begin && parent.charAt(begin) == '"'
				&& parent.charAt(end) == '"') {
			return;
		}
		throw new ShellException(Consts.Messages.QUOTE_MISMATCH);
	}
}
