package sg.edu.nus.comp.cs4218.exception;


public class ShellException extends Exception {
	public ShellException(String message) {
		super("shell: " + message);
	}

	public ShellException(Exception exception) {
		super("shell: " + exception.getMessage());
	}
}