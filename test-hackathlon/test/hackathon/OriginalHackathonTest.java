package test.hackathon;

import static org.junit.Assert.*;

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
import sg.edu.nus.comp.cs4218.impl.app.HeadApp;
import sg.edu.nus.comp.cs4218.impl.app.SedApp;
import sg.edu.nus.comp.cs4218.impl.app.TailApp;

public class OriginalHackathonTest {
	private static ByteArrayOutputStream stdout;
	private static String cmdLine;		//command line
	private static String expected;		//expected output
	private static ShellImplementation shell;
	private static final String NEWLINE = System.lineSeparator();
	private static final String SLASH = System.getProperty("file.separator");

	@Before
	public void setup() throws Exception {
		Environment.currentDirectory = System.getProperty("user.dir")+File.separator+"test-hackathon-files";
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
	 * Globbing Failure: unable to replace * is to be replaced by sequence of non slash characters
	 */
	@Test
	public void testFindGlobbing() throws AbstractApplicationException,
			ShellException {
		cmdLine = "find sz-tes* -name cxintro02.txt ";
		expected = "sz-test" + SLASH + "cxintro02.txt" + NEWLINE;
		shell.parseAndEvaluate(cmdLine, stdout);
		assertEquals(expected, stdout.toString());
	}


	/**
	 * Output format of wc for multiple files does not follow unix. 
	 * the "t" in "total" has to be in small caps.
	 * Each number has to be separated by tabs, not spaces.
	 * Discussed on IVLE on 25-Feb-2015, Topic: wc output format
	 */
	@Test
	public void testWcOutput() throws AbstractApplicationException,
			ShellException {
		Environment.currentDirectory = System.getProperty("user.dir")
				+SLASH+"test-hackathon-files"
				+SLASH +"wc-test";
		cmdLine = "wc -m -w -l w.txt b" + SLASH + "dinosaurs.txt";
		expected = "\t" + "4" + "\t" + "6" + "\t" + "14" + " " + "w.txt"+ NEWLINE 
				+ "\t" + "2" + "\t" + "5" + "\t" + "14" + " " + "b"+ SLASH + "dinosaurs.txt" + NEWLINE
				+"\t"+ "6"+ "\t" + "11"+"\t"+"28" +" "+"total" + NEWLINE;
		shell.parseAndEvaluate(cmdLine, stdout);
		assertEquals(expected, stdout.toString());
	}
	
	/**
	 * The bug is due to incorrect format of output when multiple files are provided.
	 * Also discussed on forum 24/3/15 [grep:output for multiple files]
	 */
	@Test
	public void testGrepMultipleFileOutput() throws AbstractApplicationException, ShellException {
				cmdLine = "grep Clams clam1533" + SLASH + "cybody30.txt clam1533"
						+ SLASH + "cybody40.txt";
				expected = "clam1533"
						+ SLASH
						+ "cybody30.txt:Clams eat plankton, and are eaten by small sharks and squid."
						+ NEWLINE
						+ "clam1533"
						+ SLASH
						+ "cybody30.txt:Clams can be eaten by people."
						+ NEWLINE
						+ "clam1533"
						+ SLASH
						+ "cybody40.txt:Clams are a fairly common form of bivalve, therefore making it part of the phylum mollusca."
						+ NEWLINE;
				
				shell.parseAndEvaluate(cmdLine, stdout);
				assertEquals(expected, stdout.toString());
	}
	
	/**
	 * Sed fails to replace string when regexp is in quotes
	 * Output mismatch
	 * @throws AbstractApplicationException
	 * @throws ShellException
	 */
	@Test
	public void testSedOneWithQuote() throws AbstractApplicationException, ShellException{
		SedApp cmdApp = new SedApp();
		String replacement = "s"+ SLASH + "\"Clams\"" + SLASH + "X"+ SLASH;
		String testString = "Clams here Clams second"+ NEWLINE + "Clams there" + NEWLINE;
		String expected = "X here Clams second"+ NEWLINE + "X there" + NEWLINE;
		String[] args = new String[] { replacement };
		ByteArrayInputStream stdin = new ByteArrayInputStream(testString.getBytes());
		stdout = new ByteArrayOutputStream();
		cmdApp.run(args, stdin, stdout);
		assertEquals(expected, stdout.toString());
	}
	
	

	/**
	 * TailApp fails to throw an exception when there are invalid number of command line arguments
	 * As mentioned under Application Specification (page 9, paragraph1), 
	 * the application has to throw an exception in this case.
	 * @throws AbstractApplicationException
	 */
	@Test(expected = TailException.class)
	public void testTailInvalidNumberOfArguments() throws AbstractApplicationException {
		TailApp tailApp = new TailApp();
		String[] args = new String[] { "-n", "1" };
		tailApp.run(args, null, stdout);
	}
	
	/**
	 * HeadApp fails to throw an exception when there are invalid number of command line arguments
	 * As mentioned under Application Specification (page 9, paragraph1), 
	 * the application has to throw an exception in this case.
	 * @throws AbstractApplicationException
	 */
	@Test(expected = HeadException.class)
	public void testHeadInvalidNumberOfArguments() throws AbstractApplicationException {
		HeadApp headApp = new HeadApp();
		String[] args = new String[] { "-n", "1" };
		headApp.run(args, null, stdout);
	}
	
	/**
	 * The bug is due to the re-evaluation of content between double quotes in the method value(). 
	 * content = parent.substring(begin + 1, end); tokens = Parser.tokenize(content);
	 */
	@Test
	public void testParseSingleQuotesInsideDoubleQuotes() throws AbstractApplicationException, ShellException {
		cmdLine = "echo \"'\"";
		shell.parseAndEvaluate(cmdLine, stdout);
		expected = "''";
		assertEquals(expected, stdout.toString());
	}
	
	/**
	 * The bug is due to incorrect order of command substitution in call command.
	 * substituteAll(tokens) should be performed when call command is evaluated, not be constructed.
	 */
	@Test
	public void testSeqWithCdAndSubcmd() throws AbstractApplicationException, ShellException {
		cmdLine = "cd test-hackathon-files;echo `pwd`";
		shell.parseAndEvaluate(cmdLine, stdout);
		String expected = System.getProperty("user.dir")+File.separator+"test-hackathon-files";
		assertEquals(expected, stdout.toString());
	}
	
	/**
	 * The bug is due to incorrect handle of IO-redirection. 
	 * Cat should fail with no input stream
	 */
	@Test (timeout=1000)
	public void testPipeWithEchoIoredirectionAndCat() throws AbstractApplicationException, ShellException {
		Environment.currentDirectory = System.getProperty("user.dir")+File.separator+"io-redirection-files";
		cmdLine = "echo test >out.txt | cat";
		shell.parseAndEvaluate(cmdLine, stdout);
	}
	
	/**
	 * Head app is unable to handle the situation where filename is lacked from
	 * arguments, and cause a null pointer exception unhandled
	 */
	@Test(expected = HeadException.class)
	public void testHeadNoFileName() throws AbstractApplicationException,
			ShellException {
		cmdLine = "head -n 1";
		shell.parseAndEvaluate(cmdLine, stdout);
	}

	/**
	 * Similar to last bug,tail app is unable to handle the situation where
	 * filename is lacked from arguments, and cause a null pointer exception
	 * unhandled
	 */
	@Test(expected = TailException.class)
	public void testTailNoFileName() throws AbstractApplicationException,
			ShellException {
		cmdLine = "tail -n 1";
		shell.parseAndEvaluate(cmdLine, stdout);
	}
	
}
