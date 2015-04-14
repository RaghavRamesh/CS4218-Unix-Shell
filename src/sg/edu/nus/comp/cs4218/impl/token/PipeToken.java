package sg.edu.nus.comp.cs4218.impl.token;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class PipeToken extends AbstractToken {

	protected PipeToken(String parent, int begin) {
		super(parent, begin);
	}

	@Override
	public Boolean appendNext() {
		return false;
	}

	@Override
	public TokenType getType() {
		return TokenType.PIPE;
	}

	@Override
	public String value() throws ShellException, AbstractApplicationException {
		return String.valueOf(parent.charAt(begin));
	}

	@Override
	public void checkValid() throws ShellException {
		// Do not need to check anything
	}

}
