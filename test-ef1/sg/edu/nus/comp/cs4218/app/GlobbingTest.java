package sg.edu.nus.comp.cs4218.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayOutputStream;


public class GlobbingTest {

    private static Shell shell;
    private static ByteArrayOutputStream stdout;
    String[] args;

    @Before
    public void setUp() throws Exception {
        Environment.currentDirectory = System.getProperty("user.dir") + "/test-files-ef1";
        shell = null; //TODO change here
    }

    @Test
    public void returnOriginalArgWhenGlobbingHasNoFileMatch() throws AbstractApplicationException, ShellException {
        String cmdLine = "echo no*matches";
        stdout = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdLine, stdout);

        String expected = "no*matches" + System.lineSeparator();
        Assert.assertEquals(expected, stdout.toString());
    }


    @Test
    public void returnOriginalArgWhenFileNameIsBetweenDirectories() throws AbstractApplicationException, ShellException {
        String cmdLine = "echo oyster1337/*/mussel7715";
        stdout = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdLine, stdout);

        String expected = "oyster1337/*/mussel7715"  + System.lineSeparator();
        Assert.assertEquals(expected, stdout.toString());
    }


    @Test
    public void returnExpandedArgsWhenGlobbingHasMatches() throws AbstractApplicationException, ShellException {
        String cmdLine = "echo *.txt13*";
        stdout = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdLine, stdout);

        String expected = "5callop.txt133 5callop.txt139" + System.lineSeparator();

        Assert.assertEquals(expected, stdout.toString());
    }

    @Test
    public void returnBothFilesAndDirectoriesAlphabeticallyWhenMatched() throws AbstractApplicationException, ShellException {
        String cmdLine = "echo *";

        stdout = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdLine, stdout);

        String expected = "5callop.txt133 5callop.txt139 clam1533 oyster1337 sca110p.txt339" + System.lineSeparator();

        Assert.assertEquals(expected, stdout.toString());
    }

}
