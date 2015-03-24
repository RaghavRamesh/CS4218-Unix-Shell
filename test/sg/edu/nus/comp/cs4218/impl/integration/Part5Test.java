package sg.edu.nus.comp.cs4218.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
  private static final String WRONG_EXCEPTION = "Wrong exception thrown";

  private static Shell shell;
  private static String folderPath = System.getProperty("user.dir")
      + "/test-files-integration";
  private static String tmpFolderPath = folderPath + "/tmp";

  @Before
  public void setUp() throws Exception {
    Environment.currentDirectory = folderPath;
    (new File(tmpFolderPath)).mkdir();
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

  // Positive tests

  @Test
  public void outputRedirectionToFile() throws AbstractApplicationException,
      ShellException, IOException {
    cdTempFolder();
    shell.parseAndEvaluate("echo hello > test.txt", System.out);
    String val = TestHelper.readFile(tmpFolderPath + "/test.txt");
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
  public void changeDirAndFind() throws AbstractApplicationException,
      ShellException {
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    shell.parseAndEvaluate("cd tmp; cd ..; find sam*.txt", bao);
    String expected = "sample.txt" + System.lineSeparator();
    assertEquals(expected, bao.toString());
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

  @Test
  public void writeToFileThenOverwrite() throws AbstractApplicationException,
      ShellException, IOException {
    cdTempFolder();
    shell.parseAndEvaluate("echo apple > file.txt; echo banana > file.txt",
        System.out);
    String val = TestHelper.readFile(tmpFolderPath + "/file.txt");
    String expected = "banana" + System.lineSeparator();
    assertEquals(expected, val);
  }

  @Test
  public void writeToFileThenFind() throws AbstractApplicationException,
      ShellException {
    cdTempFolder();
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    shell.parseAndEvaluate(
        "echo apple > a.txt; echo banana > b.txt; find *.txt", bao);
    String expected = "a.txt" + System.lineSeparator() + "b.txt"
        + System.lineSeparator();
    assertEquals(expected, bao.toString());
  }

  // Negative tests

  @Test
  public void outputToFileFailure() {
    cdTempFolder();
    try {
      shell.parseAndEvaluate("''; echo hello > file.txt", System.out);
    } catch (ShellException e) {
      assertEquals(false, (new File(tmpFolderPath + "/file.txt")).exists());
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void changeDirFailButStillWrite() throws IOException {
    try {
      shell.parseAndEvaluate("cd tmp; echo haha > 1.txt; ''", System.out);
    } catch (ShellException e) {
      String val = TestHelper.readFile(tmpFolderPath + "/1.txt");
      String expected = "haha" + System.lineSeparator();
      assertEquals(expected, val);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void changeDirAndFindFail() {
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    try {
      shell.parseAndEvaluate("cd tmp; cd nonsense; find sam*.txt", bao);
    } catch (AbstractApplicationException e) {
      assertEquals("", bao.toString());
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void writeToFileThenReadFileFail() throws IOException,
      AbstractApplicationException, ShellException {
    cdTempFolder();
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    try {
      shell.parseAndEvaluate("echo new > file.txt; cat file.txt; cd abcd", bao);
    } catch (AbstractApplicationException e) {
      String expected = "new" + System.lineSeparator();
      assertEquals(expected, bao.toString());
    } catch (Exception e) {
      System.out.println(e);
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void writeToFileThenOverwriteFail() throws IOException {
    cdTempFolder();
    try {
      shell
          .parseAndEvaluate(
              "echo old > file.txt; cd nonsense;echo banana > file.txt",
              System.out);
    } catch (AbstractApplicationException e) {
      String val = TestHelper.readFile(tmpFolderPath + "/file.txt");
      String expected = "old" + System.lineSeparator();
      assertEquals(expected, val);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void writeToFileThenFindFail() throws AbstractApplicationException,
      ShellException {
    cdTempFolder();
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    try {
      shell.parseAndEvaluate(
          "echo apple > a.txt; echo banana > < b.txt; find *.txt", bao);
    } catch (ShellException e) {
      String expected = "";
      assertEquals(expected, bao.toString());
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }
}
