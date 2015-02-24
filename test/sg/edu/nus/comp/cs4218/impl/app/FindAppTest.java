package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class FindAppTest {

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

		originalCurrDir = System.getProperty(Consts.Keywords.USER_DIR);
		tempRootDirectory = new File(originalCurrDir + File.separator
				+ "TempTestDir");
		boolean status = tempRootDirectory.mkdir();
		if (!status) {
			fail();
		}
		System.setProperty(Consts.Keywords.USER_DIR,
				tempRootDirectory.getAbsolutePath());

		tempFileInRoot = new File(tempRootDirectory.getAbsolutePath()
				+ File.separator + "tempFileInRoot.tmp");
		PrintWriter tempFileInRootWriter = new PrintWriter(tempFileInRoot);
		tempFileInRootWriter.println("random data");
		tempFileInRootWriter.close();

		tempSubDirectory1 = new File(tempRootDirectory.getAbsolutePath()
				+ File.separator + "temp1");
		if (!tempSubDirectory1.mkdir()) {
			fail();
		}
		tempFileIn1 = new File(tempSubDirectory1.getAbsolutePath()
				+ File.separator + "tempFileName1.tmp");
		PrintWriter tempFile1Writer = new PrintWriter(tempFileIn1);
		tempFile1Writer.println("random data");
		tempFile1Writer.close();

		tempSubDirectory2 = new File(tempRootDirectory.getAbsolutePath()
				+ File.separator + "temp2");
		if (!tempSubDirectory2.mkdir()) {
			fail();
		}
		tempFileIn2 = new File(tempSubDirectory2.getAbsolutePath()
				+ File.separator + "tempFileName2.tmp");
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
		System.setProperty(Consts.Keywords.USER_DIR, originalCurrDir);
	}

	@Test
	public void testNullArgument() {
		findApp = new FindApp();
		try {
			findApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "find: "
					+ Consts.Messages.ARG_NOT_NULL);
		}
	}

	@Test
	public void testNoArguments() {
		findApp = new FindApp();
		String[] args = new String[0];

		try {
			findApp.run(args, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "find: "
					+ Consts.Messages.NO_INPUT_FILE_OR_STDIN);
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
			assertEquals(e.getMessage(), "find: "
					+ Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testWithInvalidSearchPath() {
		findApp = new FindApp();
		String[] args = new String[2];
		args[0] = "aDirectoryNameThatDoesntExist/";
		args[1] = "temp*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "find: "
					+ Consts.Messages.PATH_NOT_FOUND);
		}
	}

	@Test
	public void testNoMatches() {
		findApp = new FindApp();

		String[] args = new String[1];
		args[0] = "temp*Name*afsdf";

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

		String[] args = new String[1];
		args[0] = "";

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
	public void test3MatchesIn3DifferentDirsIncludingTempRoot() {
		findApp = new FindApp();

		String[] args = new String[1];
		args[0] = "t*mp*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			expectedOutput += "temp1" + File.separator + "tempFileName1.tmp\n";
			expectedOutput += "temp2" + File.separator + "tempFileName2.tmp\n";
			expectedOutput += "tempFileInRoot.tmp\n";
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			fail();
		}
	}

	@Test
	public void test1MatchInTempRoot() {
		findApp = new FindApp();

		String[] args = new String[1];
		args[0] = "temp*Root*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			expectedOutput += "tempFileInRoot.tmp\n";
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			fail();
		}
	}

	@Test
	public void test2MatchesInSubDirs() {
		findApp = new FindApp();

		String[] args = new String[1];
		args[0] = "temp*Name*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			expectedOutput += "temp1" + File.separator + "tempFileName1.tmp\n";
			expectedOutput += "temp2" + File.separator + "tempFileName2.tmp\n";
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test1MatchWithRelativeSearchPathSupplied() {
		findApp = new FindApp();

		String[] args = new String[2];
		args[0] = "temp1/";
		args[1] = "temp*Name*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			expectedOutput += "tempFileName1.tmp\n";
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test1MatchWithAbsoluteSearchPathSupplied() {
		findApp = new FindApp();

		String[] args = new String[2];
		args[0] = System.getProperty(Consts.Keywords.USER_DIR) + File.separator
				+ "temp1/";
		args[1] = "temp*Name*";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			findApp.run(args, null, testOutputStream);
			String expectedOutput = "";
			expectedOutput += "tempFileName1.tmp\n";
			assertEquals(expectedOutput, testOutputStream.toString());

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testNoMatchesWithSearchPathSupplied() {
		findApp = new FindApp();

		String[] args = new String[2];
		args[0] = "temp1/";
		args[1] = "temp*Name*asdf*";

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
