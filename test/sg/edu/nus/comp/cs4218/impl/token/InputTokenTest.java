package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class InputTokenTest {
	@Test
	public void testGetType() {
		String str = "<";
		AbstractToken token = TokenFactory.getToken(str, 0);
		assertEquals(token.getType(), TokenType.INPUT);
	}

	@Test
	public void testAppend() {
		String str = "<";
		InputToken token = new InputToken(str, 0);
		assertEquals(false, token.appendNext());
		assertEquals(false, token.appendNext());
		assertEquals(token.getEnd(), 0);
	}
}
