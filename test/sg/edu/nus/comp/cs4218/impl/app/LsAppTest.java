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
import sg.edu.nus.comp.cs4218.exception.LsException;

public class LsAppTest {

	private static final String TMP = ".tmp";
	private static final String TEMP_FILE_NAME = "temp-file-name";
	LsApp lsApp = null;
	File tempTestDirectory = null;
	String originalCurrDir = "";
	String tempFolder = "TempTest";

	// used for those exception where we need to test message
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp() throws Exception {

		Environment.currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
		// create a folder named TempTest in current

		 originalCurrDir = Environment.getCurrentDirectory(); 
		tempTestDirectory = new File(originalCurrDir + File.separator
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
		Environment.setCurrentDirectory(originalCurrDir);
	}

	/*
	 * Test with null output stream
	 */
	@Test
	public void testLsWithNullOutputStream()
			throws AbstractApplicationException {

		expectedEx.expect(LsException.class);
		expectedEx.expectMessage("ls: " + Consts.Messages.OUT_STR_NOT_NULL);

		String[] args = new String[1];
		args[0] = tempFolder;

		lsApp = new LsApp();
		lsApp.run(args, null, null);

	}

	/*
	 * Test with null argument
	 */
	@Test
	public void testLsAppWithNullArgument() throws AbstractApplicationException {

		expectedEx.expect(LsException.class);
		expectedEx.expectMessage("ls: " + Consts.Messages.ARG_NOT_NULL);

		LsApp cmdApp = new LsApp();
		cmdApp.run(null, null, System.out);
	}

	@Test
	public void testLsWithMoreThanOneArg() {

		String[] args = new String[2];
		args[0] = tempFolder;
		args[1] = tempFolder;
		File temp = null;

		lsApp = new LsApp();
		try {
			temp = File.createTempFile(TEMP_FILE_NAME, TMP);
			OutputStream fileOutStream = new FileOutputStream(temp);
			lsApp.run(args, null, fileOutStream);
			fail();
		} catch (AbstractApplicationException e) {
			assertEquals(e.getMessage(), "ls: "
					+ Consts.Messages.EXPECT_ONE_ARG);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (temp != null) {
				temp.delete();
			}
		}
	}

	@Test
	public void testLsWithNoFilesInPath() {
		lsApp = new LsApp();

		File temp = null;
		String[] args = new String[1];
		args[0] = tempFolder;

		try {
			temp = File.createTempFile(TEMP_FILE_NAME, TMP);
			OutputStream fileOutStream = new FileOutputStream(temp);
			lsApp.run(args, null, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(temp)));
			assertEquals("", buffReader.readLine());
			buffReader.close();

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
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

	@Test
	public void testLsWithFilesAndDirsInPath() {
		lsApp = new LsApp();

		File temp = null;
		File newFile1 = null;
		File newFile2 = null;
		File newFile3 = null;
		File newFile4 = null;

		String[] args = new String[1];
		args[0] = tempFolder;

		try {
			temp = File.createTempFile(TEMP_FILE_NAME, TMP);
			OutputStream fileOutStream = new FileOutputStream(temp);

			String currentDir = tempTestDirectory.getAbsolutePath();
			newFile1 = new File(currentDir + File.separator + "test1.txt");
			newFile2 = new File(currentDir + File.separator + "test2.xyz");
			newFile3 = new File(currentDir + File.separator + "subdir1");
			newFile4 = new File(currentDir + File.separator + "subdir2");

			newFile1.createNewFile();
			newFile2.createNewFile();
			newFile3.mkdir();
			newFile4.mkdir();

			lsApp.run(args, null, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(temp)));
			checkAssertionsForIdealFileNamesAndDirs(buffReader);
			buffReader.close();

		} catch (AbstractApplicationException e) {
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {

			deleteIdealFiles(temp, newFile1, newFile2, newFile3, newFile4);
		}
	}

	/*
	 * Test for current path if arguments not specified
	 */
	@Test
	public void testLsWithoutArgs() throws InvalidDirectoryException,
			IOException {

		// change the current directory to make it consistent across machines
		Environment.setCurrentDirectory(tempTestDirectory.getAbsolutePath());

		lsApp = new LsApp();

		File temp = null;
		File newFile1 = null;
		File newFile2 = null;
		File newFile3 = null;
		File newFile4 = null;

		try {
			temp = File.createTempFile(TEMP_FILE_NAME, TMP);
			OutputStream fileOutStream = new FileOutputStream(temp);

			String currentDir = tempTestDirectory.getAbsolutePath();
			newFile1 = new File(currentDir + File.separator + "test1.txt");
			newFile2 = new File(currentDir + File.separator + "test2.xyz");
			newFile3 = new File(currentDir + File.separator + "subdir1");
			newFile4 = new File(currentDir + File.separator + "subdir2");

			newFile1.createNewFile();
			newFile2.createNewFile();
			newFile3.mkdir();
			newFile4.mkdir();

			String[] args = {};

			lsApp.run(args, null, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(temp)));
			checkAssertionsForIdealFileNamesAndDirs(buffReader);
			buffReader.close();

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {

			deleteIdealFiles(temp, newFile1, newFile2, newFile3, newFile4);

			// change the current directory to original current directory
			Environment.setCurrentDirectory(originalCurrDir);

		}
	}

	/*
	 * Test for files that start with dot
	 */
	@Test
	public void testLsWithWithDotInFileNamesPath() {
		lsApp = new LsApp();

		File temp = null;
		File newFile1 = null;
		File newFile2 = null;

		String[] args = new String[1];
		args[0] = tempFolder;

		try {
			temp = File.createTempFile(TEMP_FILE_NAME, TMP);
			OutputStream fileOutStream = new FileOutputStream(temp);

			String currentDir = tempTestDirectory.getAbsolutePath();
			newFile1 = new File(currentDir + File.separator + ".test1");
			newFile2 = new File(currentDir + File.separator + ".subdir1");

			newFile1.createNewFile();
			newFile2.mkdir();

			lsApp.run(args, null, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(temp)));
			assertEquals("", buffReader.readLine());
			buffReader.close();

		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {

			if (temp != null) {
				temp.delete();
			}

			if (newFile1 != null) {
				newFile1.delete();
			}

			if (newFile2 != null) {
				newFile2.delete();
			}
		}
	}

	/*
	 * Helper method for deleting files
	 */
	private void deleteIdealFiles(File temp, File newFile1, File newFile2,
			File newFile3, File newFile4) {
		if (temp != null) {
			temp.delete();
		}

		if (newFile1 != null) {
			newFile1.delete();
		}

		if (newFile2 != null) {
			newFile2.delete();
		}

		if (newFile3 != null) {
			newFile3.delete();
		}

		if (newFile4 != null) {
			newFile4.delete();
		}
	}

	/*
	 * Helper method for checking file names and directory names
	 */
	private void checkAssertionsForIdealFileNamesAndDirs(
			BufferedReader buffReader) throws IOException {
		assertEquals("subdir1" + "\t" + "subdir2" + "\t" + "test1.txt" + "\t"
				+ "test2.xyz" + "\t", buffReader.readLine());
	}

}
