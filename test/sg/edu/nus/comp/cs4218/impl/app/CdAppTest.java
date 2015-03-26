package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class CdAppTest {

	private static final String CD_EXPT = "cd: ";
	File tempTestDirectory = null;
	String currentDirectory = "";
	String tempFolder = "TempTest";

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	/*
	 * Create a folder tempTestDirectory so that tests run in an uniform manner
	 * across machines
	 */
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
		Environment.setCurrentDirectory(System.getProperty(Consts.Keywords.USER_DIR));
	}

	/*
	 * Negative test with null arguments
	 */
	@Test
	public void testCdAppWithNullArgument() throws AbstractApplicationException {
		expectedEx.expect(CdException.class);
		expectedEx.expectMessage(CD_EXPT + Consts.Messages.ARG_NOT_NULL);

		CdApp cmdApp = new CdApp();
		cmdApp.run(null, null, System.out);

	}

	/*
	 * Negative test with null output stream
	 */
	@Test
	public void testCdAppWithNullOutputStreamArgument()
			throws AbstractApplicationException {
		expectedEx.expect(CdException.class);
		expectedEx.expectMessage(CD_EXPT + Consts.Messages.OUT_STR_NOT_NULL);

		CdApp cmdApp = new CdApp();
		String[] args = new String[2];
		args[0] = tempFolder;

		cmdApp.run(args, null, null);
	}

	/*
	 * Negative test with many arguments for cd
	 */
	@Test
	public void testCdAppWithMultipleArgs() throws AbstractApplicationException {
		expectedEx.expect(CdException.class);
		expectedEx.expectMessage(CD_EXPT + Consts.Messages.EXPECT_ONE_ARG);

		CdApp cmdApp = new CdApp();
		String[] args = new String[2];
		args[0] = tempFolder;
		args[1] = "dummy";

		cmdApp.run(args, null, System.out);
	}

	/*
	 * Empty args for cd
	 */
	@Test
	public void testCdAppWithoutAnyArgument()
			throws AbstractApplicationException {
		expectedEx.expect(CdException.class);
		expectedEx.expectMessage(CD_EXPT + Consts.Messages.NO_DIR_ENTERED);

		CdApp cmdApp = new CdApp();
		String[] args = {};

		cmdApp.run(args, null, System.out);

	}

	/*
	 * Negative test with invalid directory arguments
	 */
	@Test
	public void testCdWithIncorrectDirectory()
			throws AbstractApplicationException {
		expectedEx.expect(CdException.class);
		expectedEx.expectMessage(CD_EXPT + Consts.Messages.PATH_NOT_FOUND);

		CdApp cmdApp = new CdApp();

		tempTestDirectory.delete();

		String[] args = new String[1];
		args[0] = tempFolder;

		cmdApp.run(args, null, System.out);
	}

	/*
	 * Cd command with positive correct workflow
	 */
	@Test
	public void testCdAppForFolders() throws AbstractApplicationException,
			InvalidDirectoryException, IOException {
		CdApp cmdApp = new CdApp();
		String[] args = new String[1];
		args[0] = tempFolder;

		cmdApp.run(args, null, System.out);
		String directoryAfterCd = Environment.getCurrentDirectory();
		assertEquals(currentDirectory + File.separator + tempFolder,
				directoryAfterCd);
	}

}
