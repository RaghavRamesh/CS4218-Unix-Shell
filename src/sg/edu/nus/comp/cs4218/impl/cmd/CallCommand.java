package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.ApplicationFactory;

public class CallCommand implements Command {
	private final String mCommandLine;
	private final List<String> mTokens;

	public CallCommand(String commandLine) throws ShellException {
		mCommandLine = commandLine;
		this.mTokens = Parser.parseCommandLine(commandLine);
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		if (mTokens.isEmpty()) {
			return;
		}

		String inFile = null, outFile = null;

		try {
			Application app = getApplication(mTokens.get(0));
			List<String> argsList = new ArrayList<String>();
			int currentPos = 1;
			while (currentPos < mTokens.size()) {
				String token = mTokens.get(currentPos++);
				if (Parser.isInStream(token)) {
					if (inFile != null) {
						throw new ShellException(Consts.Messages.TOO_MANY_INPUT
								+ mCommandLine);
					}
					// TODO: Handle case < >
					if (currentPos == mTokens.size()) {
						throw new ShellException(Consts.Messages.NO_IN_PROVIDED
								+ mCommandLine);
					}
					inFile = mTokens.get(currentPos++);
				} else if (Parser.isOutStream(token)) {
					if (outFile != null) {
						throw new ShellException(
								Consts.Messages.TOO_MANY_OUTPUT + mCommandLine);
					}
					// TODO: Handle case < >
					if (currentPos == mTokens.size()) {
						throw new ShellException(
								Consts.Messages.NO_OUT_PROVIDED + mCommandLine);
					}
					outFile = mTokens.get(currentPos++);
				} else if (Parser.isBackQuoted(token)) {
					// Check if back quote is before single quote, if so call
					// getCommand, else do nothing
					argsList.add(substitute(token));
				} else {
					argsList.add(token);
				}
			}
			InputStream inStream = (inFile == null) ? stdin
					: new FileInputStream(Environment.createFile(inFile));
			OutputStream outStream = (outFile == null) ? stdout
					: new FileOutputStream(Environment.createFile(outFile));
			String[] args = argsList.toArray(new String[argsList.size()]);
			app.run(args, inStream, outStream);
		} catch (FileNotFoundException e) {
			throw new ShellException(e.getMessage());
		} catch (IOException e) {
			throw new ShellException(e.getMessage());
		}
	}

	public String substitute(String input) throws AbstractApplicationException,
			ShellException, IOException {
		List<String> tokens = Parser.parseCommandLine(input);
		for (int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			if (Parser.isBackQuoted(token)) {
				// Remove back quotes
				token = token.substring(1, token.length() - 1);
				Command command = ShellImplementation.getCommand(token);
				
				File file = new File("temporary.txt");
				OutputStream outStream = new FileOutputStream(file);
				InputStreamReader fileInputStream = new InputStreamReader(new FileInputStream(file));
				command.evaluate(null, outStream);
				BufferedReader br = new BufferedReader(fileInputStream);
				tokens.set(i, br.readLine());
			}
		}
		// TODO: toString trims spaces
		String result = "";
		for (String token : tokens) {
			result += " " + token;
		}
		return result.trim();
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub

	}

	private Application getApplication(String appId) throws ShellException {
		ApplicationFactory factory = new ApplicationFactory();
		return factory.getApplication(appId);
	}
}
