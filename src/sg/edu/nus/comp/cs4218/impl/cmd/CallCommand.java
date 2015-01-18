package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.StringTokenizer;

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
		app.run(args, stdin, stdout);
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}
	
	Application getApplication(String command) {
		return null;
	}
	
	String[] getArguments(String command) {
		
		StringTokenizer st = new StringTokenizer(command);
		while (st.hasMoreTokens()) {
			
		}
		return null;
	}

}
