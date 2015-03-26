package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.FindFileSearcher;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.FindException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class FindApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {
		if (args == null) {
			throw new FindException(Consts.Messages.ARG_NOT_NULL);
		}

		if (stdout == null) {
			throw new FindException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		if (args.length == 0 || args.length == 1) {
			throw new FindException(Consts.Messages.ARG_NOT_NULL);
		} else if (args.length == 2) {
			if (args[0] == null || args[1] == null)
				throw new FindException(Consts.Messages.ARG_NOT_NULL);
			if (args[0].length() == 0 || args[1].length() == 0)
				throw new FindException(Consts.Messages.ARG_NOT_EMPTY);

			if (!args[0].equals("-name")) {
				throw new FindException(Consts.Messages.INVALID_OPTION);
			}

			try {
				PrintWriter writer = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(stdout)));
				FindFileSearcher fileSearcher = new FindFileSearcher(args[1],
						Environment.getCurrentDirectory(), writer);
				Files.walkFileTree(
						Paths.get(Environment.getCurrentDirectory()),
						fileSearcher);
				writer.flush();
			} catch (InvalidDirectoryException e) {
				throw new FindException(e);
			} catch (IOException e) {
				throw new FindException(e);
			}
		} else if (args.length == 3) {

			if (args[0] == null || args[1] == null || args[2] == null)
				throw new FindException(Consts.Messages.ARG_NOT_NULL);
			if (args[0].length() == 0 || args[1].length() == 0
					|| args[2].length() == 0)
				throw new FindException(Consts.Messages.ARG_NOT_EMPTY);

			if (!args[1].equals("-name")) {
				throw new FindException(Consts.Messages.INVALID_OPTION);
			}

			try {
				String pathArgument = args[0];
				PrintWriter writer = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(stdout)));
				String dirToSearchIn = Environment
						.checkIsDirectory(pathArgument);
				FindFileSearcher fileSearcher = new FindFileSearcher(args[2],
						dirToSearchIn, writer);
				Files.walkFileTree(Paths.get(dirToSearchIn), fileSearcher);
				writer.flush();
			} catch (IOException e) {
				throw new FindException(e);
			} catch (InvalidDirectoryException e) {
				throw new FindException(e);
			}
		} else {
			throw new FindException(Consts.Messages.TOO_MANY_ARGS);
		}

	}

}
