package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.QuoteParser;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;

public class SeqCommand implements Command {
  private String mCommandLine;
  private List<String> mTokens;
  private List<Command> mCommands;
  
  public SeqCommand(String commandLine) {
    this.mCommandLine = commandLine;
    this.mTokens = QuoteParser.parse(commandLine);
    this.mCommands = new ArrayList<Command>();
    
    String currentCommand = "";
    for (String token : mTokens) {
      currentCommand += " ";
      if (QuoteParser.isQuoted(token)) {
        currentCommand += token;
      } else {
        for (int i = 0; i < token.length(); i++) {
          Character currentCharacter = token.charAt(i);
          if (currentCharacter == ';') {
            Command command = ShellImplementation.getCommand(currentCommand.trim());
            mCommands.add(command);
            currentCommand = "";
          } else {
            currentCommand += currentCharacter;
          }
        }
      }
    }
    if (!currentCommand.trim().equals("")) {
      mCommands.add(ShellImplementation.getCommand(currentCommand.trim()));
    }
  }

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		// TODO Auto-generated method stub
		for (int i = 0; i < mCommands.size(); i++) {
		  Command command = mCommands.get(i);
		  InputStream input = i == 0 ? stdin : null;
		  command.evaluate(input, stdout);
		}
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}

}
