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
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.GrepApp;
import sg.edu.nus.comp.cs4218.impl.app.LsApp;
import sg.edu.nus.comp.cs4218.impl.app.WcApp;

public class GrepWithWcCommandTest {

	String[] app1Args;
	String[] app2Args;

	/*
	 * Command under test: wc `ls`| grep "GrepWithSub"
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
	 * This test considers the applications atomically Command being tested: wc
	 * `ls`| grep "GrepWithSub"
	 */
	@Test
	public void testGrepWithWcDirectly() throws AbstractApplicationException {
		String[] lsArg = {};

		// Do ls first
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		LsApp lsApp = new LsApp();
		lsApp.run(lsArg, null, outStream);

		// Build arguments for wc
		String fileNamesResult = new String(outStream.toString().trim());
		String[] fileNames = fileNamesResult.split("\t");
		app1Args = new String[fileNames.length];
		for (int i = 0; i < fileNames.length; i++) {
			app1Args[i] = fileNames[i].replace(System.lineSeparator(), "");
		}

		outStream.reset();

		// Run Wc
		WcApp wcApp = new WcApp();
		wcApp.run(app1Args, null, outStream);
		// convert wc output to bytes
		byte[] wcOutput = outStream.toByteArray();
		outStream.reset();

		// Run Grep
		ByteArrayInputStream inStream = new ByteArrayInputStream(wcOutput);
		app2Args = new String[] { "GrepWithSub" };
		GrepApp grepApp = new GrepApp();
		grepApp.run(app2Args, inStream, outStream);
		String expected = "5 25 138 GrepWithSubCommand.txt"+ System.lineSeparator()+ "1 14 78 GrepWithSubCommand2.txt"
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testGrepWithWcAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("wc `ls`| grep \"GrepWithSub\"", outStream);
		String expected = "5 25 138 GrepWithSubCommand.txt"+ System.lineSeparator()+ "1 14 78 GrepWithSubCommand2.txt"
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Ls application throws exception
	 */
	@Test(expected = LsException.class)
	public void testGrepWithWcFailing() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("wc `ls a`| grep \"GrepWithSub\"", outStream);
	}
}
