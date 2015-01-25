package sg.edu.nus.comp.cs4218.exception;

public class AppNotFoundException extends AbstractApplicationException{
  public AppNotFoundException(String message) {
    super(message);
  }
}
