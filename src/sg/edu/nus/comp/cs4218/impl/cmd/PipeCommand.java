package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ExecutableThread;
import sg.edu.nus.comp.cs4218.impl.Parser;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken;
import sg.edu.nus.comp.cs4218.impl.token.AbstractToken.TokenType;

public class PipeCommand implements Command {
  private final List<CallCommand> commands;
  private final String commandLine;
  
  public PipeCommand(String commandLine) throws ShellException,
  AbstractApplicationException {
    this.commandLine = commandLine;
    this.commands = new ArrayList<CallCommand>();
    List<AbstractToken> tokens = Parser.tokenize(commandLine);
    
    String currentCommand = "";
    for (AbstractToken token : tokens) {
      if (token.getType() == TokenType.PIPE) {
        Command command = ShellImplementation.getCommand(currentCommand.trim());
        assert(command.getClass() == CallCommand.class);
        commands.add((CallCommand) command);
        currentCommand = "";
      } else {
        currentCommand += " " + token.toString();
      }
    }
    if (!currentCommand.trim().equals("")) {
      CallCommand command = (CallCommand) ShellImplementation.getCommand(currentCommand.trim());
      commands.add(command);
    }
  }

  @Override
  public void evaluate(InputStream stdin, OutputStream stdout)
      throws AbstractApplicationException, ShellException {
    try {
      List<ExecutableThread> threads = new ArrayList<ExecutableThread>();
      InputStream currentInput = stdin;
      OutputStream currentOutput;
      for (int i = 0; i < commands.size(); i++) {
        if (i == commands.size() - 1) {
          currentOutput = stdout;
        } else {
          currentOutput = new PipedOutputStream();
        }
        CallCommand command = commands.get(i);
        ExecutableThread thread = new ExecutableThread(command, currentInput, currentOutput);
        threads.add(thread);
        if (i < commands.size() - 1) {
          currentInput = new PipedInputStream((PipedOutputStream) currentOutput);
        }
      }
      
      // Start all commands
      for (Thread thread : threads) {
        thread.start();
      }

      // Wait until all threads finished
      Boolean threadsNotFinished = true;
      while (threadsNotFinished) {
        Thread.sleep(100);
        threadsNotFinished = false;
        for (Thread thread : threads) {
          if (thread.isAlive()) {
            threadsNotFinished = true;
            break;
          }
        }
      }
    } catch (Exception e) {
      throw new ShellException(e.getMessage());
    }
  }

  @Override
  public void terminate() {
    // Auto generated
  }

  @Override
  public String toString() {
    return commandLine;
  }
}
