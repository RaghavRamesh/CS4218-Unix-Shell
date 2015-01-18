package sg.edu.nus.comp.cs4218.exception;

public class LsException extends AbstractApplicationException {
   public LsException(String message) {
        super("ls: " + message);
   }
}
