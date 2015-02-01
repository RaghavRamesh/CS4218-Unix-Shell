package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SeqCommand;

public class ShellImplementationTest {
	ShellImplementation si;
	
	@Test
	public void testGetSeqCommand() {
		si = new ShellImplementation();
		String cmdLine = "cd articles; cat text1.txt";
		Object obj = new SeqCommand(cmdLine);
		assertEquals(obj.getClass(), si.getCommand(cmdLine).getClass());
	}
	
	@Test
	public void testGetCallCommand() {
		si = new ShellImplementation();
		String cmdLine = "cd articles";
		Object obj = new CallCommand(cmdLine);
		assertEquals(obj.getClass(), si.getCommand(cmdLine).getClass());
	}
	
	@Test
	public void testGetPipeCommand() {
		si = new ShellImplementation();
		String cmdLine = "cat articles.txt | grep 'hello'";
		Object obj = new PipeCommand(cmdLine);
		assertEquals(obj.getClass(), si.getCommand(cmdLine).getClass());
	}
}
