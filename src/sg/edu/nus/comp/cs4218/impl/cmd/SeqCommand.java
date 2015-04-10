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
	private final List<String> commands;
	private final String commandLine;

	public SeqCommand(String commandLine) throws ShellException,
			AbstractApplicationException {
		this.commandLine = commandLine;
		this.commands = new ArrayList<String>();
		List<AbstractToken> tokens = Parser.tokenize(commandLine);

		String currentCommand = "";
		for (AbstractToken token : tokens) {
			if (token.getType() == TokenType.SEMICOLON) {
				commands.add(currentCommand.trim());
				currentCommand = "";
			} else {
				currentCommand += " " + token.toString();
			}
		}
		if (!currentCommand.trim().equals("")) {
			commands.add(currentCommand.trim());
		}
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		for (int i = 0; i < commands.size(); i++) {
			Command command = ShellImplementation.getCommand(commands.get(i));
			InputStream input = i == 0 ? stdin : null;
			command.evaluate(input, stdout);
		}
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
	}

	// @Override
	// public String toString() {
	// return commandLine;
	// }
}
