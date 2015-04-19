package coverage.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken;
import sg.edu.nus.comp.cs4218.impl.token.BackQuoteToken;
import sg.edu.nus.comp.cs4218.impl.token.DoubleQuoteToken;
import sg.edu.nus.comp.cs4218.impl.token.NormalToken;
import sg.edu.nus.comp.cs4218.impl.token.PipeToken;
import sg.edu.nus.comp.cs4218.impl.token.SemicolonToken;
import sg.edu.nus.comp.cs4218.impl.token.SingleQuoteToken;
import sg.edu.nus.comp.cs4218.impl.token.SpaceToken;
import sg.edu.nus.comp.cs4218.impl.token.TokenFactory;

public class TokenCoverageTest {
	private static final String NEWLINE = System.lineSeparator();

	@Test
	public void testPipeTokenValue() throws AbstractApplicationException,
			ShellException {
		String str = "|";
		AbstractToken token = TokenFactory.getToken(str, 0);
		assertEquals(token.value(), "|");
	}

	@Test
	public void testSemicolonTokenValue() throws AbstractApplicationException,
			ShellException {
		String str = ";";
		AbstractToken token = TokenFactory.getToken(str, 0);
		assertEquals(token.value(), ";");
	}

	@Test
	public void testCheckValid() throws ShellException {
		NormalToken tok1 = (NormalToken) TokenFactory.getToken("a", 0);
		tok1.checkValid();

		PipeToken tok2 = (PipeToken) TokenFactory.getToken("|", 0);
		tok2.checkValid();

		SemicolonToken tok3 = (SemicolonToken) TokenFactory.getToken(";", 0);
		tok3.checkValid();
	}

	@Test
	public void testChomp() {
		assertEquals("", DoubleQuoteToken.chomp(""));

		String input = "a" + NEWLINE + NEWLINE;
		String result = DoubleQuoteToken.chomp(input);
		assertEquals("a", result);

		input = NEWLINE + NEWLINE;
		result = DoubleQuoteToken.chomp(input);
		assertEquals("", result);
	}

	@Test
	public void testDoubleQuoteValueWithBackQuote() throws ShellException,
			AbstractApplicationException {
		String input = "\"`echo hi`\"";
		DoubleQuoteToken token = (DoubleQuoteToken) TokenFactory.getToken(
				input, 0);
		for (int i = 1; i < input.length(); i++) {
			token.appendNext();
		}
		assertEquals("\"hi\"", token.value());
	}

	@Test(expected = ShellException.class)
	public void testDoubleQuoteWrongFirstQuote() throws ShellException,
			AbstractApplicationException {
		DoubleQuoteToken token = new DoubleQuoteToken("'\"", 0, 1);
		token.checkValid();
	}

	@Test(expected = ShellException.class)
	public void testDoubleQuoteOnlyOneCharacter() throws ShellException,
			AbstractApplicationException {
		DoubleQuoteToken token = new DoubleQuoteToken("\"", 0, 0);
		token.checkValid();
	}

	@Test(expected = ShellException.class)
	public void testDoubleQuoteWrongSecondQuote() throws ShellException,
			AbstractApplicationException {
		DoubleQuoteToken token = new DoubleQuoteToken("\"'", 0, 1);
		token.checkValid();
	}

	@Test(expected = ShellException.class)
	public void testSingleQuoteWrongFirstQuote() throws ShellException,
			AbstractApplicationException {
		SingleQuoteToken token = new SingleQuoteToken("`'", 0, 1);
		token.checkValid();
	}

	@Test(expected = ShellException.class)
	public void testSingleQuoteOnlyOneCharacter() throws ShellException,
			AbstractApplicationException {
		SingleQuoteToken token = new SingleQuoteToken("'", 0, 0);
		token.checkValid();
	}

	@Test(expected = ShellException.class)
	public void testSingleQuoteWrongSecondQuote() throws ShellException,
			AbstractApplicationException {
		SingleQuoteToken token = new SingleQuoteToken("'a", 0, 1);
		token.checkValid();
	}

	@Test(expected = ShellException.class)
	public void testBackQuoteWrongFirstQuote() throws ShellException,
			AbstractApplicationException {
		BackQuoteToken token = new BackQuoteToken("b`", 0, 1);
		token.checkValid();
	}

	@Test(expected = ShellException.class)
	public void testBackQuoteOnlyOneCharacter() throws ShellException,
			AbstractApplicationException {
		BackQuoteToken token = new BackQuoteToken("`", 0, 0);
		token.checkValid();
	}

	@Test(expected = ShellException.class)
	public void testBackQuoteWrongSecondQuote() throws ShellException,
			AbstractApplicationException {
		BackQuoteToken token = new BackQuoteToken("`'", 0, 1);
		token.checkValid();
	}

	@Test
	public void testSpaceTokenAppendNoMore() {
		String str = " ";
		SpaceToken token = new SpaceToken(str, 0);
		assertEquals(false, token.appendNext());
	}

	@Test
	public void testNormalTokenAppendNoMore() {
		String str = "a";
		NormalToken token = new NormalToken(str, 0);
		assertEquals(false, token.appendNext());
	}
}
