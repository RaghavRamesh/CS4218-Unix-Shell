package sg.edu.nus.comp.cs4218.exception;

import java.io.FileNotFoundException;
import java.io.IOException;

import sg.edu.nus.comp.cs4218.Consts;

public class TailException extends AbstractApplicationException {
	private static final String TAIL = "tail: ";

	public TailException(String message) {
		super(TAIL + message);
	}

	public TailException(FileNotFoundException exception) {
		super(TAIL + Consts.Messages.FILE_NOT_EXISTS);
	}

	public TailException(NumberFormatException exception) {
		super(TAIL + Consts.Messages.ILLEGAL_LINE_CNT);
	}

	public TailException(IOException exception) {
		super(TAIL + exception.getMessage());
	}

	public TailException(InvalidFileException exception) {
		super(TAIL + exception.getMessage());
	}

	public TailException(Exception exception) {
		super(TAIL + exception.getMessage());
	}

}