package sg.edu.nus.comp.cs4218.exception;

public class HeadException extends AbstractApplicationException {
   public HeadException(String message) {
        super("head: " + message);
   }
}