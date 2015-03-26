package sg.edu.nus.comp.cs4218.exception;

import java.io.IOException;

public class CdException extends AbstractApplicationException {
	public CdException(String message) {
		super("cd: " + message);
	}

	public CdException(InvalidDirectoryException exception) {
		super("cd: " + exception.getMessage());
	}

	public CdException(IOException exception) {
		super("cd: " + exception.getMessage());
	}
}
