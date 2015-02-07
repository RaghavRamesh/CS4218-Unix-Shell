package sg.edu.nus.comp.cs4218.impl.app;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.DirectoryHelpers;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;
import sg.edu.nus.comp.cs4218.exception.PwdException;

public class PwdApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		if (stdout == null) {
			throw new PwdException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		String currentDirectory;
		try {
			currentDirectory = Environment.getCurrentDirectory();
			PrintWriter outPathWriter = new PrintWriter(stdout);
			outPathWriter.write(currentDirectory);
			outPathWriter.flush();
		} catch (InvalidDirectoryException e) {
			throw new PwdException(e);
		}

	}

}
