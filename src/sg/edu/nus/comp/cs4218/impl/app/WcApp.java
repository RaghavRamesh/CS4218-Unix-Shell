package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcApp implements Application {

	private static final String TOTAL = "Total";
	boolean displayBytes = false;
	boolean displayWords = false;
	boolean displayLineLength = false;

	int totalBytes = 0;
	int totalWordsLength = 0;
	int totalLineLength = 0;

	int bytesLength = 0;
	int wordsLength = 0;
	int lineLength = 0;

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		if (args == null) {
			throw new WcException(Consts.Messages.ARG_NOT_NULL);
		}

		if (stdout == null) {
			throw new WcException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		BufferedReader reader = null;
		PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(stdout)));

		try {
			ArrayList<String> fileNames = processArguments(args);

			if (fileNames.isEmpty()) {
				reader = processCountFromInputStream(stdin, writer);
				writer.flush();
				return;
			}

			processCountFromFiles(reader, writer, fileNames);
			writer.flush();
		} catch (FileNotFoundException exception) {
			throw new WcException(exception);
		} catch (IOException exception) {
			throw new WcException(exception);
		} catch (InvalidDirectoryException exception) {
			throw new WcException(exception);
		}

	}

	/**
	 * Parses the arguments and identifies file names and options
	 * @param args : file names, options such as -w,-m,-l
	 * @return list of file names
	 * @throws WcException
	 */
	private ArrayList<String> processArguments(String... args)
			throws WcException {
		ArrayList<String> fileNames = new ArrayList<String>();

		for (int i = 0; i < args.length; i++) {

			bytesLength = 0;
			wordsLength = 0;
			lineLength = 0;

			if (args[i].length() <= 0) {
				throw new WcException(Consts.Messages.FILE_NOT_VALID);
			}

			if (args[i].charAt(0) == '-') {
				updateDisplaySetting(args[i]);
			}

			else {
				fileNames.add(args[i]);
			}
		}
		return fileNames;
	}

	private BufferedReader processCountFromInputStream(InputStream stdin,
			PrintWriter writer) throws WcException, IOException {
		BufferedReader reader;
		if (stdin == null) {
			throw new WcException(Consts.Messages.INP_STR_NOT_NULL);
		}

		reader = new BufferedReader(new InputStreamReader(stdin));

		readAndProcessLine(reader);

		displayCountBasedOnArgument(writer, bytesLength, wordsLength,
				lineLength);

		writer.write("\n");
		return reader;
	}

	private void processCountFromFiles(BufferedReader reader,
			PrintWriter writer, ArrayList<String> fileNames)
			throws InvalidDirectoryException, WcException,
			FileNotFoundException, IOException {
		String requiredDirectory;
		for (int k = 0; k < fileNames.size(); k++) {
			requiredDirectory = Environment.getCurrentDirectory()
					+ File.separator + fileNames.get(k);
			File reqdPathAsFile = new File(requiredDirectory);

			boolean pathExists = reqdPathAsFile.exists();
			boolean pathIsFile = reqdPathAsFile.isFile();

			if (!pathExists) {
				throw new WcException("can't open '" + fileNames.get(k) + "'. "
						+ Consts.Messages.FILE_NOT_FOUND);
			}

			if (!pathIsFile) {
				throw new WcException("can't open '" + fileNames.get(k) + "'. "
						+ Consts.Messages.FILE_NOT_VALID);
			}

			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(requiredDirectory)));

			readAndProcessLine(reader);

			displayCountBasedOnArgument(writer, bytesLength, wordsLength,
					lineLength);

			writer.write(fileNames.get(k));
			writer.write("\n");
		}

		if (fileNames.size() > 1) {
			displayCountBasedOnArgument(writer, totalBytes, totalWordsLength,
					totalLineLength);

			writer.println(TOTAL);
		}

		reader.close();
	}

	private void readAndProcessLine(BufferedReader reader) throws IOException {
		// read line by line
		String fileSentence = reader.readLine();
		bytesLength += fileSentence.getBytes().length;
		wordsLength += fileSentence.split(" ").length;
		lineLength += 1;

		totalBytes = totalBytes + bytesLength;
		totalWordsLength = totalWordsLength + wordsLength;
		totalLineLength = totalLineLength + lineLength;
	}

	private void updateDisplaySetting(String arg) throws WcException {
		if ("-m".equals(arg)) {
			displayBytes = true;
		}

		else if ("-w".equals(arg)) {
			displayWords = true;
		}

		else if ("-l".equals(arg)) {
			displayLineLength = true;
		}

		else {
			throw new WcException(Consts.Messages.INVALID_OPTION + "-" + arg);
		}
	}

	private void displayCountBasedOnArgument(PrintWriter writer,
			int bytesLength, int wordsLength, int lineLength) {

		// if all 3 are false, then print everything
		if (!(displayBytes || displayWords || displayLineLength)) {
			displayBytes = true;
			displayWords = true;
			displayLineLength = true;
		}

		if (displayBytes) {
			writer.write(bytesLength + " ");
		}

		if (displayWords) {
			writer.write(wordsLength + " ");
		}

		if (displayLineLength) {
			writer.write(lineLength + " ");
		}

	}

}
