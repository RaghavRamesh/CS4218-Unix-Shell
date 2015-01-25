/**
 * 
 */
package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.DirectoryHelpers;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;
import sg.edu.nus.comp.cs4218.exception.LsException;

public class LsApp implements Application {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException {

		if (args == null) {
			throw new LsException(Consts.Messages.ARG_NOT_NULL);
		}

		if (stdout == null) {
			throw new LsException(Consts.Messages.OUT_STR_NOT_NULL);
		}

		if (args.length > 1) {
			throw new LsException(Consts.Messages.EXPECT_ONE_ARG);
		}

		String requiredDirectory;
		try {

			if (args.length == 1 && args[0] != null) {
				assert (args[0].length() > 0);
				requiredDirectory = args[0];
			}

			else { // current directory if argument is not mentioned
				requiredDirectory = DirectoryHelpers.getCurrentDirectory();
			}

			displayContentsInDirectory(stdout, requiredDirectory);
		} catch (InvalidDirectoryException e) {
			throw new LsException(e);
		}
	}

	private void displayContentsInDirectory(OutputStream stdout,
			String requiredDirectory) throws InvalidDirectoryException {

		File[] currDirContents = DirectoryHelpers
				.getContentsInDirectory(requiredDirectory);
		PrintWriter outPathWriter = new PrintWriter(stdout);

		for (File fileInCurrDir : currDirContents) {
			String fileNameToDisplay = fileInCurrDir.getName();
			if (fileNameToDisplay != null && fileNameToDisplay.length() > 0
					&& !fileNameToDisplay.startsWith(".")) {

				outPathWriter.println(fileNameToDisplay);
				outPathWriter.println("\t");
				// TODO: check if this is right way of printing
			}
		}

		outPathWriter.flush();
	}

}
