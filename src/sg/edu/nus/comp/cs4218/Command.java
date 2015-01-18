package sg.edu.nus.comp.cs4218;

import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public interface Command {
	
	/**
	 * Evaluates command using data provided through stdin stream. Write result to stdout stream. 
	 */
	public void evaluate(InputStream stdin, OutputStream stdout) 
				throws AbstractApplicationException, ShellException;
	
	/**
	 * Terminates current execution of the command.
	 */
	public void terminate();
	
}
