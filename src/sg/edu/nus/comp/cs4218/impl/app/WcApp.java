package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import sg.edu.nus.comp.cs4218.exception.InvalidFileException;
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

	BufferedReader reader;
	PrintWriter writer;

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		if (args == null) {
			throw new WcException(Consts.Messages.ARG_NOT_NULL);
		}

		if (stdout == null) {
			throw new WcException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		reader = null;
		writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				stdout)));

		try {
			ArrayList<String> fileNames = new ArrayList<String>();
			ArrayList<String> filePaths = new ArrayList<String>();

			processArguments(fileNames, filePaths, args);

			if (fileNames.isEmpty()) {
				processCountFromInputStream(stdin);
				writer.flush();
				return;
			}

			processCountFromFiles(fileNames, filePaths);
			writer.flush();
		} catch (FileNotFoundException exception) {
			throw new WcException(exception);
		} catch (IOException exception) {
			throw new WcException(exception);
		} catch (InvalidDirectoryException exception) {
			throw new WcException(exception);
		} catch (InvalidFileException exception) {
			throw new WcException(exception);
		}

	}

	/**
	 * Parses the arguments and identifies file names and options
	 * 
	 * @param filePaths
	 *            List of file paths
	 * @param fileNames
	 *            List of file names
	 * @param args
	 *            : file names, options such as -w,-m,-l
	 * @return list of file names
	 * @throws WcException
	 * @throws IOException
	 * @throws InvalidFileException
	 */
	private ArrayList<String> processArguments(ArrayList<String> fileNames,
			ArrayList<String> filePaths, String... args) throws WcException,
			InvalidFileException, IOException {

		for (int i = 0; i < args.length; i++) {

			bytesLength = 0;
			wordsLength = 0;
			lineLength = 0;

			if (args[i] == null ||args[i].length() <= 0) {
				throw new WcException(Consts.Messages.FILE_NOT_VALID);
			}

			if (args[i].charAt(0) == '-') {
				updateDisplaySetting(args[i]);
			}

			else {
				String path = Environment.checkIsFile(args[i]);
				fileNames.add(args[i]);
				filePaths.add(path);
			}
		}
		return filePaths;
	}

	/**
	 * Reads input from the input stream and calculates count for it
	 * 
	 * @param stdin
	 * @throws WcException
	 * @throws IOException
	 */
	private void processCountFromInputStream(InputStream stdin)
			throws WcException, IOException {
		if (stdin == null) {
			throw new WcException(Consts.Messages.INP_STR_NOT_NULL);
		}

		reader = new BufferedReader(new InputStreamReader(stdin));

		readAndProcessLinesInReader(reader);

		displayCount(writer, bytesLength, wordsLength, lineLength);

		writer.write("\n");
	}

	/**
	 * Takes care of reading all the files specified in the argument fileNames
	 * and calculates count for them
	 * 
	 * @param fileNames
	 *            List of file names to process
	 * @param filePaths
	 *            path of files to process
	 * @throws InvalidDirectoryException
	 * @throws WcException
	 * @throws IOException
	 */
	// made protected to test the method
	protected void processCountFromFiles(ArrayList<String> fileNames,
			ArrayList<String> filePaths) throws InvalidDirectoryException,
			WcException, IOException {

		if (fileNames == null)
			throw new WcException(Consts.Messages.ARG_NOT_NULL);

		if (fileNames.isEmpty() || filePaths.isEmpty()) {
			throw new WcException(Consts.Messages.FILE_NOT_VALID);
		}

		if (fileNames.size() != filePaths.size()) {
			throw new WcException(Consts.Messages.FILE_NOT_VALID);
		}
		String requiredDirectory;
		for (int k = 0; k < fileNames.size(); k++) {
			requiredDirectory = filePaths.get(k);

			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(requiredDirectory)));

			// Reset count of bytesLength,wordsLength,lineLength
			this.bytesLength = 0;
			this.wordsLength = 0;
			this.lineLength = 0;
			readAndProcessLinesInReader(reader);

			if (writer != null)
				displayCount(writer, bytesLength, wordsLength, lineLength);

			reader.close();

			if (writer != null) {
				writer.write(fileNames.get(k));
				writer.write("\n");
			}
		}

		if (fileNames.size() > 1) {
			displayCount(writer, totalBytes, totalWordsLength, totalLineLength);

			if (writer != null)
				writer.println(TOTAL);
		}

		reader.close();
	}

	/**
	 * Reads content from the reader and caculates bytes, character and word
	 * lengths
	 * 
	 * @param reader
	 *            reader from which contents have to be read
	 * @throws IOException
	 */
	// made protected to test the method
	protected void readAndProcessLinesInReader(BufferedReader reader)
			throws IOException {
		// read line by line
		String fileSentence = reader.readLine();
		String words[];

		while (fileSentence != null) {
			// +1 to account for new line character
			bytesLength += fileSentence.getBytes().length + 1;
			words = fileSentence.split(" ");
			for (String word : words) {
				if (word.length() > 0) {
					wordsLength = wordsLength + 1;
				}
			}
			lineLength += 1;
			fileSentence = reader.readLine();
		}

		totalBytes = totalBytes + bytesLength;
		totalWordsLength = totalWordsLength + wordsLength;
		totalLineLength = totalLineLength + lineLength;
	}

	/**
	 * Identifies display settings based on the argument and updates it
	 * 
	 * @param arg
	 *            settings info like -m,-w and -l
	 * @throws WcException
	 */
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
			throw new WcException(Consts.Messages.INVALID_OPTION + arg);
		}
	}

	/**
	 * Displays the byte length, word length and line length based on the
	 * settings displayBytes, displayWords and displayLineLength
	 * 
	 * @param writer
	 *            writer to which output has to be written to
	 * @param bytesLength
	 *            byte length to display
	 * @param wordsLength
	 *            word length to display
	 * @param lineLength
	 *            line length to display
	 */
	private void displayCount(PrintWriter writer, int bytesLength,
			int wordsLength, int lineLength) {

		if (writer == null)
			return;

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
