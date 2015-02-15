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
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;

public class CatAppTest {

	File tempTestDirectory = null;
	String currentDirectory = "";
	String tempFolder = "TempTest";

	@Before
	public void setUp() throws Exception {
		currentDirectory = Environment.getCurrentDirectory();
		// create a folder named TempTest in current

		tempTestDirectory = new File(currentDirectory + File.separator
				+ tempFolder);
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

	@Test
	public void testCatAppWithNullArgument() {
		CatApp cmdApp = new CatApp();
		try {
			cmdApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals("cat: " + Consts.Messages.ARG_NOT_NULL, e.getMessage());
		}
	}

	@Test
	public void testCatAppWithNullOutputStreamArgument() {
		CatApp cmdApp = new CatApp();
		String[] args = new String[2];
		args[0] = tempFolder;

		try {
			cmdApp.run(args, null, null);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "cat: "
					+ Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testCatAppWithoutAnyArgumentAndNullIputStream() {
		CatApp cmdApp = new CatApp();
		String[] args = {};

		try {
			cmdApp.run(args, null, System.out);
			fail();

		} catch (CatException e) {
			assertEquals("cat: " + Consts.Messages.INP_STR_NOT_NULL,
					e.getMessage());
		} catch (AbstractApplicationException e) {
			fail();
			e.printStackTrace();
		}
	}

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
			if (tempInput != null) {
				tempInput.delete();
			}
			if (tempOutput != null) {
				tempOutput.delete();
			}
		}
	}

	@Test
	public void testCatAppForIdealWorkFlow() {
		CatApp cmdApp = new CatApp();
		String[] args = new String[2];
		args[0] = tempFolder + File.separator + "test1.txt";
		args[1] = tempFolder + File.separator + "test2.txt";

		File temp = null;

		File file1ToRead = null;
		File file2ToRead = null;

		try {
			file1ToRead = createFileWithContents(tempFolder + File.separator
					+ "test1.txt", "Hi, This is a file1 to test cat workflow");
			file2ToRead = createFileWithContents(tempFolder + File.separator
					+ "test2.txt", "Hello, File2  is used to test cat workflow");

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
			boolean status;
			if (temp != null) {
				status = temp.delete();
				System.out.println("Status is" + status);
			}

			if (file1ToRead != null) {
				status = file1ToRead.delete();
				System.out.println("Status is" + status);
			}

			if (file2ToRead != null) {
				status = file2ToRead.delete();
				System.out.println("Status is" + status);
			}
		}
	}

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

	@Test
	public void testCatAppForNonExistentFile() {
		CatApp cmdApp = new CatApp();
		String[] args = { "test1.txt" };
		try {
			cmdApp.run(args, null, System.out);
			fail();

		} catch (AbstractApplicationException e) {
			assertEquals("cat: " + "can't open 'test1.txt'. "
					+ Consts.Messages.FILE_NOT_FOUND, e.getMessage());
		}
	}

	@Test
	public void testCatAppForReadingDir() {
		CatApp cmdApp = new CatApp();

		// create a directory
		File tempDir = new File(tempTestDirectory.getAbsolutePath()
				+ File.separator + "test1");
		tempDir.mkdir();
		String[] args = { "TempTest" + File.separator + "test1" };
		try {
			cmdApp.run(args, null, System.out);
			fail();

		} catch (CatException exception) {
			assertEquals("cat: can't open " + "'" + "TempTest" + File.separator
					+ "test1" + "'. " + Consts.Messages.FILE_NOT_VALID,
					exception.getMessage());
		} catch (AbstractApplicationException e) {
			fail();

		} finally {
			tempDir.delete();
		}
	}
}
