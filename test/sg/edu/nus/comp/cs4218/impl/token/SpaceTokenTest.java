package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class SpaceTokenTest {
  @Test
  public void testGetType() {
    String str = "  ";
    AbstractToken token = TokenFactory.getToken(str, 0);
    assertEquals(token.getType(), TokenType.SPACES);
  }
  
  @Test
  public void testAppend() {
    String str = "   a";
    SpaceToken token = new SpaceToken(str, 0);
    for (int i = 0; i < 2; i++) {
      assertEquals(true, token.appendNext());
    }
    assertEquals(token.getEnd(), 2);
  }
  
  @Test
  public void testAppendOutOfRange() {
    String str = " k";
    SpaceToken token = new SpaceToken(str, 0);
    assertEquals(false, token.appendNext());
    assertEquals(token.getEnd(), 0);
  }
  
  @Test
  public void testValue() {
    String str = "KFC  | ";
    SpaceToken token = new SpaceToken(str, 3);
    while (token.appendNext()) { }
    assertEquals("  ", token.value());
  }
}
