package sg.edu.nus.comp.cs4218.impl.token;

import sg.edu.nus.comp.cs4218.exception.ShellException;

public class InputToken extends AbstractToken {

	protected InputToken(String parent, int begin) {
		super(parent, begin);
		assert (parent.charAt(begin) == '<');
	}

	@Override
	public Boolean appendNext() {
		return false;
	}

	@Override
	public TokenType getType() {
		return TokenType.INPUT;
	}

	@Override
	public String value() {
		return String.valueOf(parent.charAt(begin));
	}

	@Override
	public void checkValid() throws ShellException {
		// Don't need to check anything
	}
}
