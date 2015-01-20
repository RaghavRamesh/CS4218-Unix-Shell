package sg.edu.nus.comp.cs4218.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.QuoteParser;

public class QuoteParserTest {
  @Test
  public void parseWithDoubleQuoteOnly() {
    List<String> tokens = QuoteParser.parse("I hate \"Java\" and \"S c a l a\"");
    List<String> expected = Arrays.asList("I", "hate", "\"Java\"", "and", "\"S c a l a\"");
    assertEquals(tokens, expected);
   }
  
  @Test
  public void parseWithSingleQuoteOnly() {
    List<String> tokens = QuoteParser.parse("I hate 'Java' and 'S c a l a'");
    List<String> expected = Arrays.asList("I", "hate", "'Java'", "and", "'S c a l a'");
    assertEquals(tokens, expected);
   }
  
  @Test
  public void parseWithBackQuoteOnly() {
    List<String> tokens = QuoteParser.parse("I hate `Java` and `S c a l a`");
    List<String> expected = Arrays.asList("I", "hate", "`Java`", "and", "`S c a l a`");
    assertEquals(tokens, expected);
  }
  
  @Test
  public void parseWithMixQuotes() {
    List<String> tokens = QuoteParser.parse("echo \"this is space: `echo \" \"`\"");
    List<String> expected = Arrays.asList("echo", "\"this is space: `echo \" \"`\"");
    assertEquals(tokens, expected);
  }
}
