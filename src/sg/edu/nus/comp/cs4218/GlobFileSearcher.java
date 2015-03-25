package sg.edu.nus.comp.cs4218;

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
	private String dirPrefix;
	// private String
	private List<String> resultList;

	public List<String> getResultList() {
		if (resultList == null)
			resultList = new ArrayList<String>();
		return resultList;
	}

	public GlobFileSearcher(String globPattern, String dirPre) throws InvalidDirectoryException, IOException {
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
		dirPrefix = dirPre;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		// if it encounters the subdirectory being searched in
		if (dir.toString().endsWith(dirPrefix))
			return FileVisitResult.CONTINUE;

		try {
			String resultToAdd = Environment.calculateRelativePath(Environment.getCurrentDirectory(), dir.toAbsolutePath().toString());
			// removing the trailing File.Separator before adding to display results
			getResultList().add(resultToAdd.substring(0, resultToAdd.length() - 1)); // TODO: change to relative path of the sub directory
		} catch (InvalidDirectoryException e) {
			// TODO:
		}

		return FileVisitResult.SKIP_SUBTREE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		// if (writer == null)
		// return FileVisitResult.CONTINUE;

		Path pathName = file.getFileName();
		if (pathName != null && matcher.matches(pathName)) {
			try {
				String resultToAdd = Environment.calculateRelativePath(Environment.getCurrentDirectory(), file.toAbsolutePath().toString());
				getResultList().add(resultToAdd); // TODO: change to relative path of the sub directory
			} catch (InvalidDirectoryException e) {
				// TODO:
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		return FileVisitResult.CONTINUE;
	}

}
