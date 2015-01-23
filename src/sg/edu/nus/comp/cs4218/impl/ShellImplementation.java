package sg.edu.nus.comp.cs4218.impl;

import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ShellImplementation implements Shell {

	@Override
	public void parseAndEvaluate(String cmdline, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
	  try {
	    
	  } catch (Exception e) {
	    throw new ShellException(e.getMessage());
	  }
	}
	
	Command getCommand(String cmdline) {
		return null;
	}
	
	public static void main(String[] args) {
	  try {
	    ShellImplementation shellImplementation = new ShellImplementation();
	    shellImplementation.parseAndEvaluate("echo \"hello world\"", System.out);
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
	}
}
