package grep_with_pipe_tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.TestHelper;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;

public class CatGrepWcTest {
	private static Shell shell;
	private static String folderPath = System.getProperty("user.dir")
			+ "/test-files-integration";
	private static String tmpFolderPath = folderPath + "/tmp";

	@Before
	public void setUp() throws Exception {
		Environment.currentDirectory = folderPath;
		(new File(tmpFolderPath)).mkdir();
		shell = new ShellImplementation(null);
	}

	@After
	public void tearDown() throws Exception {
		File tmpFolder = new File(tmpFolderPath);
		TestHelper.purgeDirectory(tmpFolder);
	}

	void cdTempFolder() {
		Environment.currentDirectory = tmpFolderPath;
	}

	@Test
	public void chainSuccess() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		shell.parseAndEvaluate("cat sample.txt | grep \"9000\" | wc -l", bao);
		String expected = "1" + System.lineSeparator();
		assertEquals(expected, bao.toString());
	}

	@Test(expected = ShellException.class)
	public void chainFailWcFromWrongFile() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		shell.parseAndEvaluate(
				"cat sample.txt | grep \"nonsense\" | wc -l < sam.txt", bao);
	}

	@Test(expected = CatException.class)
	public void chainFailCatInvalidFile() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		shell.parseAndEvaluate(
				"cat samp.txt | grep \"9000\" | sed s/power/lower/", bao);
	}
}
