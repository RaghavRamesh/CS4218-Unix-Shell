package sg.edu.nus.comp.cs4218.exception;

public class WcException extends AbstractApplicationException {
   public WcException(String message) {
        super("wc: " + message);
   }
}