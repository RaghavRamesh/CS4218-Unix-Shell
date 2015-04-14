package hackathon;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;

public class HackathonTest {
  private static ByteArrayOutputStream stdout;
  private static String cmdLine; // command line
  private static String expected; // expected output
  private static ShellImplementation shell;
  private static final String NEWLINE = System.lineSeparator();
  private static final String SLASH = System.getProperty("file.separator");

  @Before
  public void setup() throws Exception {
    Environment.currentDirectory = System.getProperty("user.dir")
        + File.separator + "test-hackathon-files";
    shell = new ShellImplementation(null);
    stdout = new ByteArrayOutputStream();
    cmdLine = "";
    expected = "";
  }

  @After
  public void tearDown() throws Exception {
    Environment.currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
  }

  /**
   * The bug is due to incorrect order of command substitution in call command.
   * substituteAll(tokens) should be performed when call command is evaluated,
   * not be constructed.
   */
  @Test
  public void testSeqWithCdAndSubcmd() throws AbstractApplicationException,
      ShellException {
    Environment.currentDirectory = System.getProperty(Consts.Keywords.USER_DIR);
    cmdLine = "cd test-hackathon-files; echo `pwd`";
    shell.parseAndEvaluate(cmdLine, stdout);
    String expected = System.getProperty("user.dir") + File.separator
        + "test-hackathon-files" + NEWLINE;
    assertEquals(expected, stdout.toString());
  }
  
  /**
   * The bug is due to the re-evaluation of content between double quotes in the method value(). 
   * content = parent.substring(begin + 1, end); tokens = Parser.tokenize(content);
   */
  @Test
  public void testParseSingleQuotesInsideDoubleQuotes() throws AbstractApplicationException, ShellException {
    cmdLine = "echo \"'\"";
    shell.parseAndEvaluate(cmdLine, stdout);
    expected = "'" + NEWLINE;
    assertEquals(expected, stdout.toString());
  }
}
