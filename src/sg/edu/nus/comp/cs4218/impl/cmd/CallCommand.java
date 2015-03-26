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
	private List<String> substitutedTokens;
	private String inputPath;
	private String outputPath;
	private InputStream inStream;
	private OutputStream outStream;
	private Boolean closeOutput;

	public CallCommand(String cmdLine) throws ShellException, AbstractApplicationException {
		try {
			this.closeOutput = false;
			this.commandLine = cmdLine;
			List<AbstractToken> tokens = Parser.tokenize(cmdLine);
			for (AbstractToken token : tokens) {
				token.checkValid();
			}
			this.substitutedTokens = substituteAll(tokens);
			this.inputPath = findInput(substitutedTokens);
			this.outputPath = findOutput(substitutedTokens);
			try {
				this.substitutedTokens = findGlobbing(substitutedTokens);
			} catch (InvalidDirectoryException e) {
				throw new ShellException(e.getMessage());
			}

		} catch (IOException e) {
			throw new ShellException(e);
		}
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout) throws AbstractApplicationException, ShellException {
		if (substitutedTokens.isEmpty()) {
			return;
		}

		try {
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
		} finally {
			terminate();
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
	public static List<String> substituteAll(List<AbstractToken> tokens) throws AbstractApplicationException, ShellException, IOException {
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
			} else if (type == TokenType.BACK_QUOTES) {
				// Split the strings inside into multiple args
				List<String> strList = normalize(val);
				if (!strList.isEmpty()) {
					current = current == null ? strList.get(0) : current + strList.get(0);
					for (int i = 1; i < strList.size(); i++) {
						list.add(current);
						current = strList.get(i);
					}
				}
			} else {
				current = current == null ? val : current + val;
			}
		}
		addNonNull(list, current);
		return list;
	}

	private static List<String> normalize(String input) {
		String str = input.replaceAll("\\s+", " ").trim();
		return Arrays.asList(str.split("\\s"));
	}

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
				if (currentIndex == tokens.size() || Parser.isSpecialCharacter(tokens.get(currentIndex))) {
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
				if (currentIndex == tokens.size() || Parser.isSpecialCharacter(tokens.get(currentIndex))) {
					throw new ShellException(Consts.Messages.INVALID_OUTPUT);
				}
				result = tokens.get(currentIndex++);
			}
		}
		return result;
	}

	public static List<String> findGlobbing(List<String> tokens) throws ShellException, IOException, InvalidDirectoryException {
		// if the first argument is find, return
		if (tokens.get(0).equals("find"))
			return tokens;
		
		// find the token with *
		// if not found a token with *, return as is
		// if found, replace that token with several tokens that were returned while searching

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
		int indexOfLastFileSeparator = relevantToken.lastIndexOf(File.separator);

		// if there is file seperator after *
		if (indexOfLastFileSeparator != -1 && indexOfLastFileSeparator > indexOfFirstAsterisk)
			return tokens;

		String directoryPrefix = "";
		String globPattern = null;
		String absolutePathOfDirToLookIn = null;
		if (indexOfLastFileSeparator == -1) {
			globPattern = relevantToken;
			absolutePathOfDirToLookIn = Environment.getCurrentDirectory();
		} else {
			directoryPrefix = relevantToken.substring(0, indexOfLastFileSeparator); // do not include the file separator
			globPattern = relevantToken.substring(indexOfLastFileSeparator + 1);
			absolutePathOfDirToLookIn = Environment.checkIsDirectory(directoryPrefix);
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
			return (outputPath == null) ? null : new FileOutputStream(Environment.createFile(outputPath));
		} catch (FileNotFoundException e) {
			throw new ShellException(e);
		}
	}
}
