package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
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
import sg.edu.nus.comp.cs4218.exception.FileCreateException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcAppTest {

	private static final String TMP = ".tmp";
	private static final String TEMP_FILE_INPUT = "temp-file-name-input";
	File tempTestDirectory = null;
	String currentDirectory = "";
	String tempFolder = "TempTest";

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
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
		System.setProperty(Consts.Keywords.USER_DIR, currentDirectory);
	}

	@Test
	public void testWcAppWithNullArgument() throws AbstractApplicationException {

		expectedEx.expect(WcException.class);
		expectedEx.expectMessage("wc: " + Consts.Messages.ARG_NOT_NULL);

		WcApp cmdApp = new WcApp();
		cmdApp.run(null, null, System.out);
	}

	@Test
	public void testWcAppWithNullOutputStreamArgument()
			throws AbstractApplicationException {

		expectedEx.expect(WcException.class);
		expectedEx.expectMessage("wc: " + Consts.Messages.OUT_STR_NOT_NULL);

		WcApp cmdApp = new WcApp();
		String[] args = new String[2];
		args[0] = tempFolder;

		cmdApp.run(args, null, null);
	}

	@Test
	public void testWcAppWithoutAnyArgumentAndNullIputStream()
			throws AbstractApplicationException {
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage("wc: " + Consts.Messages.INP_STR_NOT_NULL);

		WcApp cmdApp = new WcApp();
		String[] args = {};

		cmdApp.run(args, null, System.out);
	}

	@Test
	public void testWcAppWithoutAnyArgument() {

		WcApp cmdApp = new WcApp();
		String[] args = {};
		File tempInput = null;
		File tempOutput = null;
		OutputStream fileOutStream = null;

		try {
			tempInput = File.createTempFile(TEMP_FILE_INPUT, TMP);
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for Wc");
			writer.close();
			fileOutStream.close();

			InputStream fileInputStream = new FileInputStream(tempInput);

			tempOutput = File.createTempFile("temp-file-name", TMP);
			fileOutStream = new FileOutputStream(tempOutput);

			cmdApp.run(args, fileInputStream, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(tempOutput)));
			assertEquals("66 14 1 ", buffReader.readLine());
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
	public void testWcAppWithOnlyWordCount() {
		WcApp cmdApp = new WcApp();
		String[] args = { "-w" };
		File tempInput = null;
		File tempOutput = null;
		OutputStream fileOutStream = null;

		try {
			tempInput = File.createTempFile(TEMP_FILE_INPUT, TMP);
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for Wc");
			writer.close();
			fileOutStream.close();

			InputStream fileInputStream = new FileInputStream(tempInput);

			tempOutput = File.createTempFile("temp-file-name", TMP);
			fileOutStream = new FileOutputStream(tempOutput);

			cmdApp.run(args, fileInputStream, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(tempOutput)));
			assertEquals("14 ", buffReader.readLine());
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
	public void testWcAppWithCountFromFile() {

		WcApp cmdApp = new WcApp();
		String[] args = { "-w", "-l", TEMP_FILE_INPUT };
		File tempInput = null;
		File tempOutput = null;
		OutputStream fileOutStream = null;

		try {
			tempInput = Environment.createFile(TEMP_FILE_INPUT);
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for Wc");
			writer.close();
			fileOutStream.close();

			tempOutput = File.createTempFile("temp-file-name", TMP);
			fileOutStream = new FileOutputStream(tempOutput);

			cmdApp.run(args, null, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(tempOutput)));
			assertEquals("14 1 temp-file-name-input", buffReader.readLine());
			assertEquals("14 1 Total", buffReader.readLine());
			buffReader.close();

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		} catch (FileCreateException e) {
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
}
