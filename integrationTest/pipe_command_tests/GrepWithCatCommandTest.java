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
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.CatApp;
import sg.edu.nus.comp.cs4218.impl.app.GrepApp;

public class GrepWithCatCommandTest {

	Application grepApp;
	Application catApp;
	String[] catArgs;
	String[] grepArgs;

	// Command under test: cat PipeCommandTestFiles/GrepWithPipeCommand.txt | grep "usage"

	@Before
	public void setUp() throws Exception {

		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator
				+ "test-files-integration";

		grepApp = new GrepApp();
		catApp = new CatApp();
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
	public void testGrepWithCatDirectly() throws AbstractApplicationException {
		// Build arguments for cat
		catArgs = new String[] { "PipeCommandTestFiles/GrepWithPipeCommand.txt" };
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		// Run
		catApp = new CatApp();
		catApp.run(catArgs, null, outStream);

		// Convert cat output to byte array
		byte[] catOutput = outStream.toByteArray();
		outStream.reset();

		// Run Grep
		ByteArrayInputStream inStream = new ByteArrayInputStream(catOutput);
		grepArgs = new String[] { "usage" };
		grepApp = new GrepApp();
		grepApp.run(grepArgs, inStream, outStream);
		String expected = " This file meant for the usage of grep with sub commands."
				+ System.lineSeparator()
				+ "This is the second usage of the word."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testGrepWithCatAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("cat PipeCommandTestFiles/GrepWithPipeCommand.txt | grep 'usage'",
				outStream);
		String expected = " This file meant for the usage of grep with sub commands."
				+ System.lineSeparator()
				+ "This is the second usage of the word."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Cat application throws exception when file does not exist
	 */
	@Test(expected = CatException.class)
	public void testGrepWithCatFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("cat a.txt | grep 'usage'", outStream);
	}

}
