package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.PwdException;

public class PwdApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		if (stdout == null) {
			throw new PwdException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		String currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
		File activePathAsFile = new File(currentDirectory);

		// check if current directory information is not corrupted one
		if (!activePathAsFile.exists()) {
			throw new PwdException(Consts.Messages.CURDIR_NOT_EXIST);
		}

		if (activePathAsFile.isFile()) {
			throw new PwdException(Consts.Messages.DIR_NOT_VALID);
		}

		if (activePathAsFile.isDirectory()) {
			PrintWriter outPathWriter = new PrintWriter(stdout);
			outPathWriter.write(currentDirectory);
			outPathWriter.flush();
		}
	}
}
