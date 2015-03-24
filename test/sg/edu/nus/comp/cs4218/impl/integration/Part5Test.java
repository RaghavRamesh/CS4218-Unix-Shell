package sg.edu.nus.comp.cs4218.impl.integration;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.TestHelper;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;

public class Part5Test {

  private static Shell shell;
  private static String folderPath = System.getProperty("user.dir")
      + "/test-files-integration";
  private static String tmpFolderPath = folderPath + "/tmp";

  @Before
  public void setUp() throws Exception {
    Environment.currentDirectory = folderPath;
    shell = new ShellImplementation(null);
  }

  @After
  public void tearDown() throws Exception {
    File tmpFolder = new File(tmpFolderPath);
    TestHelper.purgeDirectory(tmpFolder);
  }

  void cdTempFolder() {
    Environment.currentDirectory = tmpFolderPath;
  }

  // Successful tests

  @Test
  public void outputRedirectionToFile() throws AbstractApplicationException,
      ShellException, IOException {
    cdTempFolder();
    shell.parseAndEvaluate("echo hello > file.txt", System.out);
    String val = TestHelper.readFile(tmpFolderPath + "/file.txt");
    String expected = "hello" + System.lineSeparator();
    assertEquals(expected, val);
  }

  @Test
  public void changeDirAndWriteToFile() throws AbstractApplicationException,
      ShellException, IOException {
    shell.parseAndEvaluate("cd tmp; echo haha > 1.txt", System.out);
    String val = TestHelper.readFile(tmpFolderPath + "/1.txt");
    String expected = "haha" + System.lineSeparator();
    assertEquals(expected, val);
  }

  @Test
  public void writeToFileThenReadFile() throws IOException,
      AbstractApplicationException, ShellException {
    cdTempFolder();
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    shell.parseAndEvaluate("echo apple > file.txt; cat file.txt", bao);
    String expected = "apple" + System.lineSeparator();
    assertEquals(expected, bao.toString());
  }
}
