package sg.edu.nus.comp.cs4218.impl.token;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class SingleQuoteToken extends AbstractToken {

  protected SingleQuoteToken(String parent, int begin) {
    super(parent, begin);
    assert(parent.charAt(begin) == '\'');
  }

  @Override
  public Boolean appendNext() {
    if (end >= parent.length() - 1) {
      return false;
    }
    
    if (end > begin && parent.charAt(begin) == '\'' && parent.charAt(end) == '\'') {
      return false;
    } else {
      end++;
      return true;
    }
  }

  @Override
  public TokenType getType() {
    return TokenType.SINGLE_QUOTES;
  }

  @Override
  public String value() throws ShellException {
    checkValid();
    return parent.substring(begin + 1, end);
  }

  @Override
  public void checkValid() throws ShellException {
    if (end > begin 
        && parent.charAt(begin) == '\''
        && parent.charAt(end) == '\'') {
      return;
    }
    throw new ShellException(Consts.Messages.QUOTE_MISMATCH);
  }

}
