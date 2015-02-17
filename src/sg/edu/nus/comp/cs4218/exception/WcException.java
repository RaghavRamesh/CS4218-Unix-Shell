package sg.edu.nus.comp.cs4218.exception;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WcException extends AbstractApplicationException {
	public WcException(String message) {
		super("wc: " + message);
	}

	public WcException(FileNotFoundException exception) {
		super("wc: " + exception.getMessage());
	}

	public WcException(IOException exception) {
		super("wc: " + exception.getMessage());
	}

	public WcException(InvalidDirectoryException exception) {
		super("wc: " + exception.getMessage());
	}
}