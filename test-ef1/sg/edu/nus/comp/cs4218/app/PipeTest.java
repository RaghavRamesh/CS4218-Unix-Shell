package sg.edu.nus.comp.cs4218.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayOutputStream;


public class PipeTest {

    private static Shell shell;
    private static ByteArrayOutputStream stdout;
    String[] args;

    @Before
    public void setUp() throws Exception {
        Environment.currentDirectory = System.getProperty("user.dir") + "/test-files-ef1";
        shell = null; //TODO change here
    }

    @Test
    public void testPipeCommandValid() throws AbstractApplicationException, ShellException {
        String cmdLine = "echo foo bar | grep b";
        stdout = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdLine, stdout);

        String expected = "foo bar" + System.lineSeparator();
        Assert.assertEquals(expected, stdout.toString());
    }

    @Test
    public void testPipeCommandValidFromFile() throws AbstractApplicationException, ShellException {
        String cmdLine = "cat sca110p.txt339 | grep c";
        stdout = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdLine, stdout);

        String expected = "The name \"scallop\" comes from the Old French escalope, which means \"shell\"."
                + System.lineSeparator()
                + "Their shells can be up to 15 centimetres (6 inches) across."
                + System.lineSeparator();
        Assert.assertEquals(expected, stdout.toString());
    }


    @Test(expected = ShellException.class)
    public void testPipeCommandInvalidFirstChild() throws AbstractApplicationException, ShellException {
        String cmdLine = "foo | grep foo";
        stdout = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdLine, stdout);
    }

    @Test(expected = GrepException.class)
    public void testPipeCommandInvalidFirstChildExecution() throws AbstractApplicationException, ShellException {
        String cmdLine = "grep foo | grep foo";
        stdout = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdLine, stdout);
    }

    @Test
    public void testPipeCommandInvalidFirstChildNoOutput() throws AbstractApplicationException, ShellException {
        String cmdLine = "cd .. | grep foo";
        stdout = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdLine, stdout);

        String expected = "" + System.lineSeparator();
        Assert.assertEquals(expected, stdout.toString());
    }


}
