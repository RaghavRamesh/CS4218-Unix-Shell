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
import sg.edu.nus.comp.cs4218.DirectoryHelpers;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.FileCreateException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;
import sg.edu.nus.comp.cs4218.impl.app.ApplicationFactory;

public class CallCommand implements Command {
	private String mCommandLine;
	private List<String> mTokens;
	
	public CallCommand(String commandLine) {
		this.mCommandLine = commandLine;
		this.mTokens = Parser.parseCommandLine(mCommandLine);
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
	  if (mTokens.size() > 0) {
	    try {
	      Application app = getApplication(mTokens.get(0));
	      List<String> argsList = new ArrayList<String>();
	      int i = 1;
	      while (i < mTokens.size()) {
	        String token = mTokens.get(i++);
	        if (Parser.isInStream(token)) {
	          String inStream = mTokens.get(i++);
	          stdin = new FileInputStream(DirectoryHelpers.createFile(inStream));
	        } else if (Parser.isOutStream(token)) {
	          String outStream = mTokens.get(i++);
	          stdout = new FileOutputStream(DirectoryHelpers.createFile(outStream));
	        } else {
	          argsList.add(token);
	        }
	      }
	      String[] args = argsList.toArray(new String[argsList.size()]);
	      app.run(args, stdin, stdout);
	    } catch (FileCreateException e) {
	      throw new ShellException(e.getMessage());
	    } catch (FileNotFoundException e) {
	      throw new ShellException(e.getMessage());
	    }
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
