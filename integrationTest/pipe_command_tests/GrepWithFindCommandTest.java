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
import sg.edu.nus.comp.cs4218.exception.FindException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.FindApp;
import sg.edu.nus.comp.cs4218.impl.app.GrepApp;

public class GrepWithFindCommandTest {

	Application app1;
	Application app2;
	String[] app1Args;
	String[] app2Args;
	
	// Command under test: find "GrepWithPipeComman*" | grep "Pipe"
	
	@Before
	public void setUp() throws Exception {

		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator
				+ "test-files-integration"
				+ File.separator
				+ "PipeCommandTestFiles";

		app1 = new GrepApp();
		app2 = new FindApp();
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
	public void testGrepWithFindDirectly() throws AbstractApplicationException {
		app2Args = new String[] {"-name", "GrepWithPipeComman*"};
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		FindApp findApp = new FindApp();
		findApp.run(app2Args, null, outStream);

		byte[] app2Results = outStream.toByteArray();
		app1Args = new String[] { "Pipe" };
		ByteArrayInputStream inputStream = new ByteArrayInputStream(app2Results);

		outStream.reset();
		GrepApp grepApp = new GrepApp();
		grepApp.run(app1Args, inputStream, outStream);

		String expected = "GrepWithPipeCommand.txt"
				+ System.lineSeparator()
				+ "GrepWithPipeCommand2.txt"
				+ System.lineSeparator();

		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testGrepWithFindAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("find -name GrepWithPipeComman* | grep 'Pipe'",
				outStream);
		String expected = "GrepWithPipeCommand.txt"
				+ System.lineSeparator()
				+ "GrepWithPipeCommand2.txt"
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Find application throws exception
	 */
	@Test(expected = FindException.class)
	public void testGrepWithFindFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("find | grep 'Pipe'", outStream);
	}
}
