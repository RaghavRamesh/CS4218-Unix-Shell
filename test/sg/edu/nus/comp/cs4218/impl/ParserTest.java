package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ParserTest {
	private static final String SHOULD_NOT_THROW = "No exception should be thrown";

	@Test
	public void testParseWithDoubleQuoteOnly() {
		try {
			List<String> tokens = Parser
					.parseCommandLine("I hat \"Java\" and \"S c a l a\"");
			List<String> expected = Arrays.asList("I", "hate", "\"Java\"",
					"and", "\"S c a l a\"");
			assertEquals(expected, tokens);
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithSingleQuoteOnly() {
		try {
			List<String> tokens = Parser
					.parseCommandLine("I eat 'Apple' with 'Orange'");
			List<String> expected = Arrays.asList("I", "eat", "'Apple'", "with",
					"'Orange'");
			assertEquals(expected, tokens);
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithBackQuoteOnly() {
		try {
			List<String> tokens = Parser
					.parseCommandLine("I hate `Java` and `S c a l a`");
			List<String> expected = Arrays.asList("I", "hate", "`Java`", "and",
					"`S c a l a`");
			assertEquals(expected, tokens);
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithSemicolon() {
		try {
			List<String> tokens = Parser
					.parseCommandLine("I hate Java;Scala and; Python");
			List<String> expected = Arrays.asList("I", "hate", "Java", ";",
					"Scala", "and", ";", "Python");
			assertEquals(expected, tokens);
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithDoubleAndBackQuotes() {
		try {
			List<String> tokens = Parser
					.parseCommandLine("echo \"this is space: `echo \" \"`\"");
			List<String> expected = Arrays.asList("echo",
					"\"this is space: `echo \" \"`\"");
			assertEquals(expected, tokens);
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithDoubleAndSingleQuotes() {
		try {
			List<String> tokens = Parser
					.parseCommandLine("echo \"this is space: 'echo ' ''\"");
			List<String> expected = Arrays.asList("echo",
					"\"this is space: 'echo ' ''\"");
			assertEquals(expected, tokens);
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseWithBackAndSingleQuotes() {
		try {
			List<String> tokens = Parser
					.parseCommandLine("echo 'this is space: `echo \" \"`'");

			List<String> expected = Arrays.asList("echo",
					"'this is space: `echo \" \"`'");
			assertEquals(expected, tokens);
		} catch (Exception e) {
			fail(SHOULD_NOT_THROW);
		}
	}

	@Test
	public void testParseDoubleQuoteMistmatch() {
		try {
			Parser.parseCommandLine("echo \"a b c");
		} catch (ShellException e) {
			assert (e.getMessage().equals(Consts.Messages.QUOTE_MISMATCH));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testParseSingleQuoteMistmatch() {
		try {
			Parser.parseCommandLine("echo \" echo 'a b c");
		} catch (ShellException e) {
			assert (e.getMessage().equals(Consts.Messages.QUOTE_MISMATCH));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testParseBackQuoteMistmatch() {
		try {
			Parser.parseCommandLine("echo `echo ls");
		} catch (ShellException e) {
			assert (e.getMessage().equals(Consts.Messages.QUOTE_MISMATCH));
		} catch (Exception e) {
			fail();
		}
	}
}
