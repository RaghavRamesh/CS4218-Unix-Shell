package sg.edu.nus.comp.cs4218.exception;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WcException extends AbstractApplicationException {
	private static final String WCEXP = "wc: ";

	public WcException(String message) {
		super(WCEXP + message);
	}

	public WcException(FileNotFoundException exception) {
		super(WCEXP + exception.getMessage());
	}

	public WcException(IOException exception) {
		super(WCEXP + exception.getMessage());
	}

	public WcException(InvalidDirectoryException exception) {
		super(WCEXP + exception.getMessage());
	}

	public WcException(InvalidFileException exception) {
		super(WCEXP + exception.getMessage());
	}
}