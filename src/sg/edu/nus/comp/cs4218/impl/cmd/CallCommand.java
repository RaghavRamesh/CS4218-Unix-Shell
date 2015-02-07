package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.QuoteParser;
import sg.edu.nus.comp.cs4218.impl.app.ApplicationFactory;

public class CallCommand implements Command {
	private String mCommandLine;
	private List<String> mTokens;
	
	public CallCommand(String commandLine) {
		this.mCommandLine = commandLine;
		this.mTokens = QuoteParser.parse(mCommandLine);
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
	  if (mTokens.size() > 0) {
	    Application app = getApplication(mTokens.get(0));
	    List<String> argsList = mTokens.subList(1, mTokens.size());
      String[] args = argsList.toArray(new String[argsList.size()]);
      app.run(args, stdin, stdout);
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
