package sg.edu.nus.comp.cs4218.exception;

public class FindException extends AbstractApplicationException {
   public FindException(String message) {
        super("find: " + message);
   }
}