package sg.edu.nus.comp.cs4218.impl.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class CallCommandTest {
  private static final String WRONG_EXCEPTION = "Wrong exception thrown";
  private static final String SHELL_STR = "shell: ";
  private static final String SHOULD_THROW = "Exception should be thrown";

  @Before
  public void setUp() {
    // In case of creating new files, need to clean and setup again
  }

  @After
  public void tearDown() {
    // In case of creating new files, need to clean and setup again
  }

  @Test
  public void testCallCommandWithTooManyOutputRedirection() {
    String cmdLine = "pwd test > a.txt >b.txt";
    try {
      new CallCommand(cmdLine).evaluate(System.in, System.out);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.TOO_MANY_OUTPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void testCallCommandWithNoOutputRedirectionProvided() {
    String cmdLine = "pwd test >";
    try {
      new CallCommand(cmdLine).evaluate(System.in, System.out);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.INVALID_OUTPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void testCallCommandWithInvalidOutputRedirectionProvided() {
    String cmdLine = "pwd test > < a.txt";
    try {
      new CallCommand(cmdLine).evaluate(System.in, System.out);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.INVALID_OUTPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void testCallCommandWithTooManyInputRedirection() {
    String cmdLine = "pwd test < a.txt <b.txt";
    try {
      new CallCommand(cmdLine).evaluate(System.in, System.out);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.TOO_MANY_INPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void testCallCommandWithNoInputRedirectionProvided() {
    String cmdLine = "pwd test <";
    try {
      new CallCommand(cmdLine).evaluate(System.in, System.out);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.INVALID_INPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void testCallCommandWithInvalidInputRedirectionProvided() {
    String cmdLine = "pwd test < <";
    try {
      new CallCommand(cmdLine).evaluate(System.in, System.out);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.INVALID_INPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void testFindInput() throws ShellException,
      AbstractApplicationException {
    List<String> tokens = Arrays.asList("ls", "<", "d.txt");
    assertEquals("d.txt", CallCommand.findInput(tokens));
  }
  
  @Test
  public void testFindInputReturnNull() throws ShellException,
      AbstractApplicationException {
    List<String> tokens = Arrays.asList("ls", "a.txt");
    assertEquals(null, CallCommand.findInput(tokens));
  }
  
  @Test
  public void testFindInputWithTooManyInput() throws ShellException,
      AbstractApplicationException {
    try {
      List<String> tokens = Arrays.asList("ls", "<", "a.txt", "and", "<", "c.txt");
      CallCommand.findInput(tokens);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.TOO_MANY_INPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }
  
  @Test
  public void testFindInputWithInvalidInput() throws ShellException,
      AbstractApplicationException {
    try {
      List<String> tokens = Arrays.asList("ls", "<", ";");
      CallCommand.findInput(tokens);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.INVALID_INPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void testFindOutput() throws ShellException,
      AbstractApplicationException {
    List<String> tokens = Arrays.asList("wc", "haha", ">", "b2.txt");
    assertEquals("b2.txt", CallCommand.findOutput(tokens));
  }
  
  @Test
  public void testFindOutputReturnNull() throws ShellException,
      AbstractApplicationException {
    List<String> tokens = Arrays.asList("echo", "hello", "b1.txt");
    assertEquals(null, CallCommand.findOutput(tokens));
  }
  
  @Test
  public void testFindOutputWithTooManyOutput() throws ShellException,
      AbstractApplicationException {
    try {
      List<String> tokens = Arrays.asList("echo", "hello", ">", "d1.txt", ">", "d2.txt");
      CallCommand.findOutput(tokens);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.TOO_MANY_OUTPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }
  
  @Test
  public void testFindOutputWithInvalidInput() throws ShellException,
      AbstractApplicationException {
    try {
      List<String> tokens = Arrays.asList("ls", ">");
      CallCommand.findOutput(tokens);
      fail(SHOULD_THROW);
    } catch (ShellException e) {
      String expected = SHELL_STR + Consts.Messages.INVALID_OUTPUT;
      assertEquals(e.getMessage(), expected);
    } catch (Exception e) {
      fail(WRONG_EXCEPTION);
    }
  }

  @Test
  public void testFindArguments() throws ShellException,
      AbstractApplicationException {
    String cmdLine = "< a.txt echo hello > b.txt world";
    List<String> expected = Arrays.asList("echo", "hello", "world");
    List<String> actual = new CallCommand(cmdLine).findArguments();
    assertEquals(expected, actual);
  }
}
