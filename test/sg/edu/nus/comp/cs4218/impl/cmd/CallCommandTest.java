package sg.edu.nus.comp.cs4218.impl.cmd;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
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
	public void testCallCommandWithInvalidOutputRedirection() {
		String cmdLine = "pwd test >";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail("Exception should be thrown");
		} catch (ShellException e) {
			String expected = "shell: " + Consts.Messages.NO_OUT_PROVIDED
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
	public void testCallCommandWithInvalidInputRedirection() {
		String cmdLine = "pwd test <";
		try {
			new CallCommand(cmdLine).evaluate(System.in, System.out);
			fail("Exception should be thrown");
		} catch (ShellException e) {
			String expected = "shell: " + Consts.Messages.NO_IN_PROVIDED
					+ cmdLine;
			assertEquals(e.getMessage(), expected);
		} catch (Exception e) {
			fail("Wrong exception thrown");
		}
	}
}
