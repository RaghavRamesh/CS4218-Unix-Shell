package sg.edu.nus.comp.cs4218;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class GlobFileSearcher extends SimpleFileVisitor<Path> {
	private final PathMatcher matcher;
	private List<String> resultList;
	private String rootDirectory;
	private int numOfDirsTouched = 0;

	public List<String> getResultList() {
		if (resultList == null)
			resultList = new ArrayList<String>();
		return resultList;
	}

	public GlobFileSearcher(String globPattern) throws InvalidDirectoryException, IOException {
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
		rootDirectory = Environment.getCurrentDirectory();
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		
		if (++numOfDirsTouched == 1) // the first dir touched, the one searching in
			return FileVisitResult.CONTINUE;

		Path pathName = dir.getFileName();
		if (!new File(pathName.toString()).isHidden() && matcher.matches(pathName)) {
			String resultToAdd = Environment.calculateRelativePath(rootDirectory, dir.toAbsolutePath().toString());
			// removing the trailing File.Separator at the directory end before adding to display results
			getResultList().add(resultToAdd.substring(0, resultToAdd.length() - 1));
		}
		return FileVisitResult.SKIP_SUBTREE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		Path pathName = file.getFileName();
		if (!new File(pathName.toString()).isHidden() && matcher.matches(pathName)) {
			String resultToAdd = Environment.calculateRelativePath(rootDirectory, file.toAbsolutePath().toString());
			getResultList().add(resultToAdd);
		}
		return FileVisitResult.CONTINUE;
	}
}
