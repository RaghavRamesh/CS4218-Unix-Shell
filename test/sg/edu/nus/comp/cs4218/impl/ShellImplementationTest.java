package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SeqCommand;

public class ShellImplementationTest {
	ShellImplementation shell;

	@Test
	public void testGetSeqCommand() {
		try {
			String cmdLine = "cd articles; cat text1.txt";
			Object obj = new SeqCommand(cmdLine);
			assertEquals(obj.getClass(), ShellImplementation
					.getCommand(cmdLine).getClass());
		} catch (Exception e) {
			fail("No exception should be thrown");
		}
	}

	@Test
	public void testGetCallCommand() {
		try {
			String cmdLine = "cd articles";
			Object obj = new CallCommand(cmdLine);
			assertEquals(obj.getClass(), ShellImplementation
					.getCommand(cmdLine).getClass());
		} catch (Exception e) {
			fail("No exception should be thrown");
		}
	}
}
