package sg.edu.nus.comp.cs4218.exception;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CatException extends AbstractApplicationException {
	public CatException(String message) {
		super("cat: " + message);
	}

	public CatException(InvalidDirectoryException exception) {
		super("cat: " + exception.getMessage());
	}

	public CatException(FileNotFoundException exception) {
		super("cat: " + exception.getMessage());
	}

	public CatException(IOException exception) {
		super("cat: " + exception.getMessage());
	}
}