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
		 writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(stdout)));

		try {
			ArrayList<String> fileNames = processArguments(args);

			if (fileNames.isEmpty()) {
				reader = processCountFromInputStream(stdin);
				writer.flush();
				return;
			}

			processCountFromFiles(fileNames);
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
	 * @param args : file names, options such as -w,-m,-l
	 * @return list of file names
	 * @throws WcException
	 * @throws IOException 
	 * @throws InvalidFileException 
	 */
	private ArrayList<String> processArguments(String... args)
			throws WcException, InvalidFileException, IOException {
		ArrayList<String> filePaths = new ArrayList<String>();

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
				Environment.checkIsFile(args[i]);
				filePaths.add(args[i]);
			}
		}
		return filePaths;
	}

	private BufferedReader processCountFromInputStream(InputStream stdin) throws WcException, IOException {
		if (stdin == null) {
			throw new WcException(Consts.Messages.INP_STR_NOT_NULL);
		}

		reader = new BufferedReader(new InputStreamReader(stdin));

		readAndProcessLinesInReader(reader);

		displayCountBasedOnArgument(writer, bytesLength, wordsLength,
				lineLength);

		writer.write("\n");
		return reader;
	}

	// made protected to test the method
	protected void processCountFromFiles(ArrayList<String> fileNames)
			throws InvalidDirectoryException, WcException, IOException
			 {
		
		if(fileNames == null)
			throw new WcException(Consts.Messages.ARG_NOT_NULL);
		
		String requiredDirectory;
		for (int k = 0; k < fileNames.size(); k++) {
			requiredDirectory = Environment.getCurrentDirectory()
					+ File.separator + fileNames.get(k);
			
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(requiredDirectory)));

			// Reset count of bytesLength,wordsLength,lineLength
			this.bytesLength = 0;
			this.wordsLength = 0;
			this.lineLength = 0;
			readAndProcessLinesInReader(reader);

			if(writer != null)
			displayCountBasedOnArgument(writer, bytesLength, wordsLength,
					lineLength);

			reader.close();
			
			if(writer!= null){
			writer.write(fileNames.get(k));
			writer.write("\n");
			}
		}

		if (fileNames.size() > 1) {
			displayCountBasedOnArgument(writer, totalBytes, totalWordsLength,
					totalLineLength);

			if(writer!=null)
			writer.println(TOTAL);
		}

		reader.close();
	}

	// made protected to test the method
	protected void readAndProcessLinesInReader(BufferedReader reader) throws IOException {
		// read line by line
		String fileSentence = reader.readLine();
		String words[];
		
		while (fileSentence != null) {
			// +1 to account for new line character
			bytesLength += fileSentence.getBytes().length +1; 
			words = fileSentence.split(" ");
			for(String word:words){
				if(word.length()>0){
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

		if(writer == null)
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
