package sg.edu.nus.comp.cs4218.impl.cmd;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class CallCommandTest {
	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testCallCommandWithTooManyOutputRedirection() {
		String cmdLine = "pwd test > a.txt >b.txt";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail("Exception should be thrown");
		} catch (ShellException e) {
			String expected = "shell: " + Consts.Messages.TOO_MANY_OUTPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}

	@Test
	public void testCallCommandWithNoOutputRedirectionProvided() {
		String cmdLine = "pwd test >";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail("Exception should be thrown");
		} catch (ShellException e) {
			String expected = "shell: " + Consts.Messages.INVALID_OUTPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}

	@Test
	public void testCallCommandWithInvalidOutputRedirectionProvided() {
		String cmdLine = "pwd test > <";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail("Exception should be thrown");
		} catch (ShellException e) {
			String expected = "shell: " + Consts.Messages.INVALID_OUTPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}

	@Test
	public void testCallCommandWithTooManyInputRedirection() {
		String cmdLine = "pwd test < a.txt <b.txt";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail("Exception should be thrown");
		} catch (ShellException e) {
			String expected = "shell: " + Consts.Messages.TOO_MANY_INPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}

	@Test
	public void testCallCommandWithNoInputRedirectionProvided() {
		String cmdLine = "pwd test <";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail("Exception should be thrown");
		} catch (ShellException e) {
			String expected = "shell: " + Consts.Messages.INVALID_INPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}

	@Test
	public void testCallCommandWithInvalidInputRedirectionProvided() {
		String cmdLine = "pwd test < <";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail("Exception should be thrown");
		} catch (ShellException e) {
			String expected = "shell: " + Consts.Messages.INVALID_INPUT
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}
	
	@Test
	public void testSubstituteWithDoubleQuoteAndBackQuote() throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo \"this is space: `echo \" \"`\"";
		assertEquals("echo this is space:", new CallCommand(cmdLine).substitute(cmdLine));
	}
	
	@Test
	public void testSubstituteWithSingleQuoteAndBackQuote() throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo 'hello `echo world`'";
		assertEquals("echo hello world", new CallCommand(cmdLine).substitute(cmdLine));
	}
	
	@Test
	public void testSubstituteWithAllQuotes() throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo \"echo 'echo `echo hello world` world' world\"";
		assertEquals("echo echo 'echo `echo hello world` world' world", new CallCommand(cmdLine).substitute(cmdLine));
	}
	
	
	@Test
	public void testSubstituteWithMultipleEchoStatements() throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo 'echo hello `echo world`'";
		assertEquals("echo echo hello world", new CallCommand(cmdLine).substitute(cmdLine));
	}
	
	@Test
	public void testSubstituteWithOnlyBackQuote() throws AbstractApplicationException, ShellException, IOException {
		String cmdLine = "echo `echo hello`";
		assertEquals("echo hello", new CallCommand(cmdLine).substitute(cmdLine));
	}
}
