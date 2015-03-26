package sg.edu.nus.comp.cs4218.impl;

import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ExecutableThread extends Thread {
  private Command command;
  private InputStream inputStream;
  private OutputStream outputStream;
  private ShellException shellException;
  private AbstractApplicationException appException;

//  public ExecutableThread() {
//    super();
//    shellException = null;
//    appException = null;
//  }

  public ExecutableThread(Command command, InputStream inputStream,
      OutputStream outputStream) {
    super();
    this.command = command;
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  @Override
  public void run() {
    try {
      command.evaluate(inputStream, outputStream);
    } catch (ShellException e) {
      shellException = e;
    } catch (AbstractApplicationException e) {
      appException = e;
    }
  }
  
  @Override
  public void interrupt() {
    command.terminate();
    super.interrupt();
  }
  
  public ShellException getShellException() {
    return shellException;
  }

  public AbstractApplicationException getApplicationException() {
    return appException;
  }
}
