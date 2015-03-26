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
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.GrepApp;
import sg.edu.nus.comp.cs4218.impl.app.LsApp;

public class LsWithGrepCommandTest {

	Application lsApp;
	Application grepApp;
	String[] lsArgs;
	String[] grepArgs;

	/*
	 * Command under test: ls PipeCommandTestFiles | grep 'Pipe'
	 */
	
	@Before
	public void setUp() throws Exception {

		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator
				+ "test-files-integration";

		grepApp = new GrepApp();
		lsApp = new LsApp();
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
	public void testLsWithGrepDirectly() throws AbstractApplicationException {
		lsArgs = new String[] { "PipeCommandTestFiles" };
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		LsApp lsApp = new LsApp();
		lsApp.run(lsArgs, null, outStream);

		grepArgs = new String[]{"Pipe"};
		byte[] bArray = outStream.toByteArray();
		ByteArrayInputStream inStream = new ByteArrayInputStream(bArray);
		
		outStream.reset();
		GrepApp grepApp = new GrepApp();
		grepApp.run(grepArgs, inStream, outStream);

		String expected = "GrepWithPipeCommand.txt\t"
				+ "GrepWithPipeCommand2.txt" + System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testLsWithGrepAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("ls PipeCommandTestFiles | grep 'Pipe'", outStream);
		String expected = "GrepWithPipeCommand.txt\t"	+ "GrepWithPipeCommand2.txt" + System.lineSeparator();
		String actual = outStream.toString();
		assertEquals(expected, actual);
	}

	/*
	 * Negative test: Ls application throws exception when directory does not exist
	 */
	@Test(expected = LsException.class)
	public void testLsWithGrepFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("ls abcdRandome | grep 'usage'", outStream);
	}
}
