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
import sg.edu.nus.comp.cs4218.exception.HeadException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.EchoApp;
import sg.edu.nus.comp.cs4218.impl.app.GrepApp;
import sg.edu.nus.comp.cs4218.impl.app.HeadApp;

public class GrepWithHeadCommandTest {

	String[] app1Args;
	String[] app2Args;

	/*
	 * Command under test: echo `head -n 1 GrepWithSubCommand.txt` | grep
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
	 * This test considers the appliheadions atomically Command being tested:
	 * head `ls`| grep "GrepWithSub"
	 */
	@Test
	public void testGrepWithHeadDirectly() throws AbstractApplicationException {

		// Build arguments for head
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		app1Args = new String[] { "-n", "1", "GrepWithSubCommand.txt" };
		// Run Head
		HeadApp headApp = new HeadApp();
		headApp.run(app1Args, null, outStream);
		// convert head output to bytes

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
		String expected = "This file meant for the usage of grep with sub commands."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testGrepWithHeadAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate(
				"echo `head -n 1 GrepWithSubCommand.txt` | grep \"usage\"",
				outStream);
		String expected = "This file meant for the usage of grep with sub commands."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Head appliheadion throws exception because a.txt does not
	 * exist
	 */
	@Test(expected = HeadException.class)
	public void testGrepWithHeadFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("head a.txt `ls` | grep \"usage\"", outStream);
	}
}
