package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class HeadAppTest {
	@Test
	public void testNullArgument() {
		HeadApp cmdApp = new HeadApp();
		try {
			cmdApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "head: " + Consts.Messages.ARG_NOT_NULL);
		}
	}

	@Test
	public void testNullOutputStream() {
		HeadApp cmdApp = new HeadApp();
		String[] args = new String[2];
		args[0] = "test1";
		args[1] = "test2";

		try {
			cmdApp.run(args, null, null);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "head: " + Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testReadFromInputStream() {
		HeadApp cmdApp = new HeadApp();

		File tempInpFile = null;
		String[] args = new String[0];

		try {
			tempInpFile = new File("temp-input-file-name.tmp");
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			String tenLinesContent = "";
			for (int i = 0; i < 10; i++) {
				tenLinesContent += "line" + (i + 1) + System.getProperty("line.separator");
			}
			testInpFileOutStream.write(tenLinesContent.getBytes());
			testInpFileOutStream.close();

			// testing for an input of 10 lines or less
			FileInputStream testInputStream = new FileInputStream("temp-input-file-name.tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(tenLinesContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of more than 10
			testInpFileOutStream = new FileOutputStream(tempInpFile, true);
			testInpFileOutStream.write(("line11" + System.getProperty("line.separator")).getBytes());
			testInpFileOutStream.close();
			testInputStream = new FileInputStream("temp-input-file-name.tmp");
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(tenLinesContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

		} catch (AbstractApplicationException e) {
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (tempInpFile != null) {
				tempInpFile.delete();
			}
		}
	}

	@Test
	public void testReadFromInputStreamWithNLines() {
		HeadApp cmdApp = new HeadApp();

		File tempInpFile = null;
		String[] args = new String[1];
		args[0] = "15"; // n=15

		try {
			tempInpFile = new File("temp-input-file-name.tmp");
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			String lessThanNlinesContent = "";
			for (int i = 0; i < Integer.parseInt(args[0]) - 1; i++) {
				lessThanNlinesContent += "line" + (i + 1) + System.getProperty("line.separator");
			}
			testInpFileOutStream.write(lessThanNlinesContent.getBytes());
			testInpFileOutStream.close();

			// testing for an input of less than n lines
			FileInputStream testInputStream = new FileInputStream("temp-input-file-name.tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(lessThanNlinesContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of n lines
			String nLinesOfContent = lessThanNlinesContent + "line" + Integer.parseInt(args[0]) + System.getProperty("line.separator");
			testInpFileOutStream = new FileOutputStream(tempInpFile, false);
			testInpFileOutStream.write(nLinesOfContent.getBytes());
			testInpFileOutStream.close();
			testInputStream = new FileInputStream("temp-input-file-name.tmp");
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(nLinesOfContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of more than n lines
			testInpFileOutStream = new FileOutputStream(tempInpFile, true);
			testInpFileOutStream.write(("line" + (Integer.parseInt(args[0]) + 1) + System.getProperty("line.separator")).getBytes());
			testInpFileOutStream.close();
			testInputStream = new FileInputStream("temp-input-file-name.tmp");
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(nLinesOfContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

		} catch (AbstractApplicationException e) {
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (tempInpFile != null) {
				tempInpFile.delete();
			}
		}
	}

	@Test
	public void testReadFromFile() {
		HeadApp cmdApp = new HeadApp();

		File tempInpFile = null;
		String[] args = new String[1];

		try {
			tempInpFile = new File("temp-input-file-name.tmp");
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			String tenLinesContent = "";
			for (int i = 0; i < 10; i++) {
				tenLinesContent += "line" + (i + 1) + System.getProperty("line.separator");
			}
			testInpFileOutStream.write(tenLinesContent.getBytes());
			testInpFileOutStream.close();

			args[0] = "temp-input-file-name.tmp";

			// testing for an input of 10 lines or less
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, null, testOutputStream);
			assertEquals(tenLinesContent, testOutputStream.toString());
			testOutputStream.reset();

			// testing for an input of more than 10
			testInpFileOutStream = new FileOutputStream(tempInpFile, true);
			testInpFileOutStream.write(("line11" + System.getProperty("line.separator")).getBytes());
			testInpFileOutStream.close();
			cmdApp.run(args, null, testOutputStream);
			assertEquals(tenLinesContent, testOutputStream.toString());
			testOutputStream.reset();

		} catch (AbstractApplicationException e) {
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (tempInpFile != null) {
				tempInpFile.delete();
			}
		}
	}

	@Test
	public void testFileNotFound() {
		HeadApp cmdApp = new HeadApp();

		String[] args = new String[1];

		try {

			args[0] = "temp-input-file-name.tmp"; // this filename does not exist

			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, null, testOutputStream);
			assertEquals("", testOutputStream.toString());
			fail();

		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "head: " + Consts.Messages.FILE_DOES_NOT_EXIST);
		}
	}

	@Test
	public void testNegativeNumLines() {
		HeadApp cmdApp = new HeadApp();

		File tempInpFile = null;
		String[] args = new String[1];

		try {
			tempInpFile = new File("temp-input-file-name.tmp");
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			testInpFileOutStream.write("randomData".getBytes());
			testInpFileOutStream.close();

			// testing for alphabets instead of number
			FileInputStream testInputStream = new FileInputStream("temp-input-file-name.tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			args[0] = "-1"; // n<0
			cmdApp.run(args, testInputStream, testOutputStream);

		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "head: " + Consts.Messages.ILLEGAL_LINE_COUNT);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (tempInpFile != null) {
				tempInpFile.delete();
			}
		}
	}

	@Test
	public void testAlphabeticalNumLines() {
		HeadApp cmdApp = new HeadApp();

		File tempInpFile = null;
		String[] args = new String[1];

		try {
			tempInpFile = new File("temp-input-file-name.tmp");
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			testInpFileOutStream.write("randomData".getBytes());
			testInpFileOutStream.close();

			// testing for alphabets instead of number
			FileInputStream testInputStream = new FileInputStream("temp-input-file-name.tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			args[0] = "abcd"; // n=?
			cmdApp.run(args, testInputStream, testOutputStream);
			fail();

		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "head: " + Consts.Messages.ILLEGAL_LINE_COUNT);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (tempInpFile != null) {
				tempInpFile.delete();
			}
		}
	}

	@Test
	public void testTooManyArguments() {
		HeadApp cmdApp = new HeadApp();

		String[] args = new String[3];
		args[0] = "abc";
		args[1] = "bcd";
		args[2] = "cde";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "head: " + Consts.Messages.TOO_MANY_ARGUMENTS);
		}
	}
}
