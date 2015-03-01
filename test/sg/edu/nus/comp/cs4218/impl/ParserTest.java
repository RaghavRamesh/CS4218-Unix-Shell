package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class ParserTest {
	private static final String SHOULD_NOT_THROW = "No exception should be thrown";

	@Test
	public void testTrimInput() {
	  try {
      List<AbstractToken> tokens = Parser.tokenize("   a b ");
      String expected = "[a,  , b]";
      assertEquals(expected, tokens.toString());
    } catch (Exception e) {
      fail(SHOULD_NOT_THROW);
    }
	}
	
	@Test
	public void testParseWithDoubleQuoteOnly() {
		try {
			List<AbstractToken> tokens = Parser.tokenize("a  \"b\"");
			String expected = "[a,   , \"b\"]";
			assertEquals(expected, tokens.toString());
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithSingleQuoteOnly() {
		try {
			List<AbstractToken> tokens = Parser.tokenize("'Apple' eat 'Orange'");
			String expected = "['Apple',  , eat,  , 'Orange']";
			assertEquals(expected, tokens.toString());
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithBackQuoteOnly() {
		try {
			List<AbstractToken> tokens = Parser.tokenize("I hate `Java` and `S c a l a`");
	    String expected = "[I,  , hate,  , `Java`,  , and,  , `S c a l a`]";
	    assertEquals(expected, tokens.toString());
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithSemicolon() {
		try {
			List<AbstractToken> tokens = Parser.tokenize("a ;b c; d");
			String expected = "[a,  , ;, b,  , c, ;,  , d]";
			assertEquals(expected, tokens.toString());
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithDoubleAndBackQuotes() {
		try {
			List<AbstractToken> tokens = Parser.tokenize("echo \"this is space: `echo \" \"`\"");
			String expected = "[echo,  , \"this is space: `echo \" \"`\"]";
			assertEquals(expected, tokens.toString());
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithDoubleAndSingleQuotes() {
		try {
			List<AbstractToken> tokens = Parser.tokenize("echo \"this is space: 'echo ' ''\"");
			String expected = "[echo,  , \"this is space: 'echo ' ''\"]";
			assertEquals(expected, tokens.toString());
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithBackAndSingleQuotes() {
		try {
			List<AbstractToken> tokens = Parser.tokenize("echo 'this is space: `echo \" \"`'");
			String expected = "[echo,  , 'this is space: `echo \" \"`']";
			assertEquals(expected, tokens.toString());
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}
}
