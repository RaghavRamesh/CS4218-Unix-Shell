package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.QuoteParser;

public class CallCommand implements Command {
	private String mCommandLine;
	
	public CallCommand(String commandLine) {
		this.mCommandLine = commandLine;
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		Application app = getApplication(mCommandLine);
		String[] args = getArguments(mCommandLine);
		if (app != null) {
		  app.run(args, stdin, stdout);
		}
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
	  
	}
	
	private Application getApplication(String command) {
		return null;
	}
	
	/**
	 * Parses arguments after quoting.
	 */
	private String[] getArguments(String command) {
	  List<String> tokens = QuoteParser.parse(command);
	  // Remove the application token
	  tokens.remove(0);
		String[] args = tokens.toArray(new String[tokens.size()]);
		return args;
	}
}
