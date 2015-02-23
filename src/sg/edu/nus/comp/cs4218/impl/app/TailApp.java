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
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.TailException;

public class TailApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

		// 4 different cases:
		// --1-- head "sdfsdf" -- only stdin needed
		// --2-- head filename.sdfs -- only arg[0] needed
		// --3-- head -n 15 "afsdfsd" -- arg[0] and stdin needed
		// --4-- head -n 23 filename.sdfsdf -- arg[0] and arg[1] needed

		if (args == null) {
			throw new TailException(Consts.Messages.ARG_NOT_NULL);// TODO: check if this is needed
		}

		if (stdout == null) {
			throw new TailException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		BufferedReader reader = null;
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stdout)));
		long numOfLinesToRead = 10; // by default, read only 10 lines

		if (args.length == 0) { // --1--

			if (stdin == null) {
				throw new TailException(Consts.Messages.NO_INPUT_FILE_OR_STDIN);
			}
			reader = new BufferedReader(new InputStreamReader(stdin));

			try {
				reader.mark(0);
				long lineCount = getLineCount(reader);
				reader.reset();
				writeToPrintStream(writer, lineCount - numOfLinesToRead, numOfLinesToRead, reader);
			} catch (IOException e) {
				throw new TailException(e.getMessage());
			}

		} else if (args.length == 1) {

			try {
				if (stdin == null) { // --2--
					reader = new BufferedReader(new FileReader(args[0]));
				} else { // --3--
					numOfLinesToRead = Integer.parseInt(args[0]);
					reader = new BufferedReader(new InputStreamReader(stdin));
				}

				try {
					reader.mark(0);
					long lineCount = getLineCount(reader);
					reader.reset();
					writeToPrintStream(writer, lineCount - numOfLinesToRead, numOfLinesToRead, new BufferedReader(reader));
				} catch (IOException e) {
					throw new TailException(e.getMessage());
				}
			} catch (FileNotFoundException e) {
				throw new TailException(Consts.Messages.FILE_DOES_NOT_EXIST);
			} catch (NumberFormatException e) {
				throw new TailException(Consts.Messages.ILLEGAL_LINE_COUNT);
			}

		} else if (args.length == 2) { // --4--
			try {
				numOfLinesToRead = Long.parseLong(args[0]);
				reader = new BufferedReader(new FileReader(args[1]));

				try {
					reader.mark(0);
					long lineCount = getLineCount(reader);
					reader.reset();
					writeToPrintStream(writer, lineCount - numOfLinesToRead, numOfLinesToRead, reader);
				} catch (IOException e) {
					throw new TailException(e.getMessage());
				}

			} catch (NumberFormatException e) {
				throw new TailException(Consts.Messages.ILLEGAL_LINE_COUNT);
			} catch (FileNotFoundException e) {
				throw new TailException(Consts.Messages.FILE_DOES_NOT_EXIST);
			}
		} else {
			throw new TailException(Consts.Messages.TOO_MANY_ARGUMENTS);
		}

	}

	private long getLineCount(BufferedReader br) throws AbstractApplicationException {
		long numOfLines = 0;

		try {
			while (br.readLine() != null)
				numOfLines++;
		} catch (IOException e) {
			throw new TailException(e.getMessage());
		}
		return numOfLines;
	}

	private void writeToPrintStream(PrintWriter writer, long numOfLinesToNotRead, long numOfLinesToRead, final BufferedReader br)
			throws AbstractApplicationException {

		// TODO: see if double checking needed
		if (writer == null)
			throw new TailException(Consts.Messages.OUT_STR_NOT_NULL);
		if (br == null)
			throw new TailException(Consts.Messages.IN_STR_NOT_NULL);
		if (numOfLinesToRead < 0)
			throw new TailException(Consts.Messages.ILLEGAL_LINE_COUNT);

		// long lineCount = getLineCount(br);
		// long numOfLinesToNotRead = 0;
		// if (lineCount > numOfLinesToRead)
		// numOfLinesToNotRead = lineCount - numOfLinesToRead;

		String line = null;
		try {

			for (long i = 0; i < numOfLinesToNotRead; i++)
				br.readLine();

			for (long i = 0; i < numOfLinesToRead; i++) {
				if ((line = br.readLine()) != null) {
					writer.write(line);
				} else {
					break;
				}

				writer.write("\n");
			}

			br.close();
			writer.close();
		} catch (IOException e) {
			throw new TailException(e.getMessage());
		}
	}

}
