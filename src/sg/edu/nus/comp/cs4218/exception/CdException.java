package sg.edu.nus.comp.cs4218.exception;

public class CdException extends AbstractApplicationException {
   public CdException(String message) {
        super("cd: " + message);
   }
}
