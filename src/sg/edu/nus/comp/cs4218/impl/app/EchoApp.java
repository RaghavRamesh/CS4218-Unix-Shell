package sg.edu.nus.comp.cs4218.impl.app;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.EchoException;

public class EchoApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if (stdout == null)
			throw new EchoException(Consts.Messages.OUT_STR_NOT_NULL); // TODO:

		if (args == null)
			throw new EchoException(Consts.Messages.ARG_NOT_NULL); // TODO:

		final PrintStream printStream = new PrintStream(stdout);
		for (String arg : args) {
			printStream.print(arg); // TODO: check if need spaces
		}
		printStream.println();
		printStream.close();

	}

}
