package sg.edu.nus.comp.cs4218.exception;

import java.io.IOException;

public class FindException extends AbstractApplicationException {
	public FindException(String message) {
		super("find: " + message);
	}

	public FindException(InvalidDirectoryException exception) {
		super("find: " + exception.getMessage());
	}

	public FindException(IOException exception) {
		super("find: " + exception.getMessage());
	}
}