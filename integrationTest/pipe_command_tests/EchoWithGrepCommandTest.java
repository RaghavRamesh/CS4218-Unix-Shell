package pipe_command_tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.EchoApp;
import sg.edu.nus.comp.cs4218.impl.app.GrepApp;

public class EchoWithGrepCommandTest {

	Application echoApp;
	Application grepApp;
	String[] echoArgs;
	String[] grepArgs;

	// Command under test: echo PipeCommandTestFiles | grep 'Pipe'

	@Before
	public void setUp() throws Exception {

		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator
				+ "test-files-integration";

		echoApp = new EchoApp();
		grepApp = new GrepApp();
	}

	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR);
	}

	/*
	 * This test considers the applications atomically
	 */
	@Test
	public void testEchoWithGrepDirectly() throws AbstractApplicationException {
		echoArgs = new String[] { "PipeCommandTestFiles" };
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		echoApp = new EchoApp();
		echoApp.run(echoArgs, null, outStream);

		grepArgs = new String[] { "Pipe" };

		byte[] echoOutput = outStream.toByteArray();
		ByteArrayInputStream inStream = new ByteArrayInputStream(echoOutput);

		outStream.reset();
		GrepApp grepApp = new GrepApp();
		grepApp.run(grepArgs, inStream, outStream);

		String expected = "PipeCommandTestFiles" + System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testEchoWithGrepAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("echo PipeCommandTestFiles | grep 'Pipe'",
				outStream);
		String expected = "PipeCommandTestFiles" + System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Grep application throws exception because echo returns
	 * empty string
	 */
	@Test(expected = GrepException.class)
	public void testEchoWithGrepFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("echo | grep", outStream);
	}
}
