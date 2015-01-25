package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.DirectoryHelpers;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class CdAppTest {

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
	public void testCdAppWithNullArgument() {
		CdApp cmdApp = new CdApp();
		try {
			cmdApp.run(null, null, System.out);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "cd: " + Consts.Messages.ARG_NOT_NULL);
		}
	}

	@Test
	public void testCdAppWithNullOutputStreamArgument() {
		CdApp cmdApp = new CdApp();
		String[] args = new String[2];
		args[0] = tempFolder;

		try {
			cmdApp.run(args, null, null);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "cd: "
					+ Consts.Messages.OUT_STR_NOT_NULL);
		}
	}

	@Test
	public void testCdAppWithMultipleArgs() {
		CdApp cmdApp = new CdApp();
		String[] args = new String[2];
		args[0] = tempFolder;
		args[1] = "dummy";

		try {
			cmdApp.run(args, null, System.out);
			fail();

		} catch (AbstractApplicationException e) {
			assertEquals("cd: " + Consts.Messages.EXPECT_ONE_ARG,
					e.getMessage());
		}
	}

	@Test
	public void testCdAppWithoutAnyArgument() {
		CdApp cmdApp = new CdApp();
		String[] args = {};

		try {
			cmdApp.run(args, null, System.out);
			fail();

		} catch (AbstractApplicationException e) {
			assertEquals("cd: " + Consts.Messages.NO_DIR_ENTERED,
					e.getMessage());
		}
	}

	@Test
	public void testCdWithIncorrectDirectory() {
		CdApp cmdApp = new CdApp();

		tempTestDirectory.delete();

		String[] args = new String[1];
		args[0] = tempFolder;

		try {
			cmdApp.run(args, null, System.out);
			fail();

		} catch (AbstractApplicationException e) {
			assertEquals("cd: " + Consts.Messages.PATH_NOT_FOUND,
					e.getMessage());
		}
	}

	@Test
	public void testCdAppForFolders() {
		CdApp cmdApp = new CdApp();
		String[] args = new String[1];
		args[0] = tempFolder;

		try {
			cmdApp.run(args, null, System.out);
			String directoryAfterCd = DirectoryHelpers.getCurrentDirectory();
			assertEquals(currentDirectory + File.separator + tempFolder,
					directoryAfterCd);

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidDirectoryException e) {
			e.printStackTrace();
			fail();
		}
	}

}
