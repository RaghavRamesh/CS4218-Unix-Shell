package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class CallCommand implements Command {
	private String command;
	
	public CallCommand(String command) {
		this.command = command;
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		Application app = getApplication(command);
		String[] args = getArguments(command);
//		app.run(args, stdin, stdout);
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}
	
	Application getApplication(String command) {
		return null;
	}
	
	/**
	 * Parses arguments after quoting.
	 */
	String[] getArguments(String command) {
		// If quote encountered, treat till end of quote as single argument
		String[] args;
		List<String> argsList = new ArrayList<String>();
		Pattern pattern = Pattern.compile("([a-zA-Z0-9]+|'[^']*'|\"[^\"]*\"|`[^`]*`)+");
		Matcher matcher = pattern.matcher(command);
		while(matcher.find()) {
		   argsList.add(matcher.group(0));
		} 	
		args = argsList.toArray(new String[argsList.size()]);
		return args;
	}
}
