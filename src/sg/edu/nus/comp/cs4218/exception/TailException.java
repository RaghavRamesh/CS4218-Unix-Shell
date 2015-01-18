package sg.edu.nus.comp.cs4218.exception;

public class TailException extends AbstractApplicationException {
   public TailException(String message) {
        super("tail: " + message);
   }
}