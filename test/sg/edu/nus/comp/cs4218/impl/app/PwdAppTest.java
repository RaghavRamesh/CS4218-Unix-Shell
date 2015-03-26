package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;
import sg.edu.nus.comp.cs4218.exception.PwdException;

public class PwdAppTest {

	PwdApp pwdApp;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp(){
		Environment.currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
	}
	
	@After
	public void tearDown(){
		Environment.currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
	}

	/*
	 * Test for null output stream.
	 */
	@Test
	public void testPwdWithNullOutputStream()
			throws AbstractApplicationException {

		expectedEx.expect(PwdException.class);
		expectedEx.expectMessage("pwd: " + Consts.Messages.OUT_STR_NOT_NULL);

		pwdApp = new PwdApp();
		pwdApp.run(null, null, null);
	}

	/*
	 * We don't test for null input stream because it is okay for it to be null
	 * and has been clubbed with this positive test
	 */
	@Test
	public void testPwd() throws InvalidDirectoryException {
		pwdApp = new PwdApp();
		File temp = null;
		try {

			temp = File.createTempFile("temp-file-name", ".tmp");
			OutputStream fileOutStream = new FileOutputStream(temp);

			String presentWorkingDir = Environment.getCurrentDirectory();

			pwdApp.run(null, null, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(temp)));
			assertEquals(buffReader.readLine(), presentWorkingDir);
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

}
