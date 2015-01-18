package sg.edu.nus.comp.cs4218.exception;

public class CatException extends AbstractApplicationException {
   public CatException(String message) {
        super("cat: " + message);
   }
}