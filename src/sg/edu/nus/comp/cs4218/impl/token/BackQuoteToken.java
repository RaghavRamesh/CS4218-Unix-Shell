package sg.edu.nus.comp.cs4218.impl.token;

import java.io.ByteArrayOutputStream;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;

public class BackQuoteToken extends AbstractToken {

  protected BackQuoteToken(String parent, int begin) {
    super(parent, begin);
    assert(parent.charAt(begin) == '`');
  }

  @Override
  public Boolean appendNext() {
    if (end >= parent.length() - 1) {
      return false;
    }
    
    if (end > begin && parent.charAt(begin) == '`' && parent.charAt(end) == '`') {
      return false;
    } else {
      end++;
      return true;
    }
  }

  @Override
  public TokenType getType() {
    return TokenType.BACK_QUOTES;
  }

  @Override
  public String value() throws ShellException, AbstractApplicationException {
    checkValid();
    
    String cmd = parent.substring(begin + 1, end);
    Command command = ShellImplementation.getCommand(cmd);
    ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
    command.evaluate(null, byteOutStream);
    return byteOutStream.toString();
  }

  @Override
  public void checkValid() throws ShellException {
    if (end > begin 
        && parent.charAt(begin) == '`'
        && parent.charAt(end) == '`') {
      return;
    }
    throw new ShellException(Consts.Messages.QUOTE_MISMATCH);
  }
}
