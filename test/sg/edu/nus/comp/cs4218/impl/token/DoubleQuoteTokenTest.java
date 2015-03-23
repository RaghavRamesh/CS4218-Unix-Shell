package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class DoubleQuoteTokenTest {
  private static final String WRONG_EXCEPTION = "Wrong exception thrown";
  private static final String SHOULD_NOT_THROWN = "No exception should be thrown";
  
  @Test
  public void testGetType() {
    String str = "\" abc\"";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.getType(), TokenType.DOUBLE_QUOTES);
  }
  
  @Test
  public void testAppend() {
    String str = "a \" ' d |\" e";
    DoubleQuoteToken token = new DoubleQuoteToken(str, 2);
    for (int i = 0; i < 6; i++) {
      assertEquals(token.appendNext(), true);
    }
    assertEquals(token.getEnd(), 8);
  }
  
  @Test
  public void testAppendAfterClosingQuote() {
    String str = "\"\" k";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.appendNext(), true);
    assertEquals(token.appendNext(), false);
    assertEquals(token.getEnd(), 1);
  }
  
  @Test
  public void testAppendOutOfRange() {
    String str = "\"";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.appendNext(), false);
    assertEquals(token.getEnd(), 0);
  }
 
  @Test
  public void testValue() {
    String str = "\" abc \"";
    DoubleQuoteToken token = (DoubleQuoteToken) TokenFactory.getToken(str, 0);
    for (int i = 0; i < 6; i++) {
      token.appendNext();
    }
    try {
      assertEquals(" abc ", token.value());
    } catch (Exception e) {
      fail(SHOULD_NOT_THROWN);
    }
  }
}
