package sg.edu.nus.comp.cs4218.exception;


public class InvalidFileException extends AbstractApplicationException {
	private static final String CAT = "cat: ";

	public InvalidFileException(String message) {
        super(message);
   }
}