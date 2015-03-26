package sg.edu.nus.comp.cs4218.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.app.CatApp;
import sg.edu.nus.comp.cs4218.impl.app.SedApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class SedTest {

    private static Application app;
    private static ByteArrayOutputStream stdout;
    private static ByteArrayInputStream stdin;
    String[] args;
    
    @Rule
	public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        app = new SedApp(); //TODO change here
    }

    @Test(expected = SedException.class)
    public void testSedNullArgs() throws AbstractApplicationException {
        args = null;
        app.run(args, stdin, stdout);
    }

    @Test(expected = SedException.class)
    public void testSedUnterminatedReplacement()
            throws AbstractApplicationException {
        String replacement = "s" + "/" + "apple"
                + "/" + "banana";
        args = new String[] { replacement };
        app.run(args, stdin, stdout);
    }

    @Test(expected = SedException.class)
    public void testSedFileDoesNotExist() throws AbstractApplicationException {
        String fileNonExistent = "foo.txt";
        String replacement = "s" + "/" + "apple"
                + "/" + "banana" + "/";
        args = new String[] { replacement, fileNonExistent };
        app.run(args, stdin, stdout);
    }

    @Test
    public void testSedMultipleWordsReplaceOne()
            throws AbstractApplicationException {
        String replacement = "s" + "/" + "apple"
                + "/" + "banana" + "/";
        args = new String[] { replacement };
        stdin = new ByteArrayInputStream("apple apple apple".getBytes());
        stdout = new ByteArrayOutputStream();

        app.run(args, stdin, stdout);
        Assert.assertEquals("banana apple apple" + System.lineSeparator(), stdout.toString());
    }

    @Test
    public void testSedMultipleWordsReplaceAll()
            throws AbstractApplicationException {
        String replacement = "s" + "/" + "apple"
                + "/" + "banana" + "/" + "g";
        args = new String[] { replacement };
        stdin = new ByteArrayInputStream("apple apple apple".getBytes());
        stdout = new ByteArrayOutputStream();
        
        app.run(args, stdin, stdout);
        Assert.assertEquals("banana banana banana" + System.lineSeparator(), stdout.toString());
    }

    @Test
    public void validArgsWithRegexMatchUsingReplaceAll() throws AbstractApplicationException {

        args = new String[] { "s/[0-9]/2/g" };
        String expected = "The name \"scallop\" comes from the Old French escalope, which means \"shell\"." + System.lineSeparator();
        expected += "Their shells can be up to 22 centimetres (2 inches) across.";
        stdin = new ByteArrayInputStream(("The name \"scallop\" comes from the Old French escalope, which means \"shell\".\n" +
                "Their shells can be up to 15 centimetres (6 inches) across.").getBytes());
        stdout = new ByteArrayOutputStream();
        
        app.run(args, stdin, stdout);
        Assert.assertEquals(expected + System.lineSeparator(), stdout.toString());
    }

    @Test
    public void validPipeStreamInputArgWithExactMatchUsingReplaceFirstDifferentSeparator() throws AbstractApplicationException {

        String pipeInputArg = "Oysters are a family of bivalves with rough, thick shells." + System.lineSeparator();
        pipeInputArg += "Many species are edible, and are usually served raw." + System.lineSeparator();
        pipeInputArg += "They are also good when cooked." + System.lineSeparator();
        pipeInputArg += "In history, they were an important food source, especially in France and Britain." + System.lineSeparator();
        pipeInputArg += "They used to grow in huge oyster beds, but were \"overfished\" in the 19th century." + System.lineSeparator();
        pipeInputArg += "Nowadays they are more expensive, so eaten less often.";
        stdin = new ByteArrayInputStream(pipeInputArg.getBytes());

        args = new String[] { "s|are|to|" };
        String expected = "Oysters to a family of bivalves with rough, thick shells." + System.lineSeparator();
        expected += "Many species to edible, and are usually served raw." + System.lineSeparator();
        expected += "They to also good when cooked." + System.lineSeparator();
        expected += "In history, they were an important food source, especially in France and Britain." + System.lineSeparator();
        expected += "They used to grow in huge oyster beds, but were \"overfished\" in the 19th century." + System.lineSeparator();
        expected += "Nowadays they to more expensive, so eaten less often.";

        stdout = new ByteArrayOutputStream();
        app.run(args, stdin, stdout);
        Assert.assertEquals(expected + System.lineSeparator(), stdout.toString());
    }
    
    /*
     * These are custom test cases written by us
     */
    
	/*
	 * Tests for null output stream
	 */
	@Test
	public void testSedAppWithNullOutputStreamArgument()
			throws AbstractApplicationException {
		expectedEx.expect(SedException.class);
		expectedEx.expectMessage("sed: " 
				+ Consts.Messages.OUT_STR_NOT_NULL);

		SedApp cmdApp = new SedApp();
		String[] args = new String[] { "s/[0-9]/2/g" };
		String pipeInputArg = "Oysters are a family of bivalves with rough, thick shells." + System.lineSeparator();
		stdin = new ByteArrayInputStream(pipeInputArg.getBytes());
		cmdApp.run(args, stdin, null);
	}
	
	@Test
	public void testSedAppWithZeroArgument()
			throws AbstractApplicationException {
		expectedEx.expect(SedException.class);
		expectedEx.expectMessage("sed: " 
				+ Consts.Messages.EXT_MIN_ONE_ARG);

		SedApp cmdApp = new SedApp();
		String[] args = new String[] {};
		String pipeInputArg = "Oysters are a family of bivalves with rough, thick shells." + System.lineSeparator();
		stdin = new ByteArrayInputStream(pipeInputArg.getBytes());
		cmdApp.run(args, stdin, null);
	}
	
	@Test
	public void testSedAppWithInvalidStartForRegexExp()
			throws AbstractApplicationException {
		expectedEx.expect(SedException.class);
		expectedEx.expectMessage("sed: " 
				+ Consts.Messages.CANNOT_FIND_S);

		SedApp cmdApp = new SedApp();
		String[] args = new String[] { "a/[0-9]/2/g" }; // Regular expression should start with s
		String pipeInputArg = "Oysters are a family of bivalves with rough, thick shells." + System.lineSeparator();
		stdin = new ByteArrayInputStream(pipeInputArg.getBytes());
		stdout = new ByteArrayOutputStream();
		cmdApp.run(args, stdin,stdout );
	}
	
	@Test
	public void testSedAppWithInvalidEndForRegexExp()
			throws AbstractApplicationException {
		expectedEx.expect(SedException.class);
		expectedEx.expectMessage("sed: " 
				+ Consts.Messages.BAD_OPTION_IN_SUB);

		SedApp cmdApp = new SedApp();
		String[] args = new String[] { "s/[0-9]/2/a" }; // Regular expression should end with / or /g
		String pipeInputArg = "Oysters are a family of bivalves with rough, thick shells." + System.lineSeparator();
		stdin = new ByteArrayInputStream(pipeInputArg.getBytes());
		stdout = new ByteArrayOutputStream();
		cmdApp.run(args, stdin,stdout );
	}
	
	@Test
	public void testSedAppWithInvalidRegexExp()
			throws AbstractApplicationException {
		expectedEx.expect(SedException.class);
		expectedEx.expectMessage("sed: " 
				+ Consts.Messages.INVALID_SYMBOL);

		SedApp cmdApp = new SedApp();
		String[] args = new String[] { "sa/[0-9]/2/a" }; // The symbol / should be immediately after s 
		String pipeInputArg = "Oysters are a family of bivalves with rough, thick shells." + System.lineSeparator();
		stdin = new ByteArrayInputStream(pipeInputArg.getBytes());
		stdout = new ByteArrayOutputStream();
		cmdApp.run(args, stdin,stdout );
	}
	
    @Test
    public void validTestWithRegexContainingDifferntSymbol() throws AbstractApplicationException {

        args = new String[] { "s#[0-9]#2#g" };
        String expected = "The name \"scallop\" comes from the Old French escalope, which means \"shell\"." + System.lineSeparator();
        expected += "Their shells can be up to 22 centimetres (2 inches) across.";
        stdin = new ByteArrayInputStream(("The name \"scallop\" comes from the Old French escalope, which means \"shell\".\n" +
                "Their shells can be up to 15 centimetres (6 inches) across.").getBytes());
        stdout = new ByteArrayOutputStream();
        
        app.run(args, stdin, stdout);
        Assert.assertEquals(expected + System.lineSeparator(), stdout.toString());
    }
	
}
