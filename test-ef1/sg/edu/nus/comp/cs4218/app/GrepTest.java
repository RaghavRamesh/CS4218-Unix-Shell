package sg.edu.nus.comp.cs4218.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class GrepTest {
    private ByteArrayOutputStream stdout;
    private Application app;

    @Before
    public void setUp() throws Exception {
        Environment.currentDirectory = System.getProperty("user.dir") + "/test-files-ef1/clam1533";
        stdout = new ByteArrayOutputStream();
        app = null; //TODO change here
    }

    @Test(expected = GrepException.class)
    public void testGrepNullInputStream() throws AbstractApplicationException {
        String[] args = new String[]{"bar|z"};

        app.run(args, null, stdout);
    }

    @Test(expected = GrepException.class)
    public void cannotGrepDirectory() throws AbstractApplicationException {
        String[] args = { "a", "abalone0000" };

        app.run(args, null, stdout);
    }

    @Test(expected = GrepException.class)
    public void testGrepEmptyPattern() throws AbstractApplicationException {
        String[] args = new String[]{ "" };
        app.run(args, new ByteArrayInputStream("foo".getBytes()), stdout);
    }


    @Test
    public void canMatchTwoArgumentsWithOneCharRegex() throws AbstractApplicationException {
        String[] args = { "a", "cxintro01.txt" };
        String expected = "A clam is a type of shellfish."
                + System.lineSeparator();

        app.run(args, null, stdout);
        Assert.assertEquals(expected, stdout.toString());
    }

    @Test
    public void canGrepWithWhitespacedRegex() throws AbstractApplicationException {
        String[] args = { "A ", "cxintro01.txt" };
        String expected = "A clam is a type of shellfish."
                + System.lineSeparator();

        app.run(args, null, stdout);
        Assert.assertEquals(expected, stdout.toString());
    }

    @Test
    public void testGrepValidPatternAndNoMatchViaStdin() throws AbstractApplicationException {
        String[] args = new String[]{"grep", "bar|z"};
        app.run(args, new ByteArrayInputStream(("adinda"   + System.lineSeparator()
                + "riandy"   + System.lineSeparator()
                + "sudarsan" + System.lineSeparator()
                + "yuan qing").getBytes()), stdout);
        Assert.assertEquals("", stdout.toString());
    }

}
