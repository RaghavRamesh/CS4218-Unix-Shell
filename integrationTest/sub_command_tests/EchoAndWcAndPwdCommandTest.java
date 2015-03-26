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
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.EchoApp;
import sg.edu.nus.comp.cs4218.impl.app.PwdApp;
import sg.edu.nus.comp.cs4218.impl.app.WcApp;

public class EchoAndWcAndPwdCommandTest {

	Application echoApp;
	Application wcApp;
	Application pwdApp;
	String[] echoArgs;
	String[] wcArgs;
	String[] pwdArgs;

	// Command under test: echo `wc SubCommand.txt` `pwd`

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
	public void testEchoAndWcAndPwdDirectly()
			throws AbstractApplicationException {
		// Run pwd
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		pwdApp = new PwdApp();
		pwdApp.run(null, null, outStream);

		String pwdOutput = outStream.toString();
		pwdOutput = pwdOutput.replace(System.lineSeparator(), "");
		outStream.reset();

		// Run wc
		wcArgs = new String[] { "SubCommand.txt" };
		wcApp = new WcApp();
		wcApp.run(wcArgs, null, outStream);

		String wcOutput = outStream.toString();
		wcOutput = wcOutput.replace(System.lineSeparator(), "");
		outStream.reset();

		// Run echo
		echoArgs = new String[] { wcOutput, pwdOutput };
		echoApp = new EchoApp();
		echoApp.run(echoArgs, null, outStream);

		String expected = "2 19 96 SubCommand.txt "
				+ System.getProperty(Consts.Keywords.USER_DIR) + File.separator
				+ "test-files-integration" + File.separator
				+ "SubCommandTestFiles" + System.lineSeparator();

		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testEchoAndWcAndPwdAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("echo `wc SubCommand.txt` `pwd`", outStream);
		String expected = "2 19 96 SubCommand.txt "
				+ System.getProperty(Consts.Keywords.USER_DIR) + File.separator
				+ "test-files-integration" + File.separator
				+ "SubCommandTestFiles" + System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Wc application throws exception when arguments is empty
	 */
	@Test(expected = WcException.class)
	public void testEchoAndWcAndPwdFailing()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("echo `wc` `pwd`", outStream);
	}

}
