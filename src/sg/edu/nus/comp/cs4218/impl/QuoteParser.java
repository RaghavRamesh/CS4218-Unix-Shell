package sg.edu.nus.comp.cs4218.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// Utility class to parse a string input into tokens based on quotes.
// The quotes are not removed from the tokens.
public final class QuoteParser {
  static final Character WHITESPACE = ' ';
  static final Character DOUBLE_QUOTE = '"';
  static final Character SINGLE_QUOTE = '\'';
  static final Character BACK_QUOTE = '`';

  private QuoteParser() {
  }

  public static List<String> parse(String input) {
    String trimmedInput = input.trim();
    String currentToken = "";
    ArrayList<String> tokens = new ArrayList<String>();
    Stack<CharacterPosition> quoteStack = new Stack<CharacterPosition>();
    for (int i = 0; i < trimmedInput.length(); i++) {
      Character currentChar = trimmedInput.charAt(i);
      if (currentChar.equals(WHITESPACE) && quoteStack.isEmpty()) {
        addNonEmptyToList(tokens, currentToken);
        currentToken = "";
        continue;
      }
      currentToken += currentChar;
      if (isQuote(currentChar)) {
        if (quoteStack.isEmpty()
            || !quoteStack.peek().getCharacter().equals(currentChar)) {
          quoteStack.push(new CharacterPosition(currentChar, i));
        } else {
          quoteStack.pop();
          if (quoteStack.isEmpty()) {
            addNonEmptyToList(tokens, currentToken);
            currentToken = "";
          }
        }
      }
    }
    addNonEmptyToList(tokens, currentToken);
    return tokens;
  }
  
  public static Boolean isQuoted(String input) {
    int n = input.length();
    if (n < 2) {
      return false;
    }
    Boolean isDoubleQuoted = (input.charAt(0) == DOUBLE_QUOTE) && (input.charAt(n - 1) == DOUBLE_QUOTE);
    Boolean isSingleQuoted = (input.charAt(0) == SINGLE_QUOTE) && (input.charAt(n - 1) == SINGLE_QUOTE);
    Boolean isBackQuoted = (input.charAt(0) == BACK_QUOTE) && (input.charAt(n - 1) == BACK_QUOTE);
    return isDoubleQuoted || isSingleQuoted || isBackQuoted;
  }

  private static Boolean addNonEmptyToList(List<String> list, String str) {
    if (str.trim().equals("")) {
      return false;
    } else {
      return list.add(str);
    }
  }

  private static Boolean isQuote(Character character) {
    return character.equals(DOUBLE_QUOTE) || character.equals(SINGLE_QUOTE)
        || character.equals(BACK_QUOTE);
  }
}
