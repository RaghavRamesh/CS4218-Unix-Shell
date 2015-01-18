package sg.edu.nus.comp.cs4218.exception;

public class EchoException extends AbstractApplicationException {
   public EchoException(String message) {
        super("echo: " + message);
   }
}