package sg.edu.nus.comp.cs4218.impl.token;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public final class TokenFactory {
  private TokenFactory() {
  }
  
  public static AbstractToken getToken(String parent, int begin) {
    Character firstChar = parent.charAt(begin);
    TokenType type = AbstractToken.getType(firstChar);
    switch (type) {
    case SPACES:
      return new SpaceToken(parent, begin);
    case SEMICOLON:
      return new SemicolonToken(parent, begin);
    case PIPE:
      return new PipeToken(parent, begin);
    case INPUT:
      return new InputToken(parent, begin);
    case OUTPUT:
      return new OutputToken(parent, begin);
    case SINGLE_QUOTES:
      return new SingleQuoteToken(parent, begin);
    case DOUBLE_QUOTES:
      return new DoubleQuoteToken(parent, begin);
    case BACK_QUOTES:
      return new BackQuoteToken(parent, begin);
    case NORMAL:
      return new NormalToken(parent, begin);
    default:
      return null;
    }
  }
}
