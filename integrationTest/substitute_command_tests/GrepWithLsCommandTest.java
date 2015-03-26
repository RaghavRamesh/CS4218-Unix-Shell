package substitute_command_tests;

import static org.junit.Assert.assertEquals;

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

public class GrepWithLsCommandTest {

	Application app1;
	Application app2;
	String[] app1Args;
	String[] app2Args;

	/*
	 * Command under test: grep 'usage' `ls`
	 */

	@Before
	public void setUp() throws Exception {

		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator
				+ "test-files-integration"
				+ File.separator
				+ "SubstituteCommandTestFiles";

		app1 = new GrepApp();
		app2 = new LsApp();
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
	public void testGrepWithLsDirectly() throws AbstractApplicationException {
		app2Args = new String[] {};
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		LsApp lsApp = new LsApp();
		lsApp.run(app2Args, null, outStream);

		String fileNamesResult = new String(outStream.toString().trim());
		String[] fileNames = fileNamesResult.split("\t");
		app1Args = new String[fileNames.length + 1];
		app1Args[0] = "usage";
		for (int i = 1; i < fileNames.length + 1; i++) {
			app1Args[i] = fileNames[i - 1].replace(System.lineSeparator(), "");
		}

		outStream.reset();
		GrepApp grepApp = new GrepApp();
		grepApp.run(app1Args, null, outStream);

		String expected = " This file meant for the usage of grep with sub commands."
				+ System.lineSeparator()
				+ "This is the second usage of the word."
				+ System.lineSeparator()
				+ "Its tests the usage of various commands."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testGrepWithLsAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("grep 'usage' `ls`", outStream);
		String expected = " This file meant for the usage of grep with sub commands."
				+ System.lineSeparator()
				+ "This is the second usage of the word."
				+ System.lineSeparator()
				+ "Its tests the usage of various commands."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Ls application throws exception
	 */
	@Test(expected = LsException.class)
	public void testGrepWithLsFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("grep 'usage' `ls abcdRandome`", outStream);
	}
}
