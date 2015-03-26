package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class HeadAppTest {
	private static final String LINE = "line";
	private static final String HEAD = "head: ";
	private static final String TEMP_INPUT_FILE = "temp-input-file-name.tmp";
	private static final String LINE_SEPARATOR = "line.separator";
	
  @Before
  public void setUp() throws Exception {
    Environment.currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
  }
  
  @After
  public void tearDown() throws Exception{
	  Environment.currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
  }

	@Test
	public void testNullArgumentsArray() {
		HeadApp cmdApp = new HeadApp();
		try {
			cmdApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD + Consts.Messages.ARG_NOT_NULL);
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
			assertEquals(e.getMessage(), HEAD
					+ Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testNullArguments() {
		HeadApp cmdApp = new HeadApp();

		String[] args = new String[1];
		ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD + Consts.Messages.ARG_NOT_NULL);
		}

		args = new String[2];
		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD + Consts.Messages.ARG_NOT_NULL);
		}

		args = new String[3];
		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD + Consts.Messages.ARG_NOT_NULL);
		}

	}

	@Test
	public void testEmptyArguments() {
		HeadApp cmdApp = new HeadApp();

		String[] args = new String[1];
		args[0] = "";
		ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD + Consts.Messages.ARG_NOT_EMPTY);
		}

		args = new String[2];
		args[0] = "";
		args[1] = "";
		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD + Consts.Messages.ARG_NOT_EMPTY);
		}

		args = new String[3];
		args[0] = "";
		args[1] = "";
		args[2] = "";
		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD + Consts.Messages.ARG_NOT_EMPTY);
		}

	}

	@Test
	public void testReadWithInvalidOptions() {
		HeadApp cmdApp = new HeadApp();

		ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
		String[] args = new String[2];
		args[0] = "-N"; // capital N as line number is not acceptable
		args[1] = "15";

		try {
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD + Consts.Messages.INVALID_OPTION);
		}
	}

	@Test
	public void testReadFromInputStream() {
		HeadApp cmdApp = new HeadApp();

		File tempInpFile = null;
		String[] args = new String[0];

		try {
			tempInpFile = new File(TEMP_INPUT_FILE);
			FileOutputStream fileOutStream = new FileOutputStream(tempInpFile);
			String tenLinesContent = "";
			for (int i = 0; i < 10; i++) {
				tenLinesContent += LINE + (i + 1)
						+ System.getProperty(LINE_SEPARATOR);
			}
			fileOutStream.write(tenLinesContent.getBytes());
			fileOutStream.close();

			// testing for an input of 10 lines or less
			FileInputStream testInputStream = new FileInputStream(
					TEMP_INPUT_FILE);
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(tenLinesContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of more than 10
			fileOutStream = new FileOutputStream(tempInpFile, true);
			fileOutStream.write(("line11" + System.getProperty(LINE_SEPARATOR))
					.getBytes());
			fileOutStream.close();
			testInputStream = new FileInputStream(TEMP_INPUT_FILE);
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
	public void testReadFromInputStreamWith0Lines() {
		HeadApp cmdApp = new HeadApp();

		File tempInpFile = null;
		String[] args = new String[2];
		args[0] = "-n";
		args[1] = "0";

		try {
			tempInpFile = new File(TEMP_INPUT_FILE);
			FileOutputStream fileOutStream = new FileOutputStream(tempInpFile);
			String arbitraryContent = "";
			for (int i = 0; i < 10; i++) {
				arbitraryContent += LINE + (i + 1)
						+ System.getProperty(LINE_SEPARATOR);
			}
			fileOutStream.write(arbitraryContent.getBytes());
			fileOutStream.close();

			FileInputStream testInputStream = new FileInputStream(
					TEMP_INPUT_FILE);
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals("", testOutputStream.toString());
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
		String[] args = new String[2];
		args[0] = "-n";
		args[1] = "15";

		try {
			tempInpFile = new File(TEMP_INPUT_FILE);
			FileOutputStream fileOutStream = new FileOutputStream(tempInpFile);
			String lessThanNlines = "";
			for (int i = 0; i < Integer.parseInt(args[1]) - 1; i++) {
				lessThanNlines += LINE + (i + 1)
						+ System.getProperty(LINE_SEPARATOR);
			}
			fileOutStream.write(lessThanNlines.getBytes());
			fileOutStream.close();

			// testing for an input of less than n lines
			FileInputStream testInputStream = new FileInputStream(
					TEMP_INPUT_FILE);
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(lessThanNlines, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of n lines
			String nLinesOfContent = lessThanNlines + LINE
					+ Integer.parseInt(args[1])
					+ System.getProperty(LINE_SEPARATOR);
			fileOutStream = new FileOutputStream(tempInpFile, false);
			fileOutStream.write(nLinesOfContent.getBytes());
			fileOutStream.close();
			testInputStream = new FileInputStream(TEMP_INPUT_FILE);
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(nLinesOfContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of more than n lines
			fileOutStream = new FileOutputStream(tempInpFile, true);
			fileOutStream
					.write((LINE + (Integer.parseInt(args[1]) + 1) + System
							.getProperty(LINE_SEPARATOR)).getBytes());
			fileOutStream.close();
			testInputStream = new FileInputStream(TEMP_INPUT_FILE);
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
			tempInpFile = new File(TEMP_INPUT_FILE);
			FileOutputStream fileOutStream = new FileOutputStream(tempInpFile);
			String tenLinesContent = "";
			for (int i = 0; i < 10; i++) {
				tenLinesContent += LINE + (i + 1)
						+ System.getProperty(LINE_SEPARATOR);
			}
			fileOutStream.write(tenLinesContent.getBytes());
			fileOutStream.close();

			args[0] = TEMP_INPUT_FILE;

			// testing for an input of 10 lines or less
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, null, testOutputStream);
			assertEquals(tenLinesContent, testOutputStream.toString());
			testOutputStream.reset();

			// testing for an input of more than 10
			fileOutStream = new FileOutputStream(tempInpFile, true);
			fileOutStream.write(("line11" + System.getProperty(LINE_SEPARATOR))
					.getBytes());
			fileOutStream.close();
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

			args[0] = TEMP_INPUT_FILE; // this filename does not exist yet
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, null, testOutputStream);
			assertEquals("", testOutputStream.toString());
			fail();

		} catch (AbstractApplicationException e) {
			String expected = "head: can't open 'temp-input-file-name.tmp'. The file could not be found";
			assertEquals(expected,e.getMessage());
		}
	}

	@Test
	public void testNegativeNumLines() throws IOException {
		HeadApp cmdApp = new HeadApp();

		File tempInpFile = null;
		String[] args = new String[2];
		FileInputStream testInputStream = null;

		try {
			tempInpFile = new File(TEMP_INPUT_FILE);
			FileOutputStream fileOutStream = new FileOutputStream(tempInpFile);
			fileOutStream.write("randomData".getBytes());
			fileOutStream.close();

			testInputStream = new FileInputStream("temp-input-file-name.tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			args[0] = "-n";
			args[1] = "-1";
			cmdApp.run(args, testInputStream, testOutputStream);

		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD
					+ Consts.Messages.ILLEGAL_LINE_CNT);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			testInputStream.close();
			if (tempInpFile != null) {
				tempInpFile.delete();
			}
		}
	}

	@Test
	public void testAlphabeticalNumLines() throws IOException {
		HeadApp cmdApp = new HeadApp();

		File tempInpFile = null;
		String[] args = new String[2];
		FileInputStream testInputStream = null;

		try {
			tempInpFile = new File(TEMP_INPUT_FILE);
			FileOutputStream fileOutStream = new FileOutputStream(tempInpFile);
			fileOutStream.write("randomData".getBytes());
			fileOutStream.close();

			testInputStream = new FileInputStream("temp-input-file-name.tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			args[0] = "-n";
			args[1] = "-1";
			cmdApp.run(args, testInputStream, testOutputStream);
			fail();

		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD
					+ Consts.Messages.ILLEGAL_LINE_CNT);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			testInputStream.close();
			if (tempInpFile != null) {
				tempInpFile.delete();
			}
		}
	}

	@Test
	public void testTooManyArguments() {
		HeadApp cmdApp = new HeadApp();

		String[] args = new String[4];
		args[0] = "abc";
		args[1] = "bcd";
		args[2] = "cde";
		args[2] = "def";

		try {
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, null, testOutputStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), HEAD + Consts.Messages.TOO_MANY_ARGS);
		}
	}
}
