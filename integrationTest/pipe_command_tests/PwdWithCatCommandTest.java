package pipe_command_tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.CatApp;
import sg.edu.nus.comp.cs4218.impl.app.PwdApp;

public class PwdWithCatCommandTest {

	String[] catArgs;

	/*
	 * Command under test: pwd | cat GrepWithPipeCommand.txt
	 */

	@Before
	public void setUp() throws Exception {

		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator
				+ "test-files-integration"
				+ File.separator
				+ "PipeCommandTestFiles";
	}

	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR);
	}

	@Test
	public void testPwdWithCatDirectly() throws AbstractApplicationException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// Run pwd
		PwdApp pwdApp = new PwdApp();
		pwdApp.run(null, null, outStream);

		// Build arguments for cat
		catArgs = new String[] { "GrepWithPipeCommand.txt" };
		outStream.reset();
		// Run Cat
		CatApp catApp = new CatApp();
		catApp.run(catArgs, null, outStream);

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
	public void testPwdWithCatAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("pwd | cat GrepWithPipeCommand.txt", outStream);
		String expected = " This file meant for the usage of grep with sub commands."
				+ System.lineSeparator()
				+ "This is the second usage of the word."
				+ System.lineSeparator();

		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Cat application throws exception because a.txt does not
	 * exist
	 */
	@Test(expected = CatException.class)
	public void testPwdWithCatFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("pwd | cat a.txt", outStream);
	}
}
