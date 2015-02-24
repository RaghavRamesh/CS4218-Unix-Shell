package sg.edu.nus.comp.cs4218.exception;

import java.io.FileNotFoundException;
import java.io.IOException;

import sg.edu.nus.comp.cs4218.Consts;

public class HeadException extends AbstractApplicationException {
   private static final String HEAD = "head: ";

public HeadException(String message) {
        super(HEAD + message);
   }

public HeadException(FileNotFoundException exception) {
	super(HEAD + Consts.Messages.FILE_NOT_EXISTS);
}

public HeadException(NumberFormatException exception) {
	super(HEAD + Consts.Messages.ILLEGAL_LINE_CNT);
}

public HeadException(IOException exception) {
	super(HEAD + exception.getMessage());
}
}