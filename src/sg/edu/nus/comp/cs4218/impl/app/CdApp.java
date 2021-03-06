package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class CdApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		if (args == null) {
			throw new CdException(Consts.Messages.ARG_NOT_NULL);
		}

		if (stdout == null) {
			throw new CdException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		if (args.length == 0) {
			throw new CdException(Consts.Messages.NO_DIR_ENTERED);
		}

		if (args.length > 1) {
			throw new CdException(Consts.Messages.EXPECT_ONE_ARG);
		}

		// Cd takes in only relative path directories
		String folderName = args[0];

		String currentDirectory;
		try {
			currentDirectory = Environment.getCurrentDirectory();

			String requiredDirectory = currentDirectory + File.separator
					+ folderName;

			Environment.setCurrentDirectory(requiredDirectory);
		} catch (InvalidDirectoryException e) {

			throw new CdException(e);
		} catch (IOException e) {
			throw new CdException(e);
		}

	}

}
