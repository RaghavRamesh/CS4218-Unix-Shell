package sg.edu.nus.comp.cs4218.exception;

public class GrepException extends AbstractApplicationException {
	public GrepException(String message) {
		super("grep: " + message);
	}

	public GrepException(Exception exception) {
		super("grep: " + exception.getMessage());
	}
}