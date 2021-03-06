package sg.edu.nus.comp.cs4218;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import sg.edu.nus.comp.cs4218.exception.FileCreateException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;
import sg.edu.nus.comp.cs4218.exception.InvalidFileException;

public final class Environment {

	/**
	 * Java VM does not support changing the current working directory. For this
	 * reason, we use Environment.currentDirectory instead.
	 */
	public static volatile String currentDirectory = System
			.getProperty("user.dir");

	private Environment() {
	};

	public static String getCurrentDirectory()
			throws InvalidDirectoryException, IOException {

		// check if current directory information is not corrupted one
		checkIsDirectory(currentDirectory);
		return currentDirectory;
	}

	public static void setCurrentDirectory(String directoryToChange)
			throws InvalidDirectoryException, IOException {

		currentDirectory = checkIsDirectory(directoryToChange);
	}

	/**
	 * 
	 * @param directoryToCheck
	 *            : absolute path/relative path of directory to check
	 * @return the canonical path of the directory
	 * @throws InvalidDirectoryException
	 * @throws IOException
	 */
	public static String checkIsDirectory(String directoryToCheck)
			throws InvalidDirectoryException, IOException {
		File reqdPathAsFile = new File(directoryToCheck);

		if (!reqdPathAsFile.isAbsolute()) {
			reqdPathAsFile = new File(Environment.currentDirectory,
					directoryToCheck);
		}

		if (!reqdPathAsFile.exists()) {
			throw new InvalidDirectoryException(Consts.Messages.PATH_NOT_FOUND);
		}

		if (reqdPathAsFile.isFile()) {
			throw new InvalidDirectoryException(Consts.Messages.DIR_NOT_VALID);
		}

		return reqdPathAsFile.getCanonicalPath();
	}

	/**
	 * 
	 * @param fileName
	 *            : relative or absolute filename
	 * @return the canonical path of the file
	 * @throws InvalidFileException
	 * @throws IOException
	 */
	public static String checkIsFile(String fileName)
			throws InvalidFileException, IOException {

		File reqdPathAsFile;

		reqdPathAsFile = new File(fileName);

		if (!reqdPathAsFile.isAbsolute()) {
			reqdPathAsFile = new File(Environment.currentDirectory, fileName);
		}

		boolean pathExists = reqdPathAsFile.exists();
		boolean pathIsFile = reqdPathAsFile.isFile();

		if (!pathExists) {
			throw new InvalidFileException("can't open '" + fileName + "'. "
					+ Consts.Messages.FILE_NOT_FOUND);
		}

		if (!pathIsFile) {
			throw new InvalidFileException("can't open '" + fileName + "'. "
					+ Consts.Messages.FILE_NOT_VALID);
		}

		return reqdPathAsFile.getCanonicalPath();
	}

	/**
	 * Gets the list of files in the directory
	 * 
	 * @param requiredDirectory
	 *            Required directory as relative or absolute path
	 * @return list of files in the directory
	 * @throws InvalidDirectoryException
	 */
	public static File[] getContentsInDirectory(String requiredDirectory)
			throws InvalidDirectoryException {
		File reqdDir = new File(requiredDirectory);

		if (!reqdDir.isAbsolute()) {
			reqdDir = new File(Environment.currentDirectory, requiredDirectory);
		}

		if (!reqdDir.exists() || !reqdDir.isDirectory()) {
			throw new InvalidDirectoryException(Consts.Messages.DIR_NOT_VALID);
		}

		return reqdDir.listFiles();
	}

	public static void deleteFolder(File folder) throws IOException {
		if (folder.isDirectory()) {
			for (File c : folder.listFiles())
				deleteFolder(c);
		}
		if (!folder.delete())
			throw new FileNotFoundException("Failed to delete file: " + folder);
	}

	public static File createFile(String relativePath)
			throws FileCreateException {
		try {
			String currentDirectory = getCurrentDirectory();
			File file = new File(currentDirectory, relativePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			return file;
		} catch (Exception e) {
			throw new FileCreateException(Consts.Messages.CANNOT_CRT_FILE + " "
					+ relativePath);
		}
	}

	public static String calculateRelativePath(String base,
			String basePlusExtraPath) {
		if (base == null || basePlusExtraPath == null)
			return null;
		return new File(base).toURI()
				.relativize(new File(basePlusExtraPath).toURI()).getPath();
	}
}
