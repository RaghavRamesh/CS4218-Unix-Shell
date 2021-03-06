package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.GlobFileSearcher;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;
import sg.edu.nus.comp.cs4218.exception.InvalidFileException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;
import sg.edu.nus.comp.cs4218.impl.app.ApplicationFactory;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class CallCommand implements Command {
	private final String commandLine;
	private String substitutedCommand;
	private List<String> substitutedTokens;
	private String inputPath;
	private String outputPath;
	private InputStream inStream;
	private OutputStream outStream;
	private Boolean closeOutput;

	public CallCommand(String cmdLine) throws ShellException,
			AbstractApplicationException {
		try {
			this.closeOutput = false;
			this.commandLine = cmdLine;
			this.substitutedCommand = substituteBackquotes(cmdLine);
			this.substitutedTokens = splitArguments(substitutedCommand);
			this.inputPath = findInput(substitutedTokens);
			this.outputPath = findOutput(substitutedTokens);
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

		try {
			substitutedTokens = findGlobbing(substitutedTokens);

			// Priority IO redirection first
			inStream = getInputStreamFromCommand();
			if (inStream == null) {
				inStream = stdin;
			}
			outStream = getOutputStreamFromCommand();
			if (outStream == null) {
				outStream = stdout;
			}
			// Generate application and arguments
			List<String> argsList = findArguments();
			Application app = getApplication(argsList.get(0));
			List<String> argsWithoutApp = argsList.subList(1, argsList.size());
			int numberOfArgs = argsWithoutApp.size();
			String[] args = argsWithoutApp.toArray(new String[numberOfArgs]);
			app.run(args, inStream, outStream);
		} catch (IOException e) {
			throw new ShellException(e);
		} catch (InvalidDirectoryException e) {
			throw new ShellException(e);
		} finally {
			terminate();
		}
	}

	/**
	 * Substitute each back quote token with the corresponding result from
	 * command substitution if necessary.
	 * 
	 * @param cmdLine
	 *            original command line
	 * @return command line after command substitution.
	 */
	public static String substituteBackquotes(String cmdLine)
			throws ShellException, AbstractApplicationException {
		List<AbstractToken> tokens = Parser.tokenize(cmdLine);
		for (AbstractToken token : tokens) {
			token.checkValid();
		}
		String result = "";
		for (AbstractToken token : tokens) {
			TokenType type = token.getType();
			String val = token.value();
			if (type == TokenType.BACK_QUOTES) {
				// Split the strings inside into multiple args
				List<String> strList = normalize(val);
				if (!strList.isEmpty()) {
					result += strList.get(0);
					for (int i = 1; i < strList.size(); i++) {
						result += " " + strList.get(i);
					}
				}
			} else {
				result += val;
			}
		}
		return result;
	}

	/**
	 * Split the command line into arguments/tokens. Also remove quotes from
	 * quote tokens.
	 * 
	 * @param input
	 *            the command line after command substitution
	 * @return A list of substituted tokens.
	 */
	public static List<String> splitArguments(String input)
			throws AbstractApplicationException, ShellException, IOException {
		List<AbstractToken> tokens = Parser.tokenize(input);
		for (AbstractToken token : tokens) {
			token.checkValid();
		}
		String current = null;
		List<String> list = new ArrayList<String>();
		for (AbstractToken token : tokens) {
			TokenType type = token.getType();
			String val = token.value();
			if (type == TokenType.SPACES) {
				addNonNull(list, current);
				current = null;
			} else if (type == TokenType.INPUT || type == TokenType.OUTPUT) {
				addNonNull(list, current);
				current = null;
				list.add(val);
			} else {
				if (type == TokenType.SINGLE_QUOTES
						|| type == TokenType.DOUBLE_QUOTES) {
					val = val.substring(1, val.length() - 1);
				}
				current = current == null ? val : current + val;
			}
		}
		addNonNull(list, current);
		return list;
	}

	/**
	 * Takes a string input and trims the string
	 * 
	 * @param String
	 *            to trim
	 * @return List of split strings
	 */
	private static List<String> normalize(String input) {
		String str = input.replaceAll("\\s+", " ").trim();
		return Arrays.asList(str.split("\\s"));
	}

	/**
	 * Adds non null string to list
	 * 
	 * @param list
	 *            of strings
	 * @param non
	 *            null str
	 */
	private static void addNonNull(List<String> list, String str) {
		if (str != null) {
			list.add(str);
		}
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
	public static String findInput(List<String> tokens) throws ShellException {
		String result = null;
		int currentIndex = 0;
		while (currentIndex < tokens.size()) {
			String token = tokens.get(currentIndex++);
			if (Parser.isInStream(token)) {
				if (result != null) {
					throw new ShellException(Consts.Messages.TOO_MANY_INPUT);
				}
				if (currentIndex == tokens.size()
						|| Parser.isSpecialCharacter(tokens.get(currentIndex))) {
					throw new ShellException(Consts.Messages.INVALID_INPUT);
				}
				result = tokens.get(currentIndex++);
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
	public static String findOutput(List<String> tokens) throws ShellException {
		String result = null;
		int currentIndex = 0;
		while (currentIndex < tokens.size()) {
			String token = tokens.get(currentIndex++);
			if (Parser.isOutStream(token)) {
				if (result != null) {
					throw new ShellException(Consts.Messages.TOO_MANY_OUTPUT);
				}
				if (currentIndex == tokens.size()
						|| Parser.isSpecialCharacter(tokens.get(currentIndex))) {
					throw new ShellException(Consts.Messages.INVALID_OUTPUT);
				}
				result = tokens.get(currentIndex++);
			}
		}
		return result;
	}

	/**
	 * Performs globbing by substituting the value of * with corresponding
	 * values
	 * 
	 * @param tokens
	 *            that may contain *
	 * @return * substituted tokens
	 * @throws ShellException
	 * @throws IOException
	 * @throws InvalidDirectoryException
	 */
	public static List<String> findGlobbing(List<String> tokens)
			throws ShellException, IOException, InvalidDirectoryException {
		// if the first argument is find, return
		if (tokens.get(0).equals("find"))
			return tokens;

		// find the token with *
		// if not found a token with *, return as is
		// if found, replace that token with several tokens that were returned
		// while searching

		String relevantToken = null;
		int relevantTokenIdx = 0;
		for (int idx = 0; idx < tokens.size(); idx++) {
			if (tokens.get(idx).contains("*")) {
				relevantToken = tokens.get(idx);
				relevantTokenIdx = idx;
				break;
			}
		}

		if (relevantToken == null)
			return tokens;

		int indexOfFirstAsterisk = relevantToken.indexOf('*');
		int indexOfLastFileSeparator = relevantToken.lastIndexOf("/");

		// if there is file separator after *
		if (indexOfLastFileSeparator != -1
				&& indexOfLastFileSeparator > indexOfFirstAsterisk)
			return tokens;

		String directoryPrefix = "";
		String globPattern = null;
		String absolutePathOfDirToLookIn = null;
		if (indexOfLastFileSeparator == -1) {
			globPattern = relevantToken;
			absolutePathOfDirToLookIn = Environment.getCurrentDirectory();
		} else {
			directoryPrefix = relevantToken.substring(0,
					indexOfLastFileSeparator); // do not include the file
												// separator
			globPattern = relevantToken.substring(indexOfLastFileSeparator + 1);
			absolutePathOfDirToLookIn = Environment
					.checkIsDirectory(directoryPrefix);
		}

		GlobFileSearcher fileSearcher = new GlobFileSearcher(globPattern);
		Files.walkFileTree(Paths.get(absolutePathOfDirToLookIn), fileSearcher);
		List<String> fileAndDirNames = fileSearcher.getResultList();

		if (fileAndDirNames.isEmpty())
			return tokens;

		List<String> newTokensList = new ArrayList<String>(tokens);
		newTokensList.remove(relevantTokenIdx);
		for (int idx = 0; idx < fileAndDirNames.size(); idx++) {
			newTokensList.add(relevantTokenIdx + idx, fileAndDirNames.get(idx));
		}

		return newTokensList;
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
		try {
			if (inStream != null) {
				inStream.close();
			}
			if (outStream != null && (outputPath != null || closeOutput)) {
				outStream.close();
			}
			System.gc();
		} catch (IOException e) {
			// Do nothing
		}
	}

	private Application getApplication(String appId) throws ShellException {
		ApplicationFactory factory = new ApplicationFactory();
		return factory.getApplication(appId);
	}

	// public String getInputPath() {
	// return inputPath;
	// }
	//
	// public String getOutputPath() {
	// return outputPath;
	// }

	public void setCloseOutput(Boolean closeOutput) {
		this.closeOutput = closeOutput;
	}

	public InputStream getInputStreamFromCommand() throws ShellException {
		if (inputPath == null) {
			return null;
		} else {
			try {
				inputPath = Environment.checkIsFile(inputPath);
				return new FileInputStream(new File(inputPath));
			} catch (InvalidFileException e) {
				throw new ShellException(e);
			} catch (IOException e) {
				throw new ShellException(e);
			}
		}
	}

	public OutputStream getOutputStreamFromCommand() throws ShellException {
		try {
			return (outputPath == null) ? null : new FileOutputStream(
					Environment.createFile(outputPath));
		} catch (FileNotFoundException e) {
			throw new ShellException(e);
		}
	}
}
