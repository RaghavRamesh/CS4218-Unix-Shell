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
import java.util.LinkedList;
import java.util.Queue;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidFileException;
import sg.edu.nus.comp.cs4218.exception.TailException;

public class TailApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		// 4 different cases:
		// --1-- tail "sdfsdf" -- only stdin needed
		// --2-- tail filename.sdfs -- only arg[0] needed
		// --3-- tail -n 15 "afsdfsd" -- arg[0], arg[1] and stdin needed
		// --4-- tail -n 23 filename.sdfsdf -- arg[0], arg[1] and arg[2] needed

		if (args == null) {
			throw new TailException(Consts.Messages.ARG_NOT_NULL);// TODO: check
																	// if this
																	// is needed
		}

		if (stdout == null) {
			throw new TailException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		BufferedReader reader = null;
		PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(stdout)));
		int numOfLines = 10; // by default, read only 10 lines

		if (args.length == 0) { // case 1

			if (stdin == null) {
				throw new TailException(Consts.Messages.IN_STR_NOT_NULL);
			}
			reader = new BufferedReader(new InputStreamReader(stdin));
			writeToPrintStream(writer, numOfLines, reader);

		} else if (args.length == 1) { // case 2

			if (args[0] == null)
				throw new TailException(Consts.Messages.ARG_NOT_NULL);
			if (args[0].length() == 0)
				throw new TailException(Consts.Messages.ARG_NOT_EMPTY);

			try {
				reader = new BufferedReader(new FileReader(
						Environment.checkIsFile(args[0])));
			} catch (InvalidFileException | IOException e) {
				throw new TailException(e);
			}
			writeToPrintStream(writer, numOfLines, reader);

		} else if (args.length == 2 || args.length == 3) { // case 3 or 4

			if (args[0] == null || args[1] == null)
				throw new TailException(Consts.Messages.ARG_NOT_NULL);
			if (args[0].length() == 0 || args[1].length() == 0)
				throw new TailException(Consts.Messages.ARG_NOT_EMPTY);

			if (args[0].equals("-n")) {
				try {
					numOfLines = Integer.parseInt(args[1]);
					if (args.length == 2)
						reader = new BufferedReader(
								new InputStreamReader(stdin));// case 3
					else {
						if (args[2] == null)
							throw new TailException(
									Consts.Messages.ARG_NOT_NULL);
						if (args[2].length() == 0)
							throw new TailException(
									Consts.Messages.ARG_NOT_EMPTY);
						reader = new BufferedReader(new FileReader(
								Environment.checkIsFile(args[2])));// case
						// 4
					}
					writeToPrintStream(writer, numOfLines, reader);
				} catch (NumberFormatException e) {
					throw new TailException(e);
				} catch (FileNotFoundException e) {
					throw new TailException(e);
				} catch (InvalidFileException e) {
					throw new TailException(e);
				} catch (IOException e) {
					throw new TailException(e);
				}
			} else {
				throw new TailException(Consts.Messages.INVALID_OPTION);
			}
		} else {
			throw new TailException(Consts.Messages.TOO_MANY_ARGS);
		}

	}

	/**
	 * Writes to the PrintWriter object while reading from the BufferedReader
	 * object from the end upto a limited number of lines specified
	 * 
	 * @param writer
	 *            : a PrintWriter object to write to
	 * @param numOfLines
	 *            : number of lines to limit writing to
	 * @param reader
	 *            : BufferedReader object to read from
	 * @throws AbstractApplicationException
	 */
	private void writeToPrintStream(PrintWriter writer, int numOfLinesToRead,
			final BufferedReader reader) throws AbstractApplicationException {

		if (writer == null)
			throw new TailException(Consts.Messages.OUT_STR_NOT_NULL);
		if (reader == null)
			throw new TailException(Consts.Messages.IN_STR_NOT_NULL);
		if (numOfLinesToRead < 0)
			throw new TailException(Consts.Messages.ILLEGAL_LINE_CNT);

		Queue<String> lines = new LinkedList<String>();
		String line = null;

		try {
			for (int i = 0; i < numOfLinesToRead; i++) {
				if ((line = reader.readLine()) == null) {
					break;
				} else {
					lines.add(line);
				}
			}

			if (!lines.isEmpty()) {
				while ((line = reader.readLine()) != null) {
					lines.remove();
					lines.add(line);
				}
			}

			while (!lines.isEmpty()) {
				writer.write(lines.remove());
				writer.write(System.getProperty("line.separator"));
			}

			reader.close();
			writer.flush();
		} catch (IOException exception) {
			throw new TailException(exception);
		}
	}
}
