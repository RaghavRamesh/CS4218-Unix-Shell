package sg.edu.nus.comp.cs4218.impl.token;

import sg.edu.nus.comp.cs4218.exception.ShellException;

public class SemicolonToken extends AbstractToken {

  protected SemicolonToken(String parent, int begin) {
    super(parent, begin);
    assert(parent.charAt(begin) == ';');
  }

  @Override
  public Boolean appendNext() {
    return false;
  }

  @Override
  public TokenType getType() {
    return TokenType.SEMICOLON;
  }
 
  @Override
  public String value() {
    return String.valueOf(parent.charAt(begin));
  }

  @Override
  public void checkValid() throws ShellException {
    // Do not need to check anything
  }
  
}
