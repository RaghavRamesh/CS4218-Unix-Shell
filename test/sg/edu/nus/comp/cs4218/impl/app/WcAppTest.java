package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.FileCreateException;
import sg.edu.nus.comp.cs4218.exception.InvalidDirectoryException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcAppTest {

	private static final String WCEXP = "wc: ";
	private static final String TMP = ".tmp";
	private static final String TEMP_FILE_INPUT = "temp-file-name-input";
	File tempTestDirectory = null;
	String currentDirectory = "";
	String tempFolder = "TempTest";

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		currentDirectory = Environment.getCurrentDirectory();
		// create a folder named TempTest in current

		tempTestDirectory = new File(currentDirectory + File.separator
				+ tempFolder);
		
		if(tempTestDirectory.exists()){
			Environment.deleteFolder(tempTestDirectory);
		}
		
		boolean status = tempTestDirectory.mkdir();
		if (!status) {
			fail();
		}
		
	//	Environment.setCurrentDirectory(tempTestDirectory.getCanonicalPath());
	}

	@After
	public void tearDown() throws Exception {
		if (tempTestDirectory == null) {
			fail();
		}

		// Delete the temporary folder and change the current Directory to
		// previous case
		tempTestDirectory.delete();
		Environment.setCurrentDirectory(currentDirectory);
	}

	@Test
	public void testWcAppWithNullArgument() throws AbstractApplicationException {

		expectedEx.expect(WcException.class);
		expectedEx.expectMessage(WCEXP + Consts.Messages.ARG_NOT_NULL);

		WcApp cmdApp = new WcApp();
		cmdApp.run(null, null, System.out);
	}

	@Test
	public void testWcAppWithNullOutputStreamArgument()
			throws AbstractApplicationException {

		expectedEx.expect(WcException.class);
		expectedEx.expectMessage(WCEXP + Consts.Messages.OUT_STR_NOT_NULL);

		WcApp cmdApp = new WcApp();
		String[] args = new String[2];
		args[0] = tempFolder;

		cmdApp.run(args, null, null);
	}

	@Test
	public void testWcAppWithoutAnyArgumentAndNullIputStream()
			throws AbstractApplicationException {
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage(WCEXP + Consts.Messages.INP_STR_NOT_NULL);

		WcApp cmdApp = new WcApp();
		String[] args = {};

		cmdApp.run(args, null, System.out);
	}

	@Test
	public void testWcAppWithoutAnyArgument() {

		WcApp cmdApp = new WcApp();
		String[] args = {};
		File tempInput = null;
		File tempOutput = null;
		OutputStream fileOutStream = null;

		try {
			tempInput = File.createTempFile(TEMP_FILE_INPUT, TMP);
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for Wc");
			writer.close();
			fileOutStream.close();

			InputStream fileInputStream = new FileInputStream(tempInput);

			tempOutput = File.createTempFile("temp-file-name", TMP);
			fileOutStream = new FileOutputStream(tempOutput);

			cmdApp.run(args, fileInputStream, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(tempOutput)));
			assertEquals("67 14 1 ", buffReader.readLine());
			buffReader.close();

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (tempInput != null) {
				tempInput.delete();
			}
			if (tempOutput != null) {
				tempOutput.delete();
			}
		}
	}

	@Test
	public void testWcAppWithOnlyWordCount() {
		WcApp cmdApp = new WcApp();
		String[] args = { "-w" };
		File tempInput = null;
		File tempOutput = null;
		OutputStream fileOutStream = null;

		try {
			tempInput = File.createTempFile(TEMP_FILE_INPUT, TMP);
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for Wc");
			writer.close();
			fileOutStream.close();

			InputStream fileInputStream = new FileInputStream(tempInput);

			tempOutput = File.createTempFile("temp-file-name", TMP);
			fileOutStream = new FileOutputStream(tempOutput);

			cmdApp.run(args, fileInputStream, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(tempOutput)));
			assertEquals("14 ", buffReader.readLine());
			buffReader.close();

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (tempInput != null) {
				tempInput.delete();
			}
			if (tempOutput != null) {
				tempOutput.delete();
			}
		}

	}

	@Test
	public void testWcAppWithCountFromFile() {

		WcApp cmdApp = new WcApp();
		String[] args = { "-w", "-l", TEMP_FILE_INPUT };
		File tempInput = null;
		File tempOutput = null;
		OutputStream fileOutStream = null;

		try {
			tempInput = Environment.createFile(TEMP_FILE_INPUT);
			fileOutStream = new FileOutputStream(tempInput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fileOutStream));
			writer.write("Hi, This is a string to test input from input stream reader for Wc");
			writer.close();
			fileOutStream.close();

			tempOutput = File.createTempFile("temp-file-name", TMP);
			fileOutStream = new FileOutputStream(tempOutput);

			cmdApp.run(args, null, fileOutStream);

			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(tempOutput)));
			assertEquals("14 1 temp-file-name-input", buffReader.readLine());
			buffReader.close();

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
			fail();
		} catch (FileCreateException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (tempInput != null) {
				tempInput.delete();
			}
			if (tempOutput != null) {
				tempOutput.delete();
			}
		}
	}
	
	@Test
	public void testReadAndProcessLinesInFileWithEmptyLine() throws IOException{
		WcApp cmdApp = new WcApp();
		
		ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new BufferedOutputStream(bOutStream));
		writer.println();
		writer.close();
		
		byte[] byteArray = bOutStream.toByteArray();
		
		ByteArrayInputStream bInStream = new ByteArrayInputStream(byteArray);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(bInStream));
		cmdApp.readAndProcessLinesInReader(reader);
		
		assertEquals(1,cmdApp.bytesLength);
		assertEquals(0,cmdApp.wordsLength);
		assertEquals(1,cmdApp.lineLength);
	}
	
	@Test
	public void testReadAndProcessLinesInFileWithOneLineAndSpaces() throws IOException{
		WcApp cmdApp = new WcApp();
		
		ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new BufferedOutputStream(bOutStream));
		writer.println(" Hey this is a line with spaces! ");
		writer.close();
		
		byte[] byteArray = bOutStream.toByteArray();
		
		ByteArrayInputStream bInStream = new ByteArrayInputStream(byteArray);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(bInStream));
		cmdApp.readAndProcessLinesInReader(reader);
		
		assertEquals(34,cmdApp.bytesLength);
		assertEquals(7,cmdApp.wordsLength);
		assertEquals(1,cmdApp.lineLength);
		
		assertEquals(34,cmdApp.totalBytes);
		assertEquals(7,cmdApp.totalWordsLength);
		assertEquals(1,cmdApp.totalLineLength);
	}
	
	@Test
	public void testReadAndProcessLinesInFileWithMultiLineAndSpaces() throws IOException{
		WcApp cmdApp = new WcApp();
		
		ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new BufferedOutputStream(bOutStream));
		writer.println(" Hey this is a line with spaces ! ");
		writer.println(" Mysecond line has special charaters");
		writer.println();
		writer.close();
		
		byte[] byteArray = bOutStream.toByteArray();
		
		ByteArrayInputStream bInStream = new ByteArrayInputStream(byteArray);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(bInStream));
		cmdApp.readAndProcessLinesInReader(reader);
		
		assertEquals(73,cmdApp.bytesLength);
		assertEquals(13,cmdApp.wordsLength);
		assertEquals(3,cmdApp.lineLength);
		
		assertEquals(73,cmdApp.totalBytes);
		assertEquals(13,cmdApp.totalWordsLength);
		assertEquals(3,cmdApp.totalLineLength);
	}
	
	@Test
	public void testProcessCountFromFilesForNoInputFileNames() throws WcException, FileNotFoundException, InvalidDirectoryException, IOException{
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage(WCEXP + Consts.Messages.ARG_NOT_NULL);
		
		ArrayList<String> filePaths = new ArrayList<String>();
		
		WcApp cmdApp = new WcApp();
		cmdApp.processCountFromFiles(null,filePaths);
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testProcessCountFromFilesForInvalidFileNames() throws WcException, FileNotFoundException, InvalidDirectoryException, IOException{
				
		ArrayList<String> fileNames = new ArrayList<String>();
		ArrayList<String> filePaths = new ArrayList<String>();
		
		fileNames.add("invalidFileName#@#$$%");
		filePaths.add("/Z:/abcdd");
		
		File invalidFile = new File("invalidFileName#@#$$%");
		if(invalidFile.exists())
			invalidFile.delete();
		
		WcApp cmdApp = new WcApp();
		cmdApp.processCountFromFiles(fileNames,filePaths);
	}
	
	@Test
	public void testProcessCountFromFilesForValidFileNames(){
		File newFile1 = null;
		File newFile2 = null; 
		
		ArrayList<String> filePaths = new ArrayList<String>();
		ArrayList<String> fileNames = new ArrayList<String>();
		
		try{
			
		newFile1 = new File(this.tempTestDirectory.getAbsoluteFile() + File.separator + "TestFile1.txt");
		newFile2 = new File(this.tempTestDirectory.getAbsoluteFile() + File.separator + "TestFile2.txt");

		filePaths.add(newFile1.getCanonicalPath());
		filePaths.add(newFile2.getCanonicalPath());
		
		newFile1.createNewFile();
		newFile2.createNewFile();
		
		PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(newFile1)));
		writer.write(" This is test code for file 1");
		writer.close();
		
		writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(newFile2)));
		writer.write("This is a test code for file 2!");
		writer .close();
		
		fileNames.add("/TempTest/TestFile1.txt");
		fileNames.add("/TempTest/TestFile2.txt");
		
		WcApp cmdApp = new WcApp();
		cmdApp.processCountFromFiles(fileNames,filePaths);
		
		assertEquals(62,cmdApp.totalBytes);
		assertEquals(2,cmdApp.totalLineLength);
		assertEquals(15,cmdApp.totalWordsLength);
		
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}finally{
			if(newFile1!= null)
				newFile1.delete();
			
			if(newFile2 != null)
				newFile2.delete();
		}
	}
}
