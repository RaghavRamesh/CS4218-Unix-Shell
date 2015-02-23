package sg.edu.nus.comp.cs4218.exception;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CatException extends AbstractApplicationException {
	private static final String CAT = "cat: ";

	public CatException(String message) {
		super(CAT + message);
	}

	public CatException(InvalidDirectoryException exception) {
		super(CAT + exception.getMessage());
	}

	public CatException(FileNotFoundException exception) {
		super(CAT + exception.getMessage());
	}

	public CatException(IOException exception) {
		super(CAT + exception.getMessage());
	}

	public CatException(InvalidFileException exception) {
		super(CAT + exception.getMessage());
	}
}