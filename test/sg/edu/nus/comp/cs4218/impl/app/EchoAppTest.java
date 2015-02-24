package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class EchoAppTest {

	@Test
	public void testEchoAppWithNullArgument() {
		EchoApp cmdApp = new EchoApp();
		try {
			cmdApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "echo: " + Consts.Messages.ARG_NOT_NULL);
		}
	}

	@Test
	public void testEchoAppWithNullOutputStream() {
		EchoApp cmdApp = new EchoApp();
		String[] args = new String[2];
		args[0] = "test1";
		args[1] = "test2";

		try {
			cmdApp.run(args, null, null);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "echo: " + Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testEchoAppWithOneArgument() {
		EchoApp cmdApp = new EchoApp();

		File temp = null;
		String[] args = new String[1];
		args[0] = "hello";
		try {
			temp = File.createTempFile("temp-file-name", ".tmp");
			OutputStream fileOutStream = new FileOutputStream(temp);
			cmdApp.run(args, null, fileOutStream);

			BufferedReader buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(temp)));
			assertEquals("hello", buffReader.readLine());
			buffReader.close();

		} catch (AbstractApplicationException e) {
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (temp != null) {
				temp.delete();
			}
		}
	}

	// @Test
	// public void testWithMultipleArguments() {
	// EchoApp cmdApp = new EchoApp();
	//
	// String[] args = new String[1];
	// args[0] = "hello world";
	// // args[1] = "hi there ";
	// try {
	// ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
	// cmdApp.run(args, null, testOutputStream);
	//
	// assertEquals("hello world\n", testOutputStream);
	//
	// } catch (AbstractApplicationException e) {
	// fail();
	// }
	// }

	@Test
	public void testEchoAppWithNoArgument() {
		EchoApp cmdApp = new EchoApp();

		File temp = null;
		String[] args = new String[1];
		try {
			temp = File.createTempFile("temp-file-name", ".tmp");
			ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
			cmdApp.run(args, null, testOutputStream);
			assertEquals(System.getProperty("line.separator"), testOutputStream.toString());
		} catch (AbstractApplicationException e) {
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (temp != null) {
				temp.delete();
			}
		}
	}

}
