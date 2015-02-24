package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.DirectoryHelpers;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.FileSearcher;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.FindException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class FindApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if (args == null) {
			throw new FindException(Consts.Messages.ARG_NOT_NULL);
		}

		if (stdout == null) {
			throw new FindException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		if (args.length == 0) {
			throw new FindException(Consts.Messages.NO_INPUT_FILE_OR_STDIN);
		} else if (args.length == 1) {
			try {
				FileSearcher fileSearcher = new FileSearcher(args[0], DirectoryHelpers.getCurrentDirectory());
				Files.walkFileTree(Paths.get(DirectoryHelpers.getCurrentDirectory()), fileSearcher);
				PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stdout)));
				writeStringArrToPrintStream(writer, fileSearcher.getFilePaths());
			} catch (InvalidDirectoryException e) {
				throw new FindException(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args.length == 2) {
			try {
				String pathArgument = args[0];
				String dirToSearchIn = Environment.checkIsDirectory(pathArgument);
			
				FileSearcher fileSearcher = new FileSearcher(args[1], dirToSearchIn);
				Files.walkFileTree(Paths.get(dirToSearchIn), fileSearcher);
				PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stdout)));
				writeStringArrToPrintStream(writer, fileSearcher.getFilePaths());
			} catch (IOException | InvalidDirectoryException e) {
				throw new FindException(e.getMessage());
			}
		} else {
			throw new FindException(Consts.Messages.TOO_MANY_ARGUMENTS);
		}

	}

	private void writeStringArrToPrintStream(PrintWriter writer, String[] stringsArr) {
		for (int i = 0; i < stringsArr.length; i++) {
			writer.write(stringsArr[i]);
			writer.write("\n");
		}
		writer.close();
	}
}
