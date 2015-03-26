package grep_with_pipe_tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.TestHelper;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.HeadException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;

public class HeadGrepSedTest {
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
  
  @Test
  public void chainSuccess() throws AbstractApplicationException, ShellException {
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    shell.parseAndEvaluate("head -n 1 sample.txt | grep \"9000\" | sed s/over/lower/", bao);
    String expected = "The power is lower 9000 !" + System.lineSeparator();
    assertEquals(expected, bao.toString());
  }
  
  @Test(expected = HeadException.class)
  public void chainFailHeadWrongFile() throws AbstractApplicationException, ShellException {
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    shell.parseAndEvaluate("head -n 1 haha.txt | grep \"9000\" | sed s/over/lower/", bao);
  }
  
  @Test
  public void chainFailGrepNothing() throws AbstractApplicationException, ShellException {
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    shell.parseAndEvaluate("head -n 1 sample.txt | grep \"nonsense\" | sed s/over/lower/", bao);
    assertEquals(System.lineSeparator(), bao.toString());
  }
}
