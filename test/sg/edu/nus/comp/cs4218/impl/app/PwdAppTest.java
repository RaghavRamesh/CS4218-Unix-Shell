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

import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class PwdAppTest {

	PwdApp pwdApp;

	@Test
	public void testPwdWithNullOutputStream() {
		pwdApp = new PwdApp();
		try {
			pwdApp.run(null, null, null);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "pwd: "
					+ Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testPwd() {
		pwdApp = new PwdApp();
		File temp = null;
		try {

			temp = File.createTempFile("temp-file-name", ".tmp");
			OutputStream fileOutStream = new FileOutputStream(temp);

			String presentWorkingDir = System
					.getProperty(Consts.Keywords.USER_DIR);

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
