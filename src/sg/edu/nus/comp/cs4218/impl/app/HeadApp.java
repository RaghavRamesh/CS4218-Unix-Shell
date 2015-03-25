package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.HeadException;
import sg.edu.nus.comp.cs4218.exception.InvalidFileException;

public class HeadApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		// 4 different cases:
		// --1-- head "sdfsdf" -- only stdin needed
		// --2-- head filename.sdfs -- only arg[0] needed
		// --3-- head -n 15 "afsdfsd" -- arg[0], arg[1] and stdin needed
		// --4-- head -n 23 filename.sdfsdf -- arg[0], arg[1] and arg[2] needed

		if (args == null) {
			throw new HeadException(Consts.Messages.ARG_NOT_NULL);
		}

		if (stdout == null) {
			throw new HeadException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		BufferedReader reader = null;
		PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(stdout)));
		int numOfLines = 10; // by default, read only 10 lines

		if (args.length == 0) { // case 1

			if (stdin == null) {
				throw new HeadException(Consts.Messages.IN_STR_NOT_NULL);
			}
			reader = new BufferedReader(new InputStreamReader(stdin));
			writeToPrintStream(writer, numOfLines, reader);

		} else if (args.length == 1) {// case 2

			if (args[0] == null)
				throw new HeadException(Consts.Messages.ARG_NOT_NULL);
			if (args[0].length() == 0)
				throw new HeadException(Consts.Messages.ARG_NOT_EMPTY);

			try {
				reader = new BufferedReader(new FileReader(Environment.checkIsFile(args[0])));
			} catch (InvalidFileException | IOException e) {
				throw new HeadException(e);
			}
		
			writeToPrintStream(writer, numOfLines, reader);

		} else if (args.length == 2 || args.length == 3) { // case 3 or 4

			if (args[0] == null || args[1] == null)
				throw new HeadException(Consts.Messages.ARG_NOT_NULL);
			if (args[0].length() == 0 || args[1].length() == 0)
				throw new HeadException(Consts.Messages.ARG_NOT_EMPTY);

			if (args[0].equals("-n")) {
				try {
					numOfLines = Integer.parseInt(args[1]);
					if (args.length == 2)
						reader = new BufferedReader(
								new InputStreamReader(stdin));// case 3
					else {
						if (args[2] == null)
							throw new HeadException(
									Consts.Messages.ARG_NOT_NULL);
						if (args[2].length() == 0)
							throw new HeadException(
									Consts.Messages.ARG_NOT_EMPTY);
						reader = new BufferedReader(new FileReader(Environment.checkIsFile((args[2]))));// case
																				// 4
					}
					writeToPrintStream(writer, numOfLines, reader);
				} catch (NumberFormatException e) {
					throw new HeadException(e);
				} catch (FileNotFoundException e) {
					throw new HeadException(e);
				} catch (InvalidFileException e) {
					throw new HeadException(e);
				} catch (IOException e) {
					throw new HeadException(e);
				}
			} else {
				throw new HeadException(Consts.Messages.INVALID_OPTION);
			}
		} else {
			throw new HeadException(Consts.Messages.TOO_MANY_ARGS);
		}

	}

	/**
	 * Writes to the PrintWriter object while reading from the BufferedReader
	 * object from the top upto a limited number of lines specified
	 * 
	 * @param writer
	 *            : a PrintWriter object to write to
	 * @param numOfLines
	 *            : number of lines to limit writing to
	 * @param reader
	 *            : BufferedReader object to read from
	 * @throws AbstractApplicationException
	 */
	protected void writeToPrintStream(PrintWriter writer, int numOfLines,
			final BufferedReader reader) throws AbstractApplicationException {

		if (writer == null)
			throw new HeadException(Consts.Messages.OUT_STR_NOT_NULL);
		if (reader == null)
			throw new HeadException(Consts.Messages.IN_STR_NOT_NULL);
		if (numOfLines < 0)
			throw new HeadException(Consts.Messages.ILLEGAL_LINE_CNT);

		String line = null;
		try {
			for (int i = 0; i < numOfLines; i++) {
				if ((line = reader.readLine()) == null) {
					break;
				} else {
					writer.write(line);
				}

				writer.write(System.getProperty("line.separator"));
			}

			reader.close();
			writer.flush();
		} catch (IOException e) {
			throw new HeadException(e);
		}
	}

}
