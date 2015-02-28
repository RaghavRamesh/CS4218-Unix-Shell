package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidFileException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;
import sg.edu.nus.comp.cs4218.impl.app.ApplicationFactory;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

// TODO: Create thread for applications
public class CallCommand implements Command {
	private final String commandLine;
	private final List<String> substitutedTokens;

	public CallCommand(String cmdLine) throws ShellException, AbstractApplicationException {
	  try {
	    this.commandLine = cmdLine;
	    this.substitutedTokens = substituteAll(Parser.tokenize(cmdLine));
	  } catch (IOException e) {
	    throw new ShellException(e);
	  }
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		if (substitutedTokens.isEmpty()) {
			return;
		}
		InputStream inStream = null;
		OutputStream outStream = null;
		String inFile = findInput();
		String outFile = findOutput();
		try {
			List<String> argsList = findArguments();
			if (inFile == null) {
				inStream = stdin;
			} else {
				inFile = Environment.checkIsFile(inFile);
				inStream = new FileInputStream(new File(inFile));
			}
			outStream = (outFile == null) ? stdout : new FileOutputStream(
					Environment.createFile(outFile));
			Application app = getApplication(argsList.get(0));
			List<String> argsWithoutApp = argsList.subList(1, argsList.size());
			String[] args = argsWithoutApp.toArray(new String[argsWithoutApp
					.size()]);
			app.run(args, inStream, outStream);
		} catch (InvalidFileException e) {
			throw new ShellException(e);
		} catch (IOException e) {
			throw new ShellException(e);
		} finally {
			try {
				if (inStream != null && inFile != null) {
					inStream.close();
				}
				if (outStream != null && outFile != null) {
					outStream.close();
				}
				System.gc();
			} catch (IOException e) {
				throw new ShellException(e);
			}
		}
	}

	/**
	 * Substitute each input token with the corresponding result from command
	 * substitution if necessary.
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @return A list of substituted tokens.
	 */
	public static List<String> substituteAll(List<AbstractToken> tokens)
			throws AbstractApplicationException, ShellException, IOException {
		String current = "";
    List<String> list = new ArrayList<String>();
    for (AbstractToken token : tokens) {
      if (token.getType() == TokenType.SPACES) {
        if (!current.isEmpty()) {
          list.add(current);
          current = "";
        }
      } else {
        current += token.value();
      }
    }
    if (!current.isEmpty()) {
      list.add(current);
    }
    return list;
	}

	/**
	 * Find the input redirection from the command.
	 * 
	 * @return A string which represents the input path, null if the command
	 *         does not have any input redirection.
	 * @throws ShellException
	 *             if the input redirection is invalid or there are multiple
	 *             inputs.
	 */
	public String findInput() throws ShellException {
		String result = null;
		int currentIndex = 0;
		while (currentIndex < substitutedTokens.size()) {
			String token = substitutedTokens.get(currentIndex++);
			if (Parser.isInStream(token)) {
				if (result != null) {
					throw new ShellException(Consts.Messages.TOO_MANY_INPUT
							+ commandLine);
				}
				if (currentIndex == substitutedTokens.size()
						|| Parser.isSpecialCharacter(substitutedTokens
								.get(currentIndex))) {
					throw new ShellException(Consts.Messages.INVALID_INPUT
							+ commandLine);
				}
				result = substitutedTokens.get(currentIndex++);
			}
		}
		return result;
	}

	/**
	 * Find the output redirection from the command.
	 * 
	 * @return A string which represents the output path, null if the command
	 *         does not have any output redirection.
	 * @throws ShellException
	 *             if the output redirection is invalid or there are multiple
	 *             outputs.
	 */
	public String findOutput() throws ShellException {
		String result = null;
		int currentIndex = 0;
		while (currentIndex < substitutedTokens.size()) {
			String token = substitutedTokens.get(currentIndex++);
			if (Parser.isOutStream(token)) {
				if (result != null) {
					throw new ShellException(Consts.Messages.TOO_MANY_OUTPUT
							+ commandLine);
				}
				if (currentIndex == substitutedTokens.size()
						|| Parser.isSpecialCharacter(substitutedTokens
								.get(currentIndex))) {
					throw new ShellException(Consts.Messages.INVALID_OUTPUT
							+ commandLine);
				}
				result = substitutedTokens.get(currentIndex++);
			}
		}
		return result;
	}

	/**
	 * Find the arguments from the command which does not include IO
	 * redirections.
	 * 
	 * @return A list of string that represents arguments.
	 */
	public List<String> findArguments() {
		ArrayList<String> result = new ArrayList<String>();
		int currentIndex = 0;
		while (currentIndex < substitutedTokens.size()) {
			String token = substitutedTokens.get(currentIndex++);
			if (Parser.isOutStream(token) || Parser.isInStream(token)) {
				// Ignore the next token (possibly file name)
				currentIndex++;
			} else {
				result.add(token);
			}
		}
		return result;
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
