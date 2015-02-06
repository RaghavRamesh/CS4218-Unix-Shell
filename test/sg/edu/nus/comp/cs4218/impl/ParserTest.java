package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.Parser;

public class ParserTest {
	@Test
	public void parseWithDoubleQuoteOnly() {
		List<String> tokens = Parser
				.parseCommandLine("I hate \"Java\" and \"S c a l a\"");
		List<String> expected = Arrays.asList("I", "hate", "\"Java\"", "and",
				"\"S c a l a\"");
		assertEquals(expected, tokens);
	}

	@Test
	public void parseWithSingleQuoteOnly() {
		List<String> tokens = Parser
				.parseCommandLine("I hate 'Java' and 'S c a l a'");
		List<String> expected = Arrays.asList("I", "hate", "'Java'", "and",
				"'S c a l a'");
		assertEquals(expected, tokens);
	}

	@Test
	public void parseWithBackQuoteOnly() {
		List<String> tokens = Parser
				.parseCommandLine("I hate `Java` and `S c a l a`");
		List<String> expected = Arrays.asList("I", "hate", "`Java`", "and",
				"`S c a l a`");
		assertEquals(expected, tokens);
	}
	
	@Test
	public void parseWithSemicolon() {
	  List<String> tokens = Parser
        .parseCommandLine("I hate Java;Scala and; Python");
    List<String> expected = Arrays.asList("I", "hate", "Java", ";", "Scala", "and", ";", "Python");
    assertEquals(expected, tokens);
	}

	@Test
	public void parseWithDoubleAndBackQuotes() {
		List<String> tokens = Parser
				.parseCommandLine("echo \"this is space: `echo \" \"`\"");
		List<String> expected = Arrays.asList("echo",
				"\"this is space: `echo \" \"`\"");
		assertEquals(expected, tokens);
	}

	@Test
	public void parseWithDoubleAndSingleQuotes() {
		List<String> tokens = Parser
				.parseCommandLine("echo \"this is space: 'echo ' ''\"");
		List<String> expected = Arrays.asList("echo",
				"\"this is space: 'echo ' ''\"");
		assertEquals(expected, tokens);
	}

	@Test
	public void parseWithBackAndSingleQuotes() {
		List<String> tokens = Parser
				.parseCommandLine("echo 'this is space: 'echo \" \"''");
		List<String> expected = Arrays.asList("echo",
				"'this is space: 'echo \" \"''");
		assertEquals(expected, tokens);
	}
}
