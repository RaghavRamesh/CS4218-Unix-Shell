package substitute_command_tests;

import static org.junit.Assert.*;

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
	
	@Before
	public void setUp() throws Exception {
		
		Environment.currentDirectory = System.getProperty(Consts.Keywords.USER_DIR) + File.separator +  "test-files-integration";
		
		app1 = new GrepApp();
		app2 = new FindApp();
	}

	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = System.getProperty(Consts.Keywords.USER_DIR); 
	}

	/*
	 * This test considers the applications atomically
	 */
	@Test
	public void testGrepWithFindDirectly() throws AbstractApplicationException {
		app2Args = new String[]{"GrepWithSubComman*"};
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		FindApp findApp = new FindApp();
		findApp.run(app2Args, null, outStream);
		app1Args = new String[]{"usage",outStream.toString().replace(System.lineSeparator(), "")};
		
		outStream.reset();
		GrepApp grepApp = new GrepApp();
		grepApp.run(app1Args, null,outStream);
		
		String expected = " This file meant for the usage of grep with sub commands." + System.lineSeparator() + "This is the second usage of the word." + System.lineSeparator();
		assertEquals(expected,outStream.toString());
	}
	
	/*
	 * This test integrates the parsing component as well
	 */
	@Test
	public void testGrepWithFindAlongWithParser() throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	
		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("grep 'usage' `find GrepWithSubComman*`", outStream);
		String expected = " This file meant for the usage of grep with sub commands." + System.lineSeparator() + "This is the second usage of the word." + System.lineSeparator();
		assertEquals(expected,outStream.toString());
	}

	/*
	 * Negative test: Find application throws exception
	 */
	@Test(expected=FindException.class)
	public void testGrepWithFindFailing() throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		ShellImplementation shImpl = new ShellImplementation(null);
		shImpl.parseAndEvaluate("grep 'usage' `find`", outStream);
	}
}
