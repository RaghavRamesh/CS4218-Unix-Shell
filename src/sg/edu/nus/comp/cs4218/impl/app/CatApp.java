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
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.InvalidFileException;

public class CatApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		if (args == null) {
			throw new CatException(Consts.Messages.ARG_NOT_NULL);
		}

		if (stdout == null) {
			throw new CatException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		BufferedReader reader = null;
		PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(stdout)));
		List<String> validFiles = new ArrayList<String>();

		try {

			if (args.length == 0) {

				if (stdin == null) {
					throw new CatException(Consts.Messages.INP_STR_NOT_NULL);
				}

				reader = new BufferedReader(new InputStreamReader(stdin));
				String line = null;

				while ((line = reader.readLine()) != null) {
					writer.write(line);
				}

				writer.write("\n");
				writer.flush();
				return;
			}

			checkValidityOfFiles(args, validFiles);

			displayContentInFiles(writer, validFiles);
		} catch (IOException exception) {
			throw new CatException(exception);
		} catch (InvalidFileException exception) {
			throw new CatException(exception);
		}

	}

	/**
	 * Reads the contents of files in  the list validFiles and writes it to the writer
	 * @param writer		The writer to which contents have to be written
	 * @param validFiles 	The list of files to display
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void displayContentInFiles(PrintWriter writer,
			List<String> validFiles) throws FileNotFoundException, IOException {
		BufferedReader reader;
		for (String filePath : validFiles) {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				writer.println(line);
			}

			writer.flush();
			reader.close();
		}
	}

	/**
	 * Checks whether the arguments are valid files
	 * @param args	list of arguments
	 * @param validFiles	list of valid file path created from args
	 * @throws InvalidFileException
	 * @throws IOException
	 */
	private void checkValidityOfFiles(String[] args, List<String> validFiles)
			throws InvalidFileException, IOException {
		for (int i = 0; i < args.length; i++) {

			if (args[i].length() <= 0) {
				throw new InvalidFileException(Consts.Messages.FILE_NOT_VALID);
			}

			String filePath = Environment.checkIsFile(args[i]);
			validFiles.add(filePath);
		}
	}

}
