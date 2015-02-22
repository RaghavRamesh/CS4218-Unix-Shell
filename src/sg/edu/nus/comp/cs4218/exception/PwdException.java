package sg.edu.nus.comp.cs4218.exception;

import java.io.IOException;

public class PwdException extends AbstractApplicationException {
   public PwdException(String message) {
        super("pwd: " + message);
   }
   
   public PwdException(InvalidDirectoryException exception) {
		super("pwd: " + exception.getMessage());
	}

	public PwdException(IOException exception) {
		super("pwd: " + exception.getMessage());
	}

}

