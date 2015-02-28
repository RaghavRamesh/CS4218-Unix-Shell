package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class SingleQuoteTokenTest {
  
  @Test
  public void testGetType() {
    String str = "' abc'";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.getType(), TokenType.SINGLE_QUOTES);
  }
  
  @Test
  public void testAppend() {
    String str = "a ' \" ` d' e";
    AbstractToken token = TokenFactory.getToken(str, 2);
    for (int i = 0; i < 6; i++) {
      assertEquals(token.appendNext(), true);
    }
    assertEquals(token.getEnd(), 8);
  }
  
  @Test
  public void testAppendAfterClosingQuote() {
    String str = "'' e";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.appendNext(), true);
    assertEquals(token.appendNext(), false);
    assertEquals(token.getEnd(), 1);
  }
  
  @Test
  public void testAppendOutOfRange() {
    String str = "'";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.appendNext(), false);
    assertEquals(token.getEnd(), 0);
  }
  
}
