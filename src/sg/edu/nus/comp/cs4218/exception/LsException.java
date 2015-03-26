package sg.edu.nus.comp.cs4218.exception;

import java.io.IOException;

public class LsException extends AbstractApplicationException {
	public LsException(String message) {
		super("ls: " + message);
	}

	public LsException(InvalidDirectoryException exception) {
		super("ls: " + exception.getMessage());
	}

	public LsException(IOException exception) {
		super("ls: " + exception.getMessage());
	}
}
