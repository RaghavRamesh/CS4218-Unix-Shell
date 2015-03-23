package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class BackQuoteTokenTest {
  
  @Test
  public void testGetType() {
    String str = "`echo hello`";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.getType(), TokenType.BACK_QUOTES);
  }
  
  @Test
  public void testAppend() {
    String str = "echo `echo good`";
    BackQuoteToken token = new BackQuoteToken(str, 5);
    for (int i = 0; i < 10; i++) {
      assertEquals(true, token.appendNext());
    }
    assertEquals(token.getEnd(), 15);
  }
  
  @Test
  public void testAppendAfterClosingQuote() {
    String str = "`` e";
    BackQuoteToken token = new BackQuoteToken(str, 0);
    assertEquals(true, token.appendNext());
    assertEquals(false, token.appendNext());
    assertEquals(1, token.getEnd());
  }
  
  @Test
  public void testAppendOutOfRange() {
    String str = "`";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(false, token.appendNext());
    assertEquals(0, token.getEnd());
  }
}
