package sg.edu.nus.comp.cs4218.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import sg.edu.nus.comp.cs4218.Command;

public class ExecutableThread extends Thread {
  private Command command;
  private InputStream inputStream;
  private OutputStream outputStream;

  public ExecutableThread() {
    super();
  }

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
    } catch (Exception e) {
      PrintStream printStream = new PrintStream(outputStream);
      printStream.println(e.getMessage());
    }
  }
}
