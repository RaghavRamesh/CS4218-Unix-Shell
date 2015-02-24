package sg.edu.nus.comp.cs4218;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class FileSearcher extends SimpleFileVisitor<Path> {
	private final PathMatcher matcher;
	private ArrayList<String> filePaths;
	private String rootDirectory;

	public FileSearcher(String globPattern, String rootDir) throws InvalidDirectoryException {
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);

		File rootDirAsFile = new File(rootDir);
		if (!rootDirAsFile.exists() || !rootDirAsFile.isDirectory()) {
			throw new InvalidDirectoryException(Consts.Messages.DIR_NOT_VALID);
		}

		rootDirectory = rootDir;
		filePaths = new ArrayList<String>();
	}

	public String[] getFilePaths() {
		return (String[]) filePaths.toArray(new String[filePaths.size()]);
	}

	public int getNumOfFiles() {
		return filePaths.size();
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		Path pathName = file.getFileName();
		if (pathName != null && matcher.matches(pathName)) {
			filePaths.add(DirectoryHelpers.calculateRelativePath(rootDirectory, file.toAbsolutePath().toString()));
		}
		return FileVisitResult.CONTINUE;
	}

}
