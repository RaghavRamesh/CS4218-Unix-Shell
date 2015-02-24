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

public class TailAppTest {
	private static final String LINE_SEPARATOR = "line.separator";
	private static final String LINE = "line";
	private static final String TAIL = "tail: ";
	private static final String TEMP_INP_FILE_NAME = "temp-input-file-name.tmp";

	@Test
	public void testNullArgument() {
		TailApp cmdApp = new TailApp();
		try {
			cmdApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), TAIL + Consts.Messages.ARG_NOT_NULL);
		}
	}

	@Test
	public void testNullOutputStream() {
		TailApp cmdApp = new TailApp();
		String[] args = new String[2];
		args[0] = "test1";
		args[1] = "test2";

		try {
			cmdApp.run(args, null, null);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), TAIL + Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testReadFromInputStream() {
		TailApp cmdApp = new TailApp();

		File tempInpFile = null;
		String[] args = new String[0];

		try {
			tempInpFile = new File(TEMP_INP_FILE_NAME);
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			String tenLinesContent = "";
			for (int i = 0; i < 10; i++) {
				tenLinesContent += LINE + (i + 1) + System.getProperty(LINE_SEPARATOR);
			}
			testInpFileOutStream.write(tenLinesContent.getBytes());
			testInpFileOutStream.close();

			// testing for an input of 10 lines or less
			FileInputStream testInputStream = new FileInputStream(TEMP_INP_FILE_NAME);
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(tenLinesContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of more than 10
			String expectedOutput = "";
			for (int i = 0; i < 10; i++) {
				expectedOutput += LINE + (i + 2) + System.getProperty(LINE_SEPARATOR);
			}
			testInpFileOutStream = new FileOutputStream(tempInpFile, true);
			testInpFileOutStream.write(("line11" + System.getProperty("line.separator")).getBytes());
			testInpFileOutStream.close();
			testInputStream = new FileInputStream(TEMP_INP_FILE_NAME);
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(expectedOutput, testOutputStream.toString());
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
		TailApp cmdApp = new TailApp();

		File tempInpFile = null;
		String[] args = new String[1];
		args[0] = "15"; // n=15

		try {
			tempInpFile = new File(TEMP_INP_FILE_NAME);
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			String lessThanNlinesContent = "";
			for (int i = 0; i < (Integer.parseInt(args[0]) - 1); i++) {
				lessThanNlinesContent += LINE + (i + 1) + System.getProperty(LINE_SEPARATOR);
			}
			testInpFileOutStream.write(lessThanNlinesContent.getBytes());
			testInpFileOutStream.close();

			// testing for an input of less than n lines
			FileInputStream testInputStream = new FileInputStream(TEMP_INP_FILE_NAME);
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(lessThanNlinesContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of n lines
			String nLinesOfContent = lessThanNlinesContent + LINE + Integer.parseInt(args[0]) + System.getProperty(LINE_SEPARATOR);
			testInpFileOutStream = new FileOutputStream(tempInpFile, false);
			testInpFileOutStream.write(nLinesOfContent.getBytes());
			testInpFileOutStream.close();
			testInputStream = new FileInputStream(TEMP_INP_FILE_NAME);
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(nLinesOfContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of more than n lines
			String expectedOutput = "";
			for (int i = 0; i < Integer.parseInt(args[0]); i++) {
				expectedOutput += LINE + (i + 2) + System.getProperty(LINE_SEPARATOR);
			}
			testInpFileOutStream = new FileOutputStream(tempInpFile, true);
			testInpFileOutStream.write((LINE + (Integer.parseInt(args[0]) + 1) + System.getProperty(LINE_SEPARATOR)).getBytes());
			testInpFileOutStream.close();
			testInputStream = new FileInputStream(TEMP_INP_FILE_NAME);
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(expectedOutput, testOutputStream.toString());
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
		TailApp cmdApp = new TailApp();

		File tempInpFile = null;
		String[] args = new String[1];

		try {
			tempInpFile = new File(TEMP_INP_FILE_NAME);
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			String tenLinesContent = "";
			for (int i = 0; i < 10; i++) {
				tenLinesContent += LINE + (i + 1) + System.getProperty(LINE_SEPARATOR);
			}
			testInpFileOutStream.write(tenLinesContent.getBytes());
			testInpFileOutStream.close();

			args[0] = TEMP_INP_FILE_NAME;

			// testing for an input of 10 lines or less
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, null, testOutputStream);
			assertEquals(tenLinesContent, testOutputStream.toString());
			testOutputStream.reset();

			// testing for an input of more than 10
			String expectedOutput = "";
			for (int i = 0; i < 10; i++) {
				expectedOutput += LINE + (i + 2) + System.getProperty(LINE_SEPARATOR);
			}
			testInpFileOutStream = new FileOutputStream(tempInpFile, true);

			testInpFileOutStream.write(("line11" + System.getProperty("line.separator")).getBytes());
			testInpFileOutStream.close();
			cmdApp.run(args, null, testOutputStream);
			assertEquals(expectedOutput, testOutputStream.toString());
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
	public void testNegativeNumLines() throws IOException {
		TailApp cmdApp = new TailApp();
		FileInputStream testInputStream = null;

		File tempInpFile = null;
		String[] args = new String[1];

		try {
			tempInpFile = new File(TEMP_INP_FILE_NAME);
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			testInpFileOutStream.write("randomData".getBytes());
			testInpFileOutStream.close();

			// testing for alphabets instead of number
			testInputStream = new FileInputStream("temp-input-file-name.tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			args[0] = "-1"; // n<0
			cmdApp.run(args, testInputStream, testOutputStream);

		} catch (AbstractApplicationException e) {
			testInputStream.close();
			assertEquals(e.getMessage(), "tail: " + Consts.Messages.ILLEGAL_LINE_CNT);

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
	public void testAlphabeticalNumLines() throws IOException {
		TailApp cmdApp = new TailApp();
		FileInputStream testInputStream = null;

		File tempInpFile = null;
		String[] args = new String[1];

		try {
			tempInpFile = new File(TEMP_INP_FILE_NAME);
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			testInpFileOutStream.write("randomData".getBytes());
			testInpFileOutStream.close();

			// testing for alphabets instead of number

			testInputStream = new FileInputStream("temp-input-file-name.tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			args[0] = "abcd"; // n=?
			cmdApp.run(args, testInputStream, testOutputStream);
			fail();

		} catch (AbstractApplicationException e) {
			testInputStream.close();
			assertEquals(e.getMessage(), "tail: " + Consts.Messages.ILLEGAL_LINE_CNT);
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
		TailApp cmdApp = new TailApp();

		String[] args = new String[3];
		args[0] = "abc";
		args[1] = "bcd";
		args[2] = "cde";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), TAIL + Consts.Messages.TOO_MANY_ARGS);
		}
	}

}
