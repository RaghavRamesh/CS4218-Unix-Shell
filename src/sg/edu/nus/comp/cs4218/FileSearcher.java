package sg.edu.nus.comp.cs4218;

import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class FileSearcher extends SimpleFileVisitor<Path> {
	private final PathMatcher matcher;
	private ArrayList<String> filePaths;

	public FileSearcher(String globPattern) {
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
		filePaths = new ArrayList<String>();
	}

	public String[] getFilePaths() {
		return (String[]) filePaths.toArray();
	}

	public int getNumOfFiles() {
		return filePaths.size();
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		Path pathName = file.getFileName();
		if (pathName != null && matcher.matches(pathName)) {
			filePaths.add(pathName.toString());
		}
		return FileVisitResult.CONTINUE;
	}

}
