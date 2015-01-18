package sg.edu.nus.comp.cs4218.exception;

public class SedException extends AbstractApplicationException {
   public SedException(String message) {
        super("sed: " + message);
   }
}