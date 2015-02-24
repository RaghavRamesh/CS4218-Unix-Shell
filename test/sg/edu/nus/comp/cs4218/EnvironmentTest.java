package sg.edu.nus.comp.cs4218;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.InvalidFileException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;

public class EnvironmentTest {

	File tempTestDirectory = null;
	String originalCurrDir = "";
	String tempFolder = "TempTest";
	private static final String TEMP_FILE_NAME = "temp-file-name";
	private static final String TMP = ".tmp";

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp() throws Exception {

		boolean status = true ;
		originalCurrDir = Environment.getCurrentDirectory();
		// create a folder named TempTest in current

		tempTestDirectory = new File(originalCurrDir + File.separator
				+ tempFolder);

		if(tempTestDirectory.exists()){
			Environment.deleteFolder(tempTestDirectory); 
		}
		
		status = tempTestDirectory.mkdir();
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

	// negative test
	@Test
	public void testCheckIsDirectoryUsingFile() throws IOException,
			InvalidDirectoryException {
		expectedEx.expect(InvalidDirectoryException.class);
		expectedEx.expectMessage(Consts.Messages.DIR_NOT_VALID);

		File tempFile = File.createTempFile(TEMP_FILE_NAME, TMP);
		tempFile.deleteOnExit();
		Environment.checkIsDirectory(tempFile.getAbsolutePath());
	}

	// negative test
	@Test
	public void testCheckIsDirectoryForInvalidDirectory()
			throws InvalidDirectoryException, IOException {
		expectedEx.expect(InvalidDirectoryException.class);
		expectedEx.expectMessage(Consts.Messages.PATH_NOT_FOUND);

		String invalidDirectory = "~#$randome$@";
		Environment.checkIsDirectory(invalidDirectory);
	}

	// positive test
	@Test
	public void testCheckIsDirectoryForValidAbsoluteDirectory()
			throws InvalidDirectoryException, IOException {

		// create a new directory
		File validDirectory = new File(tempTestDirectory + File.separator
				+ "validDirectory");
		boolean status = validDirectory.mkdir();
		if (!status) {
			fail();
		}

		String dirPath = Environment.checkIsDirectory(validDirectory
				.getAbsolutePath());
		assertEquals(validDirectory.getCanonicalPath(), dirPath);

		validDirectory.delete();
	}
	
	// positive test
	@Test
	public void testCheckIsDirectoryForValidRelativeDirectory()
			throws InvalidDirectoryException, IOException {

		// create a new directory
		File validDirectory = new File(tempTestDirectory + File.separator
				+ "validDirectory");
		boolean status = validDirectory.mkdir();
		if (!status) {
			fail();
		}

		String dirPath = Environment.checkIsDirectory("TempTest/validDirectory");
		assertEquals(validDirectory.getCanonicalPath(), dirPath);

		validDirectory.delete();
	}

	// negative test
	@Test
	public void testCheckIsFileUsingDirectory() throws IOException {

		File dir = new File(this.tempFolder, "validFolder");
		boolean status = dir.mkdir();
		if (!status)
			fail();

		try {
			Environment.checkIsFile("TempTest/validFolder");
			fail();
		} catch (InvalidFileException exception) {
			assertEquals("can't open '" + "TempTest/validFolder" + "'. "
					+ Consts.Messages.FILE_NOT_VALID, exception.getMessage());
		} finally {
			dir.delete();
		}

	}

	// negative test
	@Test
	public void testCheckIsFileForInvalidFile() throws IOException,
			InvalidFileException {
		expectedEx.expect(InvalidFileException.class);
		expectedEx.expectMessage(Consts.Messages.FILE_NOT_FOUND);

		String invalidFile = "~#$randome$@";
		Environment.checkIsFile(invalidFile);
	}

	// positive test
	@Test
	public void testCheckIsFileForValidRelativeFile() throws IOException,
			InvalidFileException {

		// create a valid file
		File validFile = new File(tempTestDirectory + File.separator
				+ "validFile.txt");

		boolean status = validFile.createNewFile();
		if(!status){
			fail();
		}
		String filePath = Environment.checkIsFile("TempTest/validFile.txt");
		assertEquals(validFile.getCanonicalPath(), filePath);

		validFile.delete();
	}
	
	@Test
	public void testCheckIsFileForValidAbsoluteFile() throws IOException,
			InvalidFileException {

		// create a valid file
		File validFile = new File(tempTestDirectory + File.separator
				+ "validFile.txt");

		boolean status = validFile.createNewFile();
		if(!status){
			fail();
		}
		String filePath = Environment.checkIsFile(validFile.getAbsolutePath());
		assertEquals(validFile.getCanonicalPath(), filePath);

		validFile.delete();
	}
}
