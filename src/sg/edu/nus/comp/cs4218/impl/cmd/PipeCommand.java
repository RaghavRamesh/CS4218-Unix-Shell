package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class PipeCommand implements Command {
  private final String commandLine;
  
	public PipeCommand(String cmdLine) {
	  commandLine = cmdLine;
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub

	}

}
