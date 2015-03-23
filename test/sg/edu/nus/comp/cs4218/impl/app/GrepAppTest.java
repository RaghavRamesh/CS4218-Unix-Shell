package sg.edu.nus.comp.cs4218.impl.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;

//public class GrepAppTest {
//
//	private static final String TEMP_FILE_NAME1 = "1.tmp";
//	private static final String TEMP_FILE_NAME2 = "2.tmp";
//	private static final String TEMP_FILE_NAME3 = "3.tmp";
//	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
//	private static final String GREP = "grep: ";
//	private static final String TEMP_DIRECTORY_NAME = "TempTestDir";
//	private static final String TEMP_SUB_DIRECTORY_NAME = "temp";
//
//	GrepApp grepApp = null;
//	File tempRootDirectory = null;
//	File tempFile1InRoot = null;
//	File tempFile2InRoot = null;
//	File tempSubDirectory = null;
//	File tempFileInSub = null;
//	String originalCurrDir = "";
//
//	@Before
//	public void setUp() throws Exception {
//		/*
//		 * Creates the following temp dir and file structure:
//		 * TempTestDir->1.tmp
//		 * TempTestDir->2.tmp
//		 * TempTestDir->temp->3.tmp
//		 */
//
//		originalCurrDir = Environment.getCurrentDirectory();
//		tempRootDirectory = new File(originalCurrDir + File.separator + "TempTestDir");
//		boolean status = tempRootDirectory.mkdir();
//		if (!status) {
//			fail();
//		}
//		Environment.setCurrentDirectory(tempRootDirectory.getAbsolutePath());
//
//		tempFile1InRoot = new File(tempRootDirectory.getAbsolutePath() + File.separator + TEMP_FILE_NAME1);
//		PrintWriter file1InRootWtr = new PrintWriter(tempFile1InRoot);
//		file1InRootWtr.println("random data");
//		file1InRootWtr.close();
//
//		tempFile2InRoot = new File(tempRootDirectory.getAbsolutePath() + File.separator + TEMP_FILE_NAME2);
//		PrintWriter file2InRootWtr = new PrintWriter(tempFile2InRoot);
//		file2InRootWtr.println("random data");
//		file2InRootWtr.close();
//
//		tempSubDirectory = new File(tempRootDirectory.getAbsolutePath() + File.separator + TEMP_SUB_DIRECTORY_NAME);
//		if (!tempSubDirectory.mkdir()) {
//			fail();
//		}
//		tempFileInSub = new File(tempSubDirectory.getAbsolutePath() + File.separator + TEMP_FILE_NAME1);
//		PrintWriter fileInSubWtr = new PrintWriter(tempFileInSub);
//		fileInSubWtr.println("random data");
//		fileInSubWtr.close();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		if (tempRootDirectory == null) {
//			fail();
//		}
//
//		tempRootDirectory.delete();
//		tempFile1InRoot.delete();
//		tempFile2InRoot.delete();
//		tempSubDirectory.delete();
//		tempFileInSub.delete();
//		Environment.setCurrentDirectory(originalCurrDir);
//	}
//
//	@Test
//	public void testNullArgumentsArray() {
//		grepApp = new GrepApp();
//		try {
//			grepApp.run(null, null, System.out);
//			fail();
//		} catch (AbstractApplicationException e) {
//
//			assertEquals(GREP + Consts.Messages.ARG_NOT_NULL, e.getMessage());
//		}
//	}
//
//	@Test
//	public void testNoElementsInArgumentsArray() {
//		grepApp = new GrepApp();
//		String[] args = new String[0];
//
//		try {
//			grepApp.run(args, null, System.out);
//			fail();
//		} catch (AbstractApplicationException e) {
//			assertEquals(GREP + Consts.Messages.NO_INP_FOUND, e.getMessage());
//		}
//	}
//
//	@Test
//	public void testNullArguments() {
//		grepApp = new GrepApp();
//
//		String[] args = new String[1];
//		ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
//		try {
//			grepApp.run(args, null, testOutputStream);
//			fail();
//		} catch (AbstractApplicationException e) {
//			assertEquals(GREP + Consts.Messages.ARG_NOT_NULL, e.getMessage());
//		}
//
//		args = new String[2];
//		try {
//			grepApp.run(args, null, testOutputStream);
//			fail();
//		} catch (AbstractApplicationException e) {
//			assertEquals(GREP + Consts.Messages.ARG_NOT_NULL, e.getMessage());
//		}
//
//	}
//
//	@Test
//	public void testNullOutputStream() {
//		grepApp = new GrepApp();
//		String[] args = new String[2];
//		args[0] = "test1";
//		args[1] = "test2";
//
//		try {
//			grepApp.run(args, null, null);
//			fail();
//		} catch (AbstractApplicationException e) {
//			assertEquals(GREP + Consts.Messages.OUT_STR_NOT_NULL, e.getMessage());
//
//		}
//	}
//}

public class GrepAppTest {
	private ByteArrayOutputStream stdout;
	private Application app;

	@Before
	public void setUp() throws Exception {
		Environment.currentDirectory = System.getProperty("user.dir") + "/test-files-ef1/clam1533";
		stdout = new ByteArrayOutputStream();
		app = new GrepApp();
	}

	@Test(expected = GrepException.class)
	public void testGrepNullInputStream() throws AbstractApplicationException {
		String[] args = new String[] { "bar|z" };

		app.run(args, null, stdout);
	}

	@Test(expected = GrepException.class)
	public void cannotGrepDirectory() throws AbstractApplicationException {
		String[] args = { "a", "abalone0000" };

		app.run(args, null, stdout);
	}

	@Test(expected = GrepException.class)
	public void testGrepEmptyPattern() throws AbstractApplicationException {
		String[] args = new String[] { "" };
		app.run(args, new ByteArrayInputStream("foo".getBytes()), stdout);
	}

	@Test
	public void canMatchTwoArgumentsWithOneCharRegex() throws AbstractApplicationException {
		String[] args = { "a", "cxintro01.txt" };
		String expected = "A clam is a type of shellfish." + System.lineSeparator();

		app.run(args, null, stdout);
		Assert.assertEquals(expected, stdout.toString());
	}

	@Test
	public void canGrepWithWhitespacedRegex() throws AbstractApplicationException {
		String[] args = { "A ", "cxintro01.txt" };
		String expected = "A clam is a type of shellfish." + System.lineSeparator();

		app.run(args, null, stdout);
		Assert.assertEquals(expected, stdout.toString());
	}

	@Test
	public void testGrepValidPatternAndNoMatchViaStdin() throws AbstractApplicationException {
		String[] args = new String[] { "grep", "bar|z" };
		app.run(args,
				new ByteArrayInputStream(
						("adinda" + System.lineSeparator() + "riandy" + System.lineSeparator() + "sudarsan" + System.lineSeparator() + "yuan qing")
								.getBytes()), stdout);
		Assert.assertEquals("", stdout.toString());
	}

}
