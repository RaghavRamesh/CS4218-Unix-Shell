package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class SeqCommand implements Command {
	private final List<Command> mCommands;

	public SeqCommand(String commandLine) throws ShellException,
			AbstractApplicationException {
		this.mCommands = new ArrayList<Command>();
		List<AbstractToken> tokens = Parser.tokenize(commandLine);

		String currentCommand = "";
		for (AbstractToken token : tokens) {
			if (token.getType() == TokenType.SEMICOLON) {
				Command command = ShellImplementation.getCommand(currentCommand.trim());
				mCommands.add(command);
				currentCommand = "";
			} else {
				currentCommand += " " + token.toString();
			}
		}
		if (!currentCommand.trim().equals("")) {
			mCommands.add(ShellImplementation.getCommand(currentCommand.trim()));
		}
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		for (int i = 0; i < mCommands.size(); i++) {
			Command command = mCommands.get(i);
			InputStream input = i == 0 ? stdin : null;
			command.evaluate(input, stdout);
		}
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
	}

}
