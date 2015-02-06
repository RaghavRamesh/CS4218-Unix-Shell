package sg.edu.nus.comp.cs4218.impl;

import java.io.OutputStream;
import java.util.List;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SeqCommand;

public class ShellImplementation implements Shell {

	@Override
	public void parseAndEvaluate(String cmdline, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
	    Command command = getCommand(cmdline);
	    command.evaluate(null, stdout);
	}

	public static Command getCommand(String cmdline) {
		List<String> tokens = Parser.parseCommandLine(cmdline);
		for (String token : tokens) {
			if (!Parser.isQuoted(token) && token.contains(";")) {
				return new SeqCommand(cmdline);
			}
		}
		for (String token : tokens) {
			if (!Parser.isQuoted(token) && token.contains("|")) {
				return new PipeCommand(cmdline);
			}
		}
		return new CallCommand(cmdline);
	}
	
	public static void main(String[] args) {
		try {
			ShellImplementation shellImplementation = new ShellImplementation();
			shellImplementation.parseAndEvaluate("cd src; pwd > a.txt; ls", System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
