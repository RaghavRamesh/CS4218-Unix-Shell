package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class OutputTokenTest {
  @Test
  public void testGetType() {
    String str = ">";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.getType(), TokenType.OUTPUT);
  }
  
  @Test
  public void testAppend() {
    String str = ">";
    OutputToken token = new OutputToken(str, 0);
    assertEquals(false, token.appendNext());
    assertEquals(false, token.appendNext());
    assertEquals(token.getEnd(), 0);
  }
}
