package substitute_command_tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.TailException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.EchoApp;
import sg.edu.nus.comp.cs4218.impl.app.GrepApp;
import sg.edu.nus.comp.cs4218.impl.app.TailApp;

public class GrepWithTailCommandTest {

	String[] app1Args;
	String[] app2Args;

	/*
	 * Command under test: echo `tail -n 2 GrepWithSubCommand.txt` | grep
	 * "usage"
	 */

	@Before
	public void setUp() throws Exception {

		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator
				+ "test-files-integration"
				+ File.separator
				+ "SubstituteCommandTestFiles";
	}

	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR);
	}

	/*
	 * This test considers the applitailions atomically Command being tested:
	 * tail `ls`| grep "GrepWithSub"
	 */
	@Test
	public void testGrepWithTailDirectly() throws AbstractApplicationException {

		// Build arguments for tail
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		app1Args = new String[] { "-n", "2", "GrepWithSubCommand.txt" };
		// Run Tail
		TailApp tailApp = new TailApp();
		tailApp.run(app1Args, null, outStream);
		// convert tail output to bytes

		String[] echoArgs = new String[] { outStream.toString().trim() };
		outStream.reset();

		EchoApp echoApp = new EchoApp();
		echoApp.run(echoArgs, null, outStream);

		byte[] echoOutput = outStream.toByteArray();
		outStream.reset();

		// Run Grep
		ByteArrayInputStream inStream = new ByteArrayInputStream(echoOutput);
		app2Args = new String[] { "usage" };
		GrepApp grepApp = new GrepApp();
		grepApp.run(app2Args, inStream, outStream);
		String expected = "This is the second usage of the word."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testGrepWithTailAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate(
				"echo `tail -n 2 GrepWithSubCommand.txt` | grep \"usage\"",
				outStream);
		String expected = "This is the second usage of the word."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Tail applitailion throws exception because a.txt does not
	 * exist
	 */
	@Test(expected = TailException.class)
	public void testGrepWithTailFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("tail a.txt `ls` | grep \"usage\"", outStream);
	}
}
