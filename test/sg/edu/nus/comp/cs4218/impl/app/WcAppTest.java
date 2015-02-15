package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

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
import sg.edu.nus.comp.cs4218.exception.FileCreateException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcAppTest {

	File tempTestDirectory = null;
	String currentDirectory = "";
	String tempFolder = "TempTest";

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
	public void testWcAppWithNullArgument() {
		WcApp cmdApp = new WcApp();
		try {
			cmdApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals("wc: " + Consts.Messages.ARG_NOT_NULL, e.getMessage());
		}
	}

	@Test
	public void testWcAppWithNullOutputStreamArgument() {
		WcApp cmdApp = new WcApp();
		String[] args = new String[2];
		args[0] = tempFolder;

		try {
			cmdApp.run(args, null, null);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "wc: "
					+ Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testWcAppWithoutAnyArgumentAndNullIputStream() {
		WcApp cmdApp = new WcApp();
		String[] args = {};

		try {
			cmdApp.run(args, null, System.out);
			fail();

		} catch (WcException e) {
			assertEquals("wc: " + Consts.Messages.INP_STR_NOT_NULL,
					e.getMessage());
		} catch (AbstractApplicationException e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test
	public void testWcAppWithoutAnyArgument() {

		WcApp cmdApp = new WcApp();
		String[] args = {};
		File tempInput = null;
		File tempOutput = null;
		OutputStream fileOutStream = null;

		try {
			tempInput = File.createTempFile("temp-file-name-input", ".tmp");
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for Wc");
			writer.close();
			fileOutStream.close();

			InputStream fileInputStream = new FileInputStream(tempInput);

			tempOutput = File.createTempFile("temp-file-name", ".tmp");
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
			tempInput = File.createTempFile("temp-file-name-input", ".tmp");
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for Wc");
			writer.close();
			fileOutStream.close();

			InputStream fileInputStream = new FileInputStream(tempInput);

			tempOutput = File.createTempFile("temp-file-name", ".tmp");
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
		String[] args = { "-w", "-l", "temp-file-name-input" };
		File tempInput = null;
		File tempOutput = null;
		OutputStream fileOutStream = null;

		try {
			tempInput = Environment.createFile("temp-file-name-input");
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for Wc");
			writer.close();
			fileOutStream.close();

			tempOutput = File.createTempFile("temp-file-name", ".tmp");
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
