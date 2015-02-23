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
	@Test
	public void testNullArgument() {
		TailApp cmdApp = new TailApp();
		try {
			cmdApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "tail: " + Consts.Messages.ARG_NOT_NULL);
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
			assertEquals(e.getMessage(), "tail: " + Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testReadFromInputStream() {
		TailApp cmdApp = new TailApp();

		File tempInpFile = null;
		File tempOutFile = null;
		String[] args = new String[0];

		try {
			tempInpFile = new File("temp-input-file-name.tmp");
			FileOutputStream testInpFileOutStream = new FileOutputStream(tempInpFile);
			String tenLinesContent = "";
			for (int i = 0; i < 10; i++) {
				tenLinesContent += "line" + (i + 1) + "\n";
			}
			testInpFileOutStream.write(tenLinesContent.getBytes());
			testInpFileOutStream.close();

			// testing for an input of 10 lines or less
			FileInputStream testInputStream = new FileInputStream("temp-input-file-name.tmp");
			tempOutFile = File.createTempFile("temp-output-file-name", ".tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, testInputStream, testOutputStream);
			assertEquals(tenLinesContent, testOutputStream.toString());
			testInputStream.close();
			testOutputStream.reset();

			// testing for an input of more than 10
			// testInpFileOutStream = new FileOutputStream(tempInpFile, true);
			// testInpFileOutStream.write("line11\n".getBytes());
			// testInpFileOutStream.close();
			// testInputStream = new FileInputStream("temp-input-file-name.tmp");
			// cmdApp.run(args, testInputStream, testOutputStream);
			// assertEquals(tenLinesContent, testOutputStream.toString());
			// testInputStream.close();
			// testOutputStream.reset();

		} catch (AbstractApplicationException e) {
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (tempInpFile != null) {
				tempInpFile.delete();
			}
			if (tempOutFile != null) {
				tempOutFile.delete();
			}
		}
	}

}
