package sg.edu.nus.comp.cs4218.impl.token;

import sg.edu.nus.comp.cs4218.exception.ShellException;

public class OutputToken extends AbstractToken {

	public OutputToken(String parent, int begin) {
		super(parent, begin);
		assert (parent.charAt(begin) == '>');
	}

	@Override
	public Boolean appendNext() {
		return false;
	}

	@Override
	public TokenType getType() {
		return TokenType.OUTPUT;
	}

	@Override
	public String value() throws ShellException {
		return String.valueOf(parent.charAt(begin));
	}

	@Override
	public void checkValid() throws ShellException {
		// Do not need to check anything
	}

}
