package sg.edu.nus.comp.cs4218.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
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

	public static Command getCommand(String cmdline) throws ShellException {
		List<String> tokens = Parser.parseCommandLine(cmdline);
		for (String token : tokens) {
			if (Parser.isSemicolon(token)) {
				return new SeqCommand(cmdline);
			}
		}
		for (String token : tokens) {
			if (Parser.isPipe(token)) {
				return new PipeCommand(cmdline);
			}
		}
		return new CallCommand(cmdline);
	}
	
	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ShellImplementation shellImplementation = new ShellImplementation();
		//PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
		
		//"cd src; pwd > a.txt; ls"
		while (true) {
			try {
				System.out.print(Environment.getCurrentDirectory() + " # ");
				
				shellImplementation.parseAndEvaluate(reader.readLine(),
						System.out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
