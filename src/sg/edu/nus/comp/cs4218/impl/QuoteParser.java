package sg.edu.nus.comp.cs4218.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// Utility class to parse a string input into tokens based on quotes.
// The quotes are not removed from the tokens.
public class QuoteParser {
  static Character WHITESPACE = ' ';
  static Character DOUBLE_QUOTE = '"';
  static Character SINGLE_QUOTE = '\'';
  static Character BACK_QUOTE = '`';
  
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
        if (quoteStack.isEmpty() || !quoteStack.peek().getCharacter().equals(currentChar)) {
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
  
  private static Boolean addNonEmptyToList(List<String> list, String str) {
    if (str.trim().equals("")) {
      return false;
    } else {
      return list.add(str);
    }
  }
  
  private static Boolean isQuote(Character character) {
    return character.equals(DOUBLE_QUOTE) 
          || character.equals(SINGLE_QUOTE) 
          || character.equals(BACK_QUOTE);
  }
}
