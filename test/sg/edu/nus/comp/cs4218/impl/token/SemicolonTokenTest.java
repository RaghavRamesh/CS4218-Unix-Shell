package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class SemicolonTokenTest {
  @Test
  public void testGetType() {
    String str = ";";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.getType(), TokenType.SEMICOLON);
  }
  
  @Test
  public void testAppend() {
    String str = ";";
    SemicolonToken token = new SemicolonToken(str, 0);
    assertEquals(false, token.appendNext());
    assertEquals(false, token.appendNext());
    assertEquals(token.getEnd(), 0);
  }
}
