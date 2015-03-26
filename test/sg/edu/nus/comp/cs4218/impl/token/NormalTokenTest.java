package sg.edu.nus.comp.cs4218.impl.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class NormalTokenTest {
	@Test
	public void testGetType() {
		String str = "apple";
		AbstractToken token = TokenFactory.getToken(str, 0);
		assertEquals(token.getType(), TokenType.NORMAL);
	}

	@Test
	public void testAppend() {
		String str = "apple";
		NormalToken token = new NormalToken(str, 0);
		for (int i = 0; i < 4; i++) {
			assertEquals(true, token.appendNext());
		}
		assertEquals(token.getEnd(), 4);
	}

	@Test
	public void testAppendOutOfRange() {
		String str = "k ";
		NormalToken token = new NormalToken(str, 0);
		assertEquals(false, token.appendNext());
		assertEquals(token.getEnd(), 0);
	}

	@Test
	public void testValue() {
		String str = "KFC | ";
		NormalToken token = new NormalToken(str, 0);
		while (token.appendNext()) {
		}
		assertEquals("KFC", token.value());
	}
}
