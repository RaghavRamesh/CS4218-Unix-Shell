package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;

public class CatAppTest {

	private static final String TEST1 = "test1";
	private static final String TEST2_TXT = "test2.txt";
	private static final String TEST1_TXT = "test1.txt";
	private static final String CAT_EXCEPTION = "cat: ";
	private static final String TEMP_FOLDER = "TempTest";
	File tempTestDirectory = null;
	String currentDirectory = "";

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	/*
	 * Create a folder tempTestDirectory so that tests run in an uniform manner
	 * across machines
	 */
	@Before
	public void setUp() throws Exception {
		currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
		// create a folder named TempTest in current directory

		tempTestDirectory = new File(currentDirectory + File.separator
				+ TEMP_FOLDER);
		boolean status = tempTestDirectory.mkdir();
		if (!status) {
			fail();
		}
	}

	@After
	public void tearDown() throws Exception {
		if (tempTestDirectory == null) {
			fail();
		}

		// Delete the temporary folder and change the current Directory to
		// previous case
		tempTestDirectory.delete();
		Environment.setCurrentDirectory(currentDirectory);
	}

	/*
	 * Tests for null output stream
	 */

	@Test
	public void testCatAppWithNullArgument()
			throws AbstractApplicationException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage(CAT_EXCEPTION + Consts.Messages.ARG_NOT_NULL);
		CatApp cmdApp = new CatApp();

		cmdApp.run(null, null, System.out);

	}

	/*
	 * Tests for null output stream
	 */
	@Test
	public void testCatAppWithNullOutputStreamArgument()
			throws AbstractApplicationException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage(CAT_EXCEPTION
				+ Consts.Messages.OUT_STR_NOT_NULL);

		CatApp cmdApp = new CatApp();
		String[] args = new String[2];
		args[0] = TEMP_FOLDER;

		cmdApp.run(args, null, null);

	}

	/*
	 * Tests for null input stream
	 */
	@Test
	public void testCatAppWithoutAnyArgumentAndNullIputStream()
			throws AbstractApplicationException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage(CAT_EXCEPTION
				+ Consts.Messages.INP_STR_NOT_NULL);

		CatApp cmdApp = new CatApp();
		String[] args = {};

		cmdApp.run(args, null, System.out);
	}

	/*
	 * Tests Cat Application with empty argument list. Try..catch is used here
	 * instead of throwing exception and then catching them, because we want to
	 * clean up the temporary files that were used by the test even if there is
	 * some other exception thrown using the finally block
	 */
	@Test
	public void testCatAppWithoutAnyArgument() {

		CatApp cmdApp = new CatApp();
		String[] args = {};
		File tempInput = null;
		File tempOutput = null;
		OutputStream fileOutStream = null;

		try {
			tempInput = File.createTempFile("temp-file-name-input", ".tmp");
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for cat");
			writer.close();
			fileOutStream.close();

			InputStream fileInputStream = new FileInputStream(tempInput);

			tempOutput = File.createTempFile("temp-file-name", ".tmp");
			fileOutStream = new FileOutputStream(tempOutput);

			cmdApp.run(args, fileInputStream, System.out);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(tempInput)));
			assertEquals(
					"Hi, This is a string to test input from input stream reader for cat",
					buffReader.readLine());
			buffReader.close();

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		} finally {
			// delete the files that were created in the test
			if (tempInput != null) {
				tempInput.delete();
			}
			if (tempOutput != null) {
				tempOutput.delete();
			}
		}
	}

	/*
	 * Positive test for the entire ideal workflow
	 */
	@Test
	public void testCatAppForIdealWorkFlow() {
		CatApp cmdApp = new CatApp();
		String[] args = new String[2];
		args[0] = TEMP_FOLDER + File.separator + TEST1_TXT;
		args[1] = TEMP_FOLDER + File.separator + TEST2_TXT;

		File temp = null;

		File file1ToRead = null;
		File file2ToRead = null;

		try {
			file1ToRead = createFileWithContents(TEMP_FOLDER + File.separator
					+ TEST1_TXT, "Hi, This is a file1 to test cat workflow");
			file2ToRead = createFileWithContents(TEMP_FOLDER + File.separator
					+ TEST2_TXT, "Hello, File2  is used to test cat workflow");

			// temp file used for storing and checking output correctness

			temp = File.createTempFile("temp-file-name", ".tmp");
			OutputStream fileOutStream = new FileOutputStream(temp);

			cmdApp.run(args, null, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(temp)));
			assertEquals("Hi, This is a file1 to test cat workflow",
					buffReader.readLine());
			assertEquals("Hello, File2  is used to test cat workflow",
					buffReader.readLine());
			buffReader.close();

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (temp != null) {
				temp.delete();
			}

			if (file1ToRead != null) {
				file1ToRead.delete();
			}

			if (file2ToRead != null) {
				file2ToRead.delete();
			}
		}
	}

	/*
	 * Test for non existent files
	 */
	@Test
	public void testCatAppForNonExistentFile()
			throws AbstractApplicationException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage(CAT_EXCEPTION + "can't open 'test1.txt'. "
				+ Consts.Messages.FILE_NOT_FOUND);
		CatApp cmdApp = new CatApp();
		String[] args = { TEST1_TXT };

		cmdApp.run(args, null, System.out);

	}

	/*
	 * Test for cat provided with directory
	 */
	@Test
	public void testCatAppForReadingDir() throws AbstractApplicationException {
		CatApp cmdApp = new CatApp();

		// create a directory
		File tempDir = new File(tempTestDirectory.getAbsolutePath()
				+ File.separator + TEST1);
		tempDir.mkdir();
		String[] args = { TEMP_FOLDER + File.separator + TEST1 };
		try {
			cmdApp.run(args, null, System.out);
			fail();

		} catch (CatException exception) {
			assertEquals("cat: can't open " + "'" + TEMP_FOLDER
					+ File.separator + TEST1 + "'. "
					+ Consts.Messages.FILE_NOT_VALID, exception.getMessage());
		} catch (AbstractApplicationException e) {
			fail();

		} finally {
			tempDir.delete();
		}
	}

	/**
	 * Helper method for creating files with contents
	 * 
	 * @param fileName
	 * @param fileContents
	 * @return
	 * @throws IOException
	 */
	private File createFileWithContents(String fileName, String fileContents)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileName)));
		writer.write(fileContents);
		writer.close();

		File test = new File(fileName);
		assertTrue(test.exists());
		return test;
	}
}
