package sg.edu.nus.comp.cs4218;

import java.io.File;

import sg.edu.nus.comp.cs4218.exception.FileCreateException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public final class DirectoryHelpers {

	private DirectoryHelpers() {
	}

	public static String getCurrentDirectory() throws InvalidDirectoryException {
		String currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
		File activePathAsFile = new File(currentDirectory);

		// check if current directory information is not corrupted one
		if (!activePathAsFile.exists()) {
			throw new InvalidDirectoryException(Consts.Messages.CURDIR_NOT_EXIST);
		}

		if (activePathAsFile.isFile()) {
			throw new InvalidDirectoryException(Consts.Messages.DIR_NOT_VALID);
		}

		return currentDirectory;
	}

	public static File[] getContentsInDirectory(String requiredDirectory) throws InvalidDirectoryException {
		File reqdDir = new File(requiredDirectory);

		if (!reqdDir.exists() || !reqdDir.isDirectory()) {
			throw new InvalidDirectoryException(Consts.Messages.DIR_NOT_VALID);
		}

		return reqdDir.listFiles();
	}

	public static File createFile(String relativePath) throws FileCreateException {
		try {
			String currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
			File file = new File(currentDirectory, relativePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			return file;
		} catch (Exception e) {
			throw new FileCreateException(Consts.Messages.CANNOT_CREATE_FILE + " " + relativePath);
		}
	}

	public static String calculateRelativePath(String base, String basePlusExtraPath) {
		if (base == null || basePlusExtraPath == null)
			return null;
		return new File(base).toURI().relativize(new File(basePlusExtraPath).toURI()).getPath();
	}

}