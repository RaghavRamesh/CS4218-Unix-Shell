/**
 * 
 */
package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
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

			requiredDirectory = getRequiredDirectory(args);

			displayContentsInDirectory(stdout, requiredDirectory);
		} catch (InvalidDirectoryException e) {
			throw new LsException(e);
		} catch (IOException e) {
			throw new LsException(e);
		}
	}

	/**
	 * Parses the argument and returns the name of the directory that has to be
	 * displayed
	 * 
	 * @param args
	 * @return name of the directory that has to be displayed
	 * @throws LsException
	 * @throws InvalidDirectoryException
	 * @throws IOException
	 */
	private String getRequiredDirectory(String... args) throws LsException,
			InvalidDirectoryException, IOException {
		String requiredDirectory;
		if (args.length == 1 && args[0] != null) {
			if (args[0].length() == 0) {
				throw new LsException(Consts.Messages.ARG_NOT_EMPTY);
			}
			requiredDirectory = args[0];
		}

		else { // current directory if argument is not mentioned
			requiredDirectory = "";
		}
		return requiredDirectory;
	}

	/**
	 * Displays the contents of the directory
	 * 
	 * @param stdout
	 *            Output stream to which the contents have to be written to
	 * @param requiredDirectory
	 *            directory whose content has to be displayed
	 * @throws InvalidDirectoryException
	 */
	private void displayContentsInDirectory(OutputStream stdout,
			String requiredDirectory) throws InvalidDirectoryException {

		File[] currDirContents = Environment
				.getContentsInDirectory(requiredDirectory);
		PrintWriter outPathWriter = new PrintWriter(stdout);

		for (File fileInCurrDir : currDirContents) {
			String fileNameToDisplay = fileInCurrDir.getName();
			if (fileNameToDisplay != null && fileNameToDisplay.length() > 0
					&& !fileNameToDisplay.startsWith(".")) {

				outPathWriter.print(fileNameToDisplay);
				outPathWriter.print("\t");
			}
		}

		outPathWriter.println();
		outPathWriter.flush();
	}

}
