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
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;

public class TailGrepWc {
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
		shell.parseAndEvaluate(
				"tail -n 1 sample.txt | grep \"brown\" | sed s/brown/black/",
				bao);
		String expected = "The quick black fox jumps over the lazy dog"
				+ System.lineSeparator();
		assertEquals(expected, bao.toString());
	}

	@Test(expected = SedException.class)
	public void chainFailSedInvalid() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		shell.parseAndEvaluate(
				"tail -n 1 sample.txt | grep \"brown\" | sed s/over/lower", bao);
	}

	@Test(expected = ShellException.class)
	public void chainFailTailWrongFile() throws AbstractApplicationException,
			ShellException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		shell.parseAndEvaluate(
				"tail -n 1 < haha.txt | grep \"brown\" | sed s/over/lower", bao);
	}
}
