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
import sg.edu.nus.comp.cs4218.exception.HeadException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.EchoApp;
import sg.edu.nus.comp.cs4218.impl.app.HeadApp;
import sg.edu.nus.comp.cs4218.impl.app.TailApp;

public class HeadAndTailAndEchoCommandTest {

	Application echoApp;
	Application headApp;
	Application tailApp;
	String[] echoArgs;
	String[] headArgs;
	String[] tailArgs;

	// Command under test: echo `head -n 1 SubCommand.txt` `tail -n 1
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
	public void testHeadAndTailAndEchoDirectly()
			throws AbstractApplicationException {
		// Run head
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		headArgs = new String[] { "-n", "1", "SubCommand.txt" };
		headApp = new HeadApp();
		headApp.run(headArgs, null, outStream);

		String headOutput = outStream.toString();
		headOutput = headOutput.replace(System.lineSeparator(), "");
		outStream.reset();

		// Run tail
		tailArgs = new String[] { "-n", "1", "SubCommand2.txt" };
		tailApp = new TailApp();
		tailApp.run(tailArgs, null, outStream);

		String tailOutput = outStream.toString();
		tailOutput = tailOutput.replace(System.lineSeparator(), "");
		outStream.reset();

		// Run echo
		echoArgs = new String[] { headOutput, tailOutput };
		echoApp = new EchoApp();
		echoApp.run(echoArgs, null, outStream);

		String expected = " This file meant for the usage of grep with sub commands. "
				+ "Its tests the usage of various commands."
				+ System.lineSeparator();

		assertEquals(expected, outStream.toString());
	}

	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testHeadAndTailAndEchoAlongWithParser()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate(
				"echo `head -n 1 SubCommand.txt` `tail -n 1 SubCommand2.txt`",
				outStream);
		String expected = "This file meant for the usage of grep with sub commands. "
				+ "Its tests the usage of various commands."
				+ System.lineSeparator();
		assertEquals(expected, outStream.toString());
	}

	/*
	 * Negative test: Head application throws exception when arguments is empty
	 */
	@Test(expected = HeadException.class)
	public void testHeadAndTailAndEchoFailing()
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("echo `head` `tail -n 1 SubCommand2.txt`",
				outStream);
	}

}
