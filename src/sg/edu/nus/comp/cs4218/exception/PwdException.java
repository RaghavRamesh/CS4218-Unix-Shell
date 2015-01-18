package sg.edu.nus.comp.cs4218.exception;

public class PwdException extends AbstractApplicationException {
   public PwdException(String message) {
        super("pwd: " + message);
   }
}
