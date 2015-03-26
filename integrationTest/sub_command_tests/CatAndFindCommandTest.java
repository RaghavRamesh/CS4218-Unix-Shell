package sub_command_tests;

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
import sg.edu.nus.comp.cs4218.exception.FindException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.CatApp;
import sg.edu.nus.comp.cs4218.impl.app.FindApp;

public class CatAndFindCommandTest {

	Application catApp;
	Application findApp;
	String[] catArgs;
	String[] findArgs;

	// Command under test: cat `find -name SubCommand.txt` `find -name
	// SubCommand2.txt`

	@Before
	public void setUp() throws Exception {

		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR)
				+ File.separator
				+ "test-files-integration"
				+ File.separator
				+ "SubCommandTestFiles";
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
	public void testCatAndFindDirectly() throws AbstractApplicationException {
		// Run first find
		findArgs = new String[] { "-name", "SubCommand.txt" };
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		findApp = new FindApp();
		findApp.run(findArgs, null, outStream);

		String findOutput = outStream.toString().replace(
				System.lineSeparator(), "");
		outStream.reset();

		catArgs = new String[2];
		catArgs[0] = findOutput;

		// Run second find
		findArgs[0] = "-name";
		findArgs[1] = "SubCommand2.txt";
		findApp.run(findArgs, null, outStream);

		findOutput = outStream.toString().replace(System.lineSeparator(), "");
		outStream.reset();

		catArgs[1] = findOutput;

		// Run cat
		catApp = new CatApp();
		catApp.run(catArgs, null, outStream);

		String expected = " This file meant for the usage of grep with sub commands."
				+ System.lineSeparator()
				+ "This is the second usage of the word."
				+ System.lineSeparator()
				+ " This is a second file for testting."
				+ System.lineSeparator()
				+ "Its tests the usage of various commands."
				+ System.lineSeparator();

		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testCatAndFindAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate(
				"cat `find -name SubCommand.txt` `find -name SubCommand2.txt`",
				outStream);
		String expected = " This file meant for the usage of grep with sub commands."
				+ System.lineSeparator()
				+ "This is the second usage of the word."
				+ System.lineSeparator()
				+ " This is a second file for testting."
				+ System.lineSeparator()
				+ "Its tests the usage of various commands."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Wc application throws exception when arguments is empty
	 */
	@Test(expected = FindException.class)
	public void testCatAndFindFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("cat `find` `find`", outStream);
	}

}
