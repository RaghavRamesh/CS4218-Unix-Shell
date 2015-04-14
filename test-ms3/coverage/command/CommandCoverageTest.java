package coverage.command;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;

public class CommandCoverageTest {

  @Test
  public void testEmptyCallCommand() throws ShellException,
      AbstractApplicationException {
    CallCommand cmd = new CallCommand("");
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    cmd.evaluate(null, bao);
    assertEquals(bao.toString(), "");
  }

  @Test
  public void testSubstituteBackquotesEmpty() throws ShellException,
      AbstractApplicationException {
    String result = CallCommand.substituteBackquotes("echo ``");
    String expected = "echo ";
    assertEquals(expected, result);
  }
}
