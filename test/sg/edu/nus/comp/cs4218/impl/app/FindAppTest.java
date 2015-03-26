package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class FindAppTest {

	private static final String TEMP_FILE_NAME1 = "tempFileName1.tmp";
	private static final String TEMP_FILE_NAME2 = "tempFileName2.tmp";
	private static final String TEMP_FILE_ROOT = "tempFileInRoot.tmp";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String TEMP1 = "temp1";
	private static final String FIND = "find: ";
	FindApp findApp = null;
	File tempRootDirectory = null;
	File tempSubDirectory1 = null;
	File tempSubDirectory2 = null;
	File tempFileInRoot = null;
	File tempFileIn1 = null;
	File tempFileIn2 = null;
	String originalCurrDir = "";

	@Before
	public void setUp() throws Exception {

		/*
		 * Creates the following temp dir and file structure:
		 * TempTestDir->tempFileInRoot.tmp TempTestDir->temp1->tempFileName1.tmp
		 * TempTestDir->temp2->tempFileName2.tmp
		 */

		originalCurrDir = Environment.getCurrentDirectory();
		tempRootDirectory = new File(originalCurrDir + File.separator + "TempTestDir");
		boolean status = tempRootDirectory.mkdir();
		if (!status) {
			fail();
		}
		Environment.setCurrentDirectory(tempRootDirectory.getAbsolutePath());

		tempFileInRoot = new File(tempRootDirectory.getAbsolutePath() + File.separator + TEMP_FILE_ROOT);
		PrintWriter fileInRootWtr = new PrintWriter(tempFileInRoot);
		fileInRootWtr.println("random data");
		fileInRootWtr.close();

		tempSubDirectory1 = new File(tempRootDirectory.getAbsolutePath() + File.separator + TEMP1);
		if (!tempSubDirectory1.mkdir()) {
			fail();
		}
		tempFileIn1 = new File(tempSubDirectory1.getAbsolutePath() + File.separator + TEMP_FILE_NAME1);
		PrintWriter tempFile1Writer = new PrintWriter(tempFileIn1);
		tempFile1Writer.println("random data");
		tempFile1Writer.close();

		tempSubDirectory2 = new File(tempRootDirectory.getAbsolutePath() + File.separator + "temp2");
		if (!tempSubDirectory2.mkdir()) {
			fail();
		}
		tempFileIn2 = new File(tempSubDirectory2.getAbsolutePath() + File.separator + TEMP_FILE_NAME2);
		PrintWriter tempFile2Writer = new PrintWriter(tempFileIn2);
		tempFile2Writer.println("random data");
		tempFile2Writer.close();
	}

	@After
	public void tearDown() throws Exception {
		if (tempRootDirectory == null) {
			fail();
		}

		tempFileInRoot.delete();
		tempFileIn1.delete();
		tempFileIn2.delete();
		tempSubDirectory1.delete();
		tempSubDirectory2.delete();
		tempRootDirectory.delete();
		Environment.setCurrentDirectory(originalCurrDir);
	}

	@Test
	public void testNullArgumentsArray() {
		findApp = new FindApp();
		try {
			findApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {

			assertEquals(e.getMessage(), FIND + Consts.Messages.ARG_NOT_NULL);
		}
	}

	@Test
	public void testNoElementsInArgumentsArray() {
		findApp = new FindApp();
		String[] args = new String[0];

		try {
			findApp.run(args, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(FIND + Consts.Messages.ARG_NOT_NULL, e.getMessage());
		}
	}

	@Test
	public void testNullArguments() {
		FindApp cmdApp = new FindApp();

		String[] args = new String[1];
		ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(FIND + Consts.Messages.ARG_NOT_NULL, e.getMessage());
		}

		args = new String[2];
		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(FIND + Consts.Messages.ARG_NOT_NULL, e.getMessage());
		}

		args = new String[3];
		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), FIND + Consts.Messages.ARG_NOT_NULL);
		}

	}

	@Test
	public void testNullOutputStream() {
		findApp = new FindApp();
		String[] args = new String[2];
		args[0] = "test1";
		args[1] = "test2";

		try {
			findApp.run(args, null, null);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), FIND + Consts.Messages.OUT_STR_NOT_NULL);

		}
	}

	@Test
	public void testWithInvalidSearchPath() {
		findApp = new FindApp();
		String[] args = new String[3];
		args[0] = "aDirectoryNameThatDoesntExist" + File.separator;
		args[1] = "-name";
		args[2] = "temp*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), FIND + Consts.Messages.PATH_NOT_FOUND);
		}
	}

	@Test
	public void testNoMatches() {
		findApp = new FindApp();

		String[] args = new String[2];
		args[0] = "-name";
		args[1] = "temp*Name*afsdf";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			fail();
		}
	}

	@Test
	public void testEmptyFindString() {
		findApp = new FindApp();

		String[] args = new String[2];
		args[0] = "-name";
		args[1] = "";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			assertEquals(expectedOutput, testOutputStream.toString());
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), FIND + Consts.Messages.ARG_NOT_EMPTY);
		}
	}

	@Test
	public void testAllMatchesInDifferentDirs() {
		findApp = new FindApp();

		String[] args = new String[2];
		args[0] = "-name";
		args[1] = "*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";

			expectedOutput += "temp1/" + TEMP_FILE_NAME1 + LINE_SEPARATOR;
			expectedOutput += "temp2/" + TEMP_FILE_NAME2 + LINE_SEPARATOR;
			expectedOutput += TEMP_FILE_ROOT + LINE_SEPARATOR;
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			fail();
		}
	}

	@Test
	public void test3MatchesIn3DifferentDirsIncludingTempRoot() {
		findApp = new FindApp();

		String[] args = new String[2];
		args[0] = "-name";
		args[1] = "t*mp*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";

			expectedOutput += "temp1/" + TEMP_FILE_NAME1 + LINE_SEPARATOR;
			expectedOutput += "temp2/" + TEMP_FILE_NAME2 + LINE_SEPARATOR;
			expectedOutput += TEMP_FILE_ROOT + LINE_SEPARATOR;
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			fail();
		}
	}

	@Test
	public void test1MatchInTempRoot() {
		findApp = new FindApp();

		String[] args = new String[2];
		args[0] = "-name";
		args[1] = "temp*Root*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			expectedOutput += TEMP_FILE_ROOT + LINE_SEPARATOR;
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			fail();
		}
	}

	@Test
	public void test2MatchesInSubDirs() {
		findApp = new FindApp();

		String[] args = new String[2];
		args[0] = "-name";
		args[1] = "temp*Name*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			expectedOutput += "temp1/" + TEMP_FILE_NAME1 + LINE_SEPARATOR;
			expectedOutput += "temp2/" + TEMP_FILE_NAME2 + LINE_SEPARATOR;
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test1MatchWithRelativeSearchPathSupplied() {
		findApp = new FindApp();

		String[] args = new String[3];
		args[0] = TEMP1 + File.separator;
		args[1] = "-name";
		args[2] = "temp*Name*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			expectedOutput += TEMP_FILE_NAME1 + LINE_SEPARATOR;
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test1MatchWithAbsoluteSearchPathSupplied() throws InvalidDirectoryException, IOException {
		findApp = new FindApp();

		String[] args = new String[3];
		args[0] = Environment.getCurrentDirectory() + File.separator + TEMP1 + File.separator;
		args[1] = "-name";
		args[2] = "temp*Name*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";

			expectedOutput += TEMP_FILE_NAME1 + LINE_SEPARATOR;
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testNoMatchesWithSearchPathSupplied() {
		findApp = new FindApp();

		String[] args = new String[3];
		args[0] = TEMP1 + File.separator;
		args[1] = "-name";
		args[2] = "temp*Name*asdf*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			fail();
		}
	}

}
