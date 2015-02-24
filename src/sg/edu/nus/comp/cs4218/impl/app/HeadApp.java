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
import sg.edu.nus.comp.cs4218.exception.HeadException;

public class HeadApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

		// 4 different cases:
		// --1-- head "sdfsdf" -- only stdin needed
		// --2-- head filename.sdfs -- only arg[0] needed
		// --3-- head -n 15 "afsdfsd" -- arg[0] and stdin needed
		// --4-- head -n 23 filename.sdfsdf -- arg[0] and arg[1] needed

		if (args == null) {
			throw new HeadException(Consts.Messages.ARG_NOT_NULL);// TODO: check if this is needed
		}

		if (stdout == null) {
			throw new HeadException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		BufferedReader reader = null;
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stdout)));
		int numOfLines = 10; // by default, read only 10 lines

		if (args.length == 0) { // --1--

			if (stdin == null) {
				throw new HeadException(Consts.Messages.NO_INPUT_FILE_OR_STDIN);
			}
			reader = new BufferedReader(new InputStreamReader(stdin));
			writeToPrintStream(writer, numOfLines, reader);

		} else if (args.length == 1) {

			try {
				if (stdin == null) { // --2--
					reader = new BufferedReader(new FileReader(args[0]));
				} else { // --3--
					numOfLines = Integer.parseInt(args[0]);
					reader = new BufferedReader(new InputStreamReader(stdin));
				}
				writeToPrintStream(writer, numOfLines, reader);
			} catch (FileNotFoundException e) {
				throw new HeadException(Consts.Messages.FILE_DOES_NOT_EXIST);
			} catch (NumberFormatException e) {
				throw new HeadException(Consts.Messages.ILLEGAL_LINE_COUNT);
			}

		} else if (args.length == 2) { // --4--
			try {
				numOfLines = Integer.parseInt(args[0]);
				reader = new BufferedReader(new FileReader(args[1]));
				writeToPrintStream(writer, numOfLines, reader);
			} catch (NumberFormatException e) {
				throw new HeadException(Consts.Messages.ILLEGAL_LINE_COUNT);
			} catch (FileNotFoundException e) {
				throw new HeadException(Consts.Messages.FILE_DOES_NOT_EXIST);
			}
		} else {
			throw new HeadException(Consts.Messages.TOO_MANY_ARGUMENTS);
		}

	}

	protected void writeToPrintStream(PrintWriter writer, int numOfLines, final BufferedReader br) throws AbstractApplicationException {

		// TODO: see if double checking needed
		if (writer == null)
			throw new HeadException(Consts.Messages.OUT_STR_NOT_NULL);
		if (br == null)
			throw new HeadException(Consts.Messages.IN_STR_NOT_NULL);
		if (numOfLines < 0)
			throw new HeadException(Consts.Messages.ILLEGAL_LINE_COUNT);
		;

		String line = null;
		try {
			for (int i = 0; i < numOfLines; i++) {
				if ((line = br.readLine()) != null) {
					writer.write(line);
				} else {
					break;
				}

				writer.write(System.getProperty("line.separator"));
			}

			br.close();
			writer.close();
		} catch (IOException e) {
			throw new HeadException(e.getMessage());
		}
	}

}
