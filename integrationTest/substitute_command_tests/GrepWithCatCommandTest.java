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
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.CatApp;
import sg.edu.nus.comp.cs4218.impl.app.GrepApp;
import sg.edu.nus.comp.cs4218.impl.app.LsApp;

public class GrepWithCatCommandTest {

	String[] app1Args;
	String[] app2Args;

	/*
	 * Command under test: cat `ls` | grep "usage"
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
	 * This test considers the applications atomically Command being tested: cat
	 * `ls`| grep "GrepWithSub"
	 */
	@Test
	public void testGrepWithCatDirectly() throws AbstractApplicationException {
		String[] lsArg = {};

		// Do ls first
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		LsApp lsApp = new LsApp();
		lsApp.run(lsArg, null, outStream);

		// Build arguments for cat
		String fileNamesResult = new String(outStream.toString().trim());
		String[] fileNames = fileNamesResult.split("\t");
		app1Args = new String[fileNames.length];
		for (int i = 0; i < fileNames.length; i++) {
			app1Args[i] = fileNames[i].replace(System.lineSeparator(), "");
		}

		outStream.reset();

		// Run Cat
		CatApp catApp = new CatApp();
		catApp.run(app1Args, null, outStream);
		// convert cat output to bytes
		byte[] catOutput = outStream.toByteArray();
		outStream.reset();

		// Run Grep
		ByteArrayInputStream inStream = new ByteArrayInputStream(catOutput);
		app2Args = new String[] { "usage" };
		GrepApp grepApp = new GrepApp();
		grepApp.run(app2Args, inStream, outStream);
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
	public void testGrepWithCatAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("cat `ls` | grep \"usage\"", outStream);
		String expected = " This file meant for the usage of grep with sub commands."
				+ System.lineSeparator()
				+ "This is the second usage of the word."
				+ System.lineSeparator()
				+ "Its tests the usage of various commands."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Cat application throws exception because a.txt does not exist
	 */
	@Test(expected = CatException.class)
	public void testGrepWithCatFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("cat a.txt `ls` | grep \"usage\"", outStream);
	}
}
