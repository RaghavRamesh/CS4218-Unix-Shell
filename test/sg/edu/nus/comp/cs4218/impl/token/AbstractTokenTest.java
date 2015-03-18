package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class AbstractTokenTest {
  @Test
  public void testGetType() {
    assertEquals(TokenType.SPACES, AbstractToken.getType(' '));
    assertEquals(TokenType.INPUT, AbstractToken.getType('<'));
    assertEquals(TokenType.OUTPUT, AbstractToken.getType('>'));
    assertEquals(TokenType.SEMICOLON, AbstractToken.getType(';'));
    assertEquals(TokenType.PIPE, AbstractToken.getType('|'));
    assertEquals(TokenType.DOUBLE_QUOTES, AbstractToken.getType('"'));
    assertEquals(TokenType.SINGLE_QUOTES, AbstractToken.getType('\''));
    assertEquals(TokenType.BACK_QUOTES, AbstractToken.getType('`'));
    assertEquals(TokenType.NORMAL, AbstractToken.getType('a'));
  }
}
