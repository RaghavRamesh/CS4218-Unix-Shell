package hackathon;

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
import sg.edu.nus.comp.cs4218.exception.HeadException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.TailException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.SedApp;

public class HackathonTest {
	private static ByteArrayOutputStream stdout;
	private static String cmdLine; // command line
	private static String expected; // expected output
	private static ShellImplementation shell;
	private static final String NEWLINE = System.lineSeparator();
	private static final String SLASH = System.getProperty("file.separator");

	@Before
	public void setup() throws Exception {
		Environment.currentDirectory = System.getProperty("user.dir")
				+ File.separator + "test-hackathon-files";
		shell = new ShellImplementation(null);
		stdout = new ByteArrayOutputStream();
		cmdLine = "";
		expected = "";
	}

	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR);
	}

	/**
	 * The bug is due to incorrect order of command substitution in call
	 * command. substituteAll(tokens) should be performed when call command is
	 * evaluated, not be constructed.
	 * 
	 * Fixed by change the behaviour when initialize a CallCommand (line 45 to
	 * 46)
	 * 
	 */
	@Test
	public void testSeqWithCdAndSubcmd() throws AbstractApplicationException,
			ShellException {
		Environment.currentDirectory = System
				.getProperty(Consts.Keywords.USER_DIR);
		cmdLine = "cd test-hackathon-files; echo `pwd`";
		shell.parseAndEvaluate(cmdLine, stdout);
		String expected = System.getProperty("user.dir") + File.separator
				+ "test-hackathon-files" + NEWLINE;
		assertEquals(expected, stdout.toString());
	}

	/**
	 * The bug is due to the re-evaluation of content between double quotes in
	 * the method value(). content = parent.substring(begin + 1, end); tokens =
	 * Parser.tokenize(content);
	 * 
	 * Fixed by change the behaviour of DoubleQuoteToken value() method (line 60
	 * to 74)
	 * 
	 */
	@Test
	public void testParseSingleQuotesInsideDoubleQuotes()
			throws AbstractApplicationException, ShellException {
		cmdLine = "echo \"'\"";
		shell.parseAndEvaluate(cmdLine, stdout);
		expected = "'" + NEWLINE;
		assertEquals(expected, stdout.toString());
	}

	/**
	 * The bug is due to Sed app not ignoring the double quotes in the method
	 * run.
	 * 
	 * Fixed by replacing all double quotes in regular expression to empty
	 * string in SedApp class ( lines 55 - 58)
	 */
	@Test
	public void testSedOneWithQuote() throws AbstractApplicationException,
			ShellException {
		SedApp cmdApp = new SedApp();
		String replacement = "s" + SLASH + "\"Clams\"" + SLASH + "X" + SLASH;
		String testString = "Clams here Clams second" + NEWLINE + "Clams there"
				+ NEWLINE;
		String expected = "X here Clams second" + NEWLINE + "X there" + NEWLINE;
		String[] args = new String[] { replacement };
		ByteArrayInputStream stdin = new ByteArrayInputStream(
				testString.getBytes());
		stdout = new ByteArrayOutputStream();
		cmdApp.run(args, stdin, stdout);
		assertEquals(expected, stdout.toString());
	}

	/**
	 * Testing a bug introduced due to absence of exception handling for missing
	 * file name argument
	 * 
	 * Fixed by adding the exception handling for file name argument in HeadApp
	 * (line 77 -92)
	 */
	@Test(expected = HeadException.class)
	public void testHeadNoFileName() throws AbstractApplicationException,
			ShellException {
		cmdLine = "head -n 1";
		shell.parseAndEvaluate(cmdLine, stdout);
	}

	/**
	 * Testing a bug introduced due to absence of exception handling for missing
	 * file name argument
	 * 
	 * Fixed by adding the exception handling for file name argument in TailApp
	 * (line 80-87)
	 */
	@Test(expected = TailException.class)
	public void testTailNoFileName() throws AbstractApplicationException,
			ShellException {
		cmdLine = "tail -n 1";
		shell.parseAndEvaluate(cmdLine, stdout);
	}
}
