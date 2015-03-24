package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
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
	private final List<String> substitutedTokens;
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
//			System.out.println("[CallCommand] cmdLine: " + cmdLine);
			List<AbstractToken> tokens = Parser.tokenize(cmdLine);
			for (AbstractToken token : tokens) {
				token.checkValid();
			}
			this.substitutedTokens = substituteAll(tokens);
			this.inputPath = findInput(substitutedTokens);
//			System.out.println("[CallCommand] inputPath: " + this.inputPath);
			this.outputPath = findOutput(substitutedTokens);
//			System.out.println("[CallCommand] outputPath: " + this.outputPath);
			this.findGlobbing(substitutedTokens);
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
	public static List<String> substituteAll(List<AbstractToken> tokens)
			throws AbstractApplicationException, ShellException, IOException {
		String current = "";
		List<String> list = new ArrayList<String>();
		for (AbstractToken token : tokens) {
			TokenType type = token.getType();
//			System.out.println("[CallCommand] type: " + type);
			if (type == TokenType.SPACES) {
				addNonEmpty(list, current);
				current = "";
			} else if (type == TokenType.INPUT || type == TokenType.OUTPUT) {
				addNonEmpty(list, current);
				current = "";
				list.add(token.value());
			} else {
				current += token.value();
			}
		}
		if (!current.isEmpty()) {
			list.add(current);
		}
		return list;
	}

	private static void addNonEmpty(List<String> list, String str) {
		if (!str.isEmpty()) {
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
	
	public static String findGlobbing(List<String> tokens) throws ShellException {
		String result = "";
		int currentIndex = 0;
		while (currentIndex < tokens.size()) {
			String token = tokens.get(currentIndex++);
			if (token.contains(String.valueOf('*'))) {
				if (token.matches("\\*")) {
					// If only *, return all contents
					try {
						File[] files = Environment.getContentsInDirectory(Environment.getCurrentDirectory());
						for (File file : files) 
							result += file.getName() + " ";
					} catch (InvalidDirectoryException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
//					System.out.println(result);
				} else if (token.matches("[a-z|A-Z|0-9]+\\/\\*\\/[a-z|A-Z|0-9]+")) {
					// if it is between slashes do nothing
					result = token;
					// System.out.println("[findGlobbing]: " + result);
				} else if (token.matches("([(a-z|A-Z|0-9|\\*)]?)+\\*([(a-z|A-Z|0-9|\\*)]?)+")) {
					// if there is a match print only those that matched
					// Get contents, filter according to token
					// 3 cases; 1. * before; 2. * after; 3. * before and after
					String strippedToken = token.replace("*", "");
					try {
						File[] files = Environment.getContentsInDirectory(Environment.getCurrentDirectory());
						for (File file : files) {
							if ((file.getName().matches("(([a-z|A-Z|0-9|.]))+" + strippedToken + "\\b")) 
									&& (token.matches("(([a-z|A-Z|0-9|.|\\*]))+" + strippedToken + "\\b"))) {
								System.out.println("[findGlobbing] 1st case: " + file.getName());
								result+=file.getName() + " ";
							} else if ((file.getName().matches("\\b" + strippedToken + "(([a-z|A-Z|0-9|.]))+\\b"))
									&& (token.matches("\\b" + strippedToken + "(([a-z|A-Z|0-9|.|*]))+"))) {
								System.out.println("[findGlobbing] 2nd case: " + file.getName());
								result += file.getName() + " ";
							} else if ((file.getName().matches("(([a-z|A-Z|0-9|.]))+" + strippedToken + "(([a-z|A-Z|0-9|.]))+")) 
									&& (token.matches("(([a-z|A-Z|0-9|.|\\*]))+" + strippedToken + "(([a-z|A-Z|0-9|.|\\*]))+"))){
								System.out.println("[findGlobbing] 3rd case: " + file.getName());
								result += file.getName() + " ";
							}
						}
					} catch (InvalidDirectoryException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} 
			}
		}
		System.out.println("Result: " + result);
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

	public String getInputPath() {
		return inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

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
