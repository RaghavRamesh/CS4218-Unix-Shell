package sg.edu.nus.comp.cs4218.impl;

import java.io.FileNotFoundException;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;

public class ShellImplementation implements Shell {

	@Override
	public void parseAndEvaluate(String cmdline, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		// TODO Auto-generated method stub
		try {
			String s = "Hello testing";
			stdout.write(s.getBytes());
			Command comm = new CallCommand(cmdline);
			
			comm.evaluate(stdin, stdout);
		} catch (Exception e) {
			
		}
	}
	
	Command getCommand() {
		return null;
	}
	
	public static void main(String[] args) throws FileNotFoundException, AbstractApplicationException, ShellException {
		ShellImplementation shellImplementation = new ShellImplementation();
		shellImplementation.parseAndEvaluate("echo 'hello world'", System.out);
	}
}
