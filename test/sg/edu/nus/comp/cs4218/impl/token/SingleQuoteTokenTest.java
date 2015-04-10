package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class SingleQuoteTokenTest {
	private static final String WRONG_EXCEPTION = "Wrong exception thrown";

	@Test
	public void testGetType() {
		String str = "' abc'";
		AbstractToken token = TokenFactory.getToken(str, 0);
		assertEquals(token.getType(), TokenType.SINGLE_QUOTES);
	}

	@Test
	public void testAppend() {
		String str = "a ' \" ` d' e";
		AbstractToken token = TokenFactory.getToken(str, 2);
		for (int i = 0; i < 6; i++) {
			assertEquals(token.appendNext(), true);
		}
		assertEquals(token.getEnd(), 8);
	}

	@Test
	public void testAppendAfterClosingQuote() {
		String str = "'' e";
		AbstractToken token = TokenFactory.getToken(str, 0);
		assertEquals(token.appendNext(), true);
		assertEquals(token.appendNext(), false);
		assertEquals(token.getEnd(), 1);
	}

	@Test
	public void testAppendOutOfRange() {
		String str = "'";
		AbstractToken token = TokenFactory.getToken(str, 0);
		assertEquals(token.appendNext(), false);
		assertEquals(token.getEnd(), 0);
	}

	@Test
	public void testValue() {
		String str = "'a'";
		SingleQuoteToken token = (SingleQuoteToken) TokenFactory.getToken(str,
				0);
		token.appendNext();
		token.appendNext();
		try {
			assertEquals("'a'", token.value());
		} catch (ShellException e) {
			fail(WRONG_EXCEPTION);
		}
	}
	
	 @Test(expected = ShellException.class)
	 public void testValueInvalid() throws ShellException {
	   String str = "'a";
	   SingleQuoteToken token = (SingleQuoteToken) TokenFactory.getToken(str, 0);
	   token.appendNext();
	   token.appendNext();
	   token.value();
	 }
}
