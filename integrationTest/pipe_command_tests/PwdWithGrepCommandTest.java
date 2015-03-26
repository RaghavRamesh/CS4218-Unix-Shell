package pipe_command_tests;

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
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.GrepApp;
import sg.edu.nus.comp.cs4218.impl.app.PwdApp;

public class PwdWithGrepCommandTest {

	String[] grepArgs;

	/*
	 * Command under test: pwd | grep 'Pipe'
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
	public void testPwdWithGrepDirectly() throws AbstractApplicationException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// Run pwd
		PwdApp pwdApp = new PwdApp();
		pwdApp.run(null, null, outStream);

		// Convert pwd output to byte array
		byte[] pwdOutput = outStream.toByteArray();
		outStream.reset();

		// Run Grep
		ByteArrayInputStream inStream = new ByteArrayInputStream(pwdOutput);
		grepArgs = new String[] { "Pipe" };
		GrepApp grepApp = new GrepApp();
		grepApp.run(grepArgs, inStream, outStream);
		String expected = System.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator + "test-files-integration" + File.separator
				+ "PipeCommandTestFiles" + System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testPwdWithGrepAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("pwd | grep 'Pipe'", outStream);
		String expected = System.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator + "test-files-integration" + File.separator
				+ "PipeCommandTestFiles" + System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Grep application throws exception because there is no
	 * input was given
	 */
	@Test(expected = GrepException.class)
	public void testPwdWithGrepFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("pwd | grep ", outStream);
	}
}
