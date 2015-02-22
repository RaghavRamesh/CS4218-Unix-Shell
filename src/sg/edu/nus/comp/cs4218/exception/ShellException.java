package sg.edu.nus.comp.cs4218.exception;

import java.io.IOException;

public class ShellException extends Exception {
	public ShellException(String message) {
		super("shell: " + message);
	}

	public ShellException(IOException e) {
		super("shell: " + e.getMessage());
	}
}