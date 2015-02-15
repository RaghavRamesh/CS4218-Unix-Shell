package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.DirectoryHelpers;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.FileCreateException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;
import sg.edu.nus.comp.cs4218.impl.app.ApplicationFactory;

public class CallCommand implements Command {
  private final String mCommandLine;
	private final List<String> mTokens;
	
	public CallCommand(String commandLine) throws ShellException {
	  mCommandLine = commandLine;
		this.mTokens = Parser.parseCommandLine(commandLine);
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
	  if (mTokens.isEmpty()) {
	    return;
	  }
	  
	  String inFile = null;
	  String outFile = null;
	  
    try {
      Application app = getApplication(mTokens.get(0));
      List<String> argsList = new ArrayList<String>();
      int currentPos = 1;
      while (currentPos < mTokens.size()) {
        String token = mTokens.get(currentPos++);
        if (Parser.isInStream(token)) {
          if (inFile != null) {
            throw new ShellException(Consts.Messages.TOO_MANY_INPUT + mCommandLine);
          }
          // TODO: Handle case < >
          if (currentPos == mTokens.size()) {
            throw new ShellException(Consts.Messages.NO_IN_PROVIDED + mCommandLine);
          }
          inFile = mTokens.get(currentPos++);
        } else if (Parser.isOutStream(token)) {
          if (outFile != null) {
            throw new ShellException(Consts.Messages.TOO_MANY_OUTPUT + mCommandLine);
          }
          // TODO: Handle case < >
          if (currentPos == mTokens.size()) {
            throw new ShellException(Consts.Messages.NO_OUT_PROVIDED + mCommandLine);
          }
          outFile = mTokens.get(currentPos++);
        } else {
          argsList.add(token);
        }
      }
      InputStream inStream = (inFile == null) ? stdin : 
        new FileInputStream(DirectoryHelpers.createFile(inFile));
      OutputStream outStream = (outFile == null) ? stdout :
        new FileOutputStream(DirectoryHelpers.createFile(outFile));
      String[] args = argsList.toArray(new String[argsList.size()]);
      app.run(args, inStream, outStream);
    } catch (FileNotFoundException e) {
      throw new ShellException(e.getMessage());
    }
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
	  
	}
	
	private Application getApplication(String appId) throws ShellException {
    ApplicationFactory factory = new ApplicationFactory();
    return factory.getApplication(appId);
	}
}
