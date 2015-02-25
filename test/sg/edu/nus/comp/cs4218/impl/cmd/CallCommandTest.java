package sg.edu.nus.comp.cs4218.impl.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class CallCommandTest {
	private static final String WRONG_EXCEPTION = "Wrong exception thrown";
	private static final String SHELL_STR = "shell: ";
	private static final String SHOULD_THROW = "Exception should be thrown";

	@Before
	public void setUp() {
		// In case of creating new files, need to clean and setup again
	}

	@After
	public void tearDown() {
		// In case of creating new files, need to clean and setup again
	}

	@Test
	public void testCallCommandWithTooManyOutputRedirection() {
		String cmdLine = "pwd test > a.txt >b.txt";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail(SHOULD_THROW);
		} catch (ShellException e) {
			String expected = SHELL_STR + Consts.Messages.TOO_MANY_OUTPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail(WRONG_EXCEPTION);
		}
	}

	@Test
	public void testCallCommandWithNoOutputRedirectionProvided() {
		String cmdLine = "pwd test >";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail(SHOULD_THROW);
		} catch (ShellException e) {
			String expected = SHELL_STR + Consts.Messages.INVALID_OUTPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail(WRONG_EXCEPTION);
		}
	}

	@Test
	public void testCallCommandWithInvalidOutputRedirectionProvided() {
		String cmdLine = "pwd test > < a.txt";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail(SHOULD_THROW);
		} catch (ShellException e) {
			String expected = SHELL_STR + Consts.Messages.INVALID_OUTPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail(WRONG_EXCEPTION);
		}
	}

	@Test
	public void testCallCommandWithTooManyInputRedirection() {
		String cmdLine = "pwd test < a.txt <b.txt";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail(SHOULD_THROW);
		} catch (ShellException e) {
			String expected = SHELL_STR + Consts.Messages.TOO_MANY_INPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail(WRONG_EXCEPTION);
		}
	}

	@Test
	public void testCallCommandWithNoInputRedirectionProvided() {
		String cmdLine = "pwd test <";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail(SHOULD_THROW);
		} catch (ShellException e) {
			String expected = SHELL_STR + Consts.Messages.INVALID_INPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail(WRONG_EXCEPTION);
		}
	}

	@Test
	public void testCallCommandWithInvalidInputRedirectionProvided() {
		String cmdLine = "pwd test < <";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail(SHOULD_THROW);
		} catch (ShellException e) {
			String expected = SHELL_STR + Consts.Messages.INVALID_INPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail(WRONG_EXCEPTION);
		}
	}

	@Test
	public void testSubstituteWithDoubleQuoteAndBackQuote()
			throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo \"this is space: `echo \" \"`\"";
		assertEquals("echo this is space:", CallCommand.substitute(cmdLine));
	}

	@Test
	public void testSubstituteWithSingleQuoteAndBackQuote()
			throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo 'hello `echo world`'";
		assertEquals("echo 'hello `echo world`'",
				CallCommand.substitute(cmdLine));
	}

	@Test
	public void testSubstituteWithAllQuotes()
			throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo \"echo 'echo `echo hello world` world' world\"";
		assertEquals("echo echo 'echo `echo hello world` world' world",
				CallCommand.substitute(cmdLine));
	}

	@Test
	public void testSubstituteWithMultipleEchoStatements()
			throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo 'echo hello `echo world`'";
		assertEquals("echo 'echo hello `echo world`'",
				CallCommand.substitute(cmdLine));
	}

	@Test
	public void testSubstituteWithOnlyBackQuote()
			throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo `echo hello`";
		assertEquals("echo hello", CallCommand.substitute(cmdLine));
	}

	@Test
	public void testSubstituteWithSequenceCommand()
			throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo `echo hello; echo world`";
		assertEquals("echo hello\nworld", CallCommand.substitute(cmdLine));
	}

	@Test
	public void testSubstituteWithBackQuoteEvaluatingToNull()
			throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo hello``";
		assertEquals("echo hello", CallCommand.substitute(cmdLine));
	}

	@Test
	public void testFindInput() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "echo hello < a.txt";
		assertEquals("a.txt", new CallCommand(cmdLine).findInput());
	}

	@Test
	public void testFindOutput() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "echo hello > b.txt";
		assertEquals("b.txt", new CallCommand(cmdLine).findOutput());
	}

	@Test
	public void testFindArguments() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "< a.txt echo hello > b.txt world";
		List<String> expected = Arrays.asList("echo", "hello", "world");
		List<String> actual = new CallCommand(cmdLine).findArguments();
		assertEquals(expected, actual);
	}
}
