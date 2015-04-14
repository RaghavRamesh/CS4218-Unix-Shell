package coverage.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken;
import sg.edu.nus.comp.cs4218.impl.token.TokenFactory;

public class TokenCoverageTest {
  // Test for tokens
  @Test
  public void testPipeTokenValue() throws AbstractApplicationException,
      ShellException {
      String str = "|";
      AbstractToken token = TokenFactory.getToken(str, 0);
      assertEquals(token.value(), "|");
  }
  
  @Test
  public void testSemicolonTokenValue() throws AbstractApplicationException,
      ShellException {
      String str = ";";
      AbstractToken token = TokenFactory.getToken(str, 0);
      assertEquals(token.value(), ";");
  }
}
