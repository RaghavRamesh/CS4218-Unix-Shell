package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.CdException;

public class HeadApp implements Application {

	public HeadApp() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

		// TODO: check if all readers (stdin and filereaders) are properly closed
		// TODO: when reading from file, throw exception if file doesnt exist

		// different examples
		// head "sdfsdf" -- only stdin present --1
		// head filename.sdfs -- only arg[0] present --2
		// head -n 15 "afsdfsd" -- arg[0] and stdin present --3
		// head -n 23 filename.sdfsdf -- arg[0] and arg[1] present --4
		// conclusion --

		if (args == null) {
			throw new CdException(Consts.Messages.ARG_NOT_NULL);// TODO
		}

		if (stdout == null) {
			throw new CdException(Consts.Messages.OUT_STR_NOT_NULL);// TODO
		}

		String requiredDirectory;
		BufferedReader reader = null;
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stdout)));
		int numOfLines = 10;

		// if (args.length == 1) {
		// numOfLines = Integer.parseInt(args[0]); // TODO: check if valid?
		// }

		if (args.length == 0) { // 1

			if (stdin == null) {
				throw new CatException("input stream is null"); // TODO
			}
			reader = new BufferedReader(new InputStreamReader(stdin));
			writeToPrintStream(writer, numOfLines, reader);
		} else if (args.length == 1) { // 2,3

			if (stdin == null) { // 2
				reader = new BufferedReader(new FileReader(args[0]));
			} else { // 3

			}

		} else if (args.length == 2) { // 4

		} else { // ERROR too many arguments

		}

	}

	// protected for testing
	protected void writeToPrintStream(PrintWriter writer, int numOfLines, final BufferedReader br) {
		String line = null;

		try {
			for (int i = 0; i < numOfLines; i++) {
				if ((line = br.readLine()) != null) {
					writer.write(line);
				} else {
					break;
				}

				writer.write("\n");
				br.close();
			}
		} catch (IOException e) { // TODO:
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
