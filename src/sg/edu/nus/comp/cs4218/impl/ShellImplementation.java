package sg.edu.nus.comp.cs4218.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SeqCommand;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class ShellImplementation implements Shell {
  private final BufferedReader reader;
  private ExecutableThread runningThread;
  
  public ShellImplementation(BufferedReader reader) {
    this.reader = reader;
    this.runningThread = new ExecutableThread();
  }

	@Override
	public void parseAndEvaluate(String cmdline, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		Command command = getCommand(cmdline);
		runningThread = new ExecutableThread(command, null, stdout);
		runningThread.start();
	}

	/**
	 * Construct an appropriate command from an command line input string.
	 * 
	 * @param cmdline
	 *            The original string input to check.
	 * @return A command.
	 */
	public static Command getCommand(String cmdline) throws ShellException,
			AbstractApplicationException {
		List<AbstractToken> tokens = Parser.tokenize(cmdline);
		for (AbstractToken token : tokens) {
			if (token.getType() == TokenType.SEMICOLON) {
				return new SeqCommand(cmdline);
			}
		}
	  for (AbstractToken token : tokens) {
      if (token.getType() == TokenType.PIPE) {
        return new PipeCommand(cmdline);
      }
    }
		return new CallCommand(cmdline);
	}
	
	public void run() {
	  while (true) {
	    try {
	      while (runningThread.isAlive()) {
	        Thread.sleep(100);
	      }
	      System.out.print(Environment.getCurrentDirectory() + " # ");
	      parseAndEvaluate(reader.readLine(), System.out);
	    } catch (Exception e) {
	      System.out.println(e.getMessage());
	    }
	  }
	}

	public static void main(String... args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ShellImplementation shellImpl = new ShellImplementation(reader);
		shellImpl.run();
	}
}
