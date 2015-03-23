package sg.edu.nus.comp.cs4218.impl.token;

import sg.edu.nus.comp.cs4218.exception.ShellException;

public class SpaceToken extends AbstractToken {

  protected SpaceToken(String parent, int begin) {
    super(parent, begin);
    assert(parent.charAt(begin) == ' ');
  }

  @Override
  public Boolean appendNext() {
    if (end >= parent.length() - 1) {
      return false;
    }
    
    Character nextChar = parent.charAt(end + 1);
    if (nextChar == ' ') {
      end++;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public TokenType getType() {
    return TokenType.SPACES;
  }
  
  @Override
  public String value() {
    return parent.substring(begin, end + 1);
  }

  @Override
  public void checkValid() throws ShellException {
    // Do not need to check anything
  }

}
