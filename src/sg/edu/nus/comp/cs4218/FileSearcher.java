package sg.edu.nus.comp.cs4218;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class FileSearcher extends SimpleFileVisitor<Path> {
	private final PathMatcher matcher;
	private final String rootDirectory;
	private final PrintWriter writer;

	public FileSearcher(String globPattern, String rootDir, PrintWriter pw) throws InvalidDirectoryException {
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);

		File rootDirAsFile = new File(rootDir);
		if (!rootDirAsFile.exists() || !rootDirAsFile.isDirectory()) {
			throw new InvalidDirectoryException(Consts.Messages.DIR_NOT_VALID);
		}

		rootDirectory = rootDir;
		writer = pw;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		Path pathName = file.getFileName();
		if (pathName != null && matcher.matches(pathName)) {
			writer.write(Environment.calculateRelativePath(rootDirectory, file.toAbsolutePath().toString()));
			writer.write(System.getProperty("line.separator"));
		}
		return FileVisitResult.CONTINUE;
	}

}
