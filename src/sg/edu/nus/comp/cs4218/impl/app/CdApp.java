package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.DirectoryHelpers;
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

		String folderName = args[0];
		// TODO : check for ..,/bin, ~ after consulting tutor

		String currentDirectory;
		try {
			currentDirectory = DirectoryHelpers.getCurrentDirectory();

			String requiredDirectory = currentDirectory + File.separator
					+ folderName;
			File reqdPathAsFile = new File(requiredDirectory);

			if (!reqdPathAsFile.exists()) {
				throw new CdException(Consts.Messages.PATH_NOT_FOUND);
			}

			if (reqdPathAsFile.isFile()) {
				throw new CdException(Consts.Messages.DIR_NOT_VALID);
			}

			System.setProperty(Consts.Keywords.USER_DIR, requiredDirectory);
		} catch (InvalidDirectoryException e) {

			throw new CdException(e);
		}

	}

}
