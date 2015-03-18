package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
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
