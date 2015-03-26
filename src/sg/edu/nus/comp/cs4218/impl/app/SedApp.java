package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidFileException;
import sg.edu.nus.comp.cs4218.exception.SedException;

public class SedApp implements Application {

	boolean isGlobalReplace;

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		if (args == null) {
			throw new SedException(Consts.Messages.ARG_NOT_NULL);
		}

		if (args.length < 1) {
			throw new SedException(Consts.Messages.EXT_MIN_ONE_ARG);
		}

		if (stdout == null) {
			throw new SedException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		String regexReplacement = args[0];

		if (!regexReplacement.substring(0, 1).equals("s")) {
			throw new SedException(Consts.Messages.CANNOT_FIND_S);
		}

		List<Integer> indicesOfSymbol = processRegexInputString(regexReplacement);

		String regularExp = regexReplacement.substring(
				indicesOfSymbol.get(0) + 1, indicesOfSymbol.get(1));
		String replacement = regexReplacement.substring(
				indicesOfSymbol.get(1) + 1, indicesOfSymbol.get(2));

		InputStream streamToRead = null;

		boolean isStdinInput = false;

		if (args.length > 1) {
			String fileName = args[1];
			try {
				String path = Environment.checkIsFile(fileName);
				streamToRead = new FileInputStream(path);
			} catch (InvalidFileException | IOException e) {
				throw new SedException(e);
			}
		} else {
			if (stdin == null) {
				throw new SedException(Consts.Messages.INP_STR_NOT_NULL);
			}
			isStdinInput = true;
			streamToRead = stdin;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				streamToRead));
		PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(stdout)));
		String inputLine;
		Pattern pattern = Pattern.compile(regularExp);
		try {
			int count = 0;
			while ((inputLine = reader.readLine()) != null) {
				count++;
				Matcher matcher = pattern.matcher(inputLine);

				String output = "";
				if (this.isGlobalReplace) {
					output = matcher.replaceAll(replacement);
				} else {
					output = matcher.replaceFirst(replacement);
				}
				writer.println(output);
			}
			if (count == 0) {
				writer.write(System.lineSeparator());
			}
		} catch (IOException e) {
			throw new SedException(e);
		} finally {
			writer.flush();
			if (!isStdinInput) {
				try {
					streamToRead.close();
				} catch (IOException e) {
					throw new SedException(e);
				}
			}
		}
	}

	/**
	 * 
	 * @param regexReplacement
	 *            string that has to be processed
	 * @return list of indices of the symbol
	 * @throws SedException
	 */
	private List<Integer> processRegexInputString(String regexReplacement)
			throws SedException {
		// identify character at index 1 which is / (eg char at index 1 in
		// s/apple/babana)
		String symbol = regexReplacement.substring(1, 2);

		// Represents list of valid symbols
		// We don't allow ` because actual shell does not seem to allow it
		String validSymbolsRegex = "([^a-zA-Z0-9` 	])";
		boolean isValidRegex = symbol.matches(validSymbolsRegex);

		if (!isValidRegex) {
			throw new SedException(Consts.Messages.INVALID_SYMBOL);
		}

		List<Integer> indicesOfSymbol = new ArrayList<Integer>();

		for (int i = 0; i < regexReplacement.length(); i++) {
			if (regexReplacement.charAt(i) == symbol.charAt(0)) {
				indicesOfSymbol.add(i);
			}
		}

		if (indicesOfSymbol.size() != 3) {
			throw new SedException(Consts.Messages.UNTERMINATED_SYM + symbol);
		}

		if (indicesOfSymbol.get(2) == regexReplacement.length() - 1) {
			isGlobalReplace = false;
		} else if (indicesOfSymbol.get(2) == regexReplacement.length() - 2
				&& regexReplacement.charAt(regexReplacement.length() - 1) == 'g') {
			isGlobalReplace = true;
		}

		else {
			throw new SedException(Consts.Messages.BAD_OPTION_IN_SUB);
		}
		return indicesOfSymbol;
	}

}
