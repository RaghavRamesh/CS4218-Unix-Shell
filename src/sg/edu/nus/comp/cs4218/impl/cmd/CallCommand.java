package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Consts;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;
import sg.edu.nus.comp.cs4218.impl.ShellImplementation;
import sg.edu.nus.comp.cs4218.impl.app.ApplicationFactory;

public class CallCommand implements Command {
  private final String mCommandLine;
  private final List<String> mTokens;
  private final List<String> mSubstitutedTokens;

  public CallCommand(String commandLine) throws ShellException, AbstractApplicationException {
    try {
      mCommandLine = commandLine;
      List<String> tokens = Parser.parseCommandLine(commandLine);
      mTokens = new ArrayList<String>();
      for (String token : tokens) {
        // Remove the outer double/single quotes
        if (Parser.isDoubleQuoted(token) || Parser.isSingleQuoted(token)) {
          token = token.substring(1, token.length() - 1);
        }
        if (!token.isEmpty()) {
          mTokens.add(token);
        }
      }
      mSubstitutedTokens = substituteAll(mTokens);
    } catch (IOException e) {
      throw new ShellException(e);
    }
  }

  @Override
  public void evaluate(InputStream stdin, OutputStream stdout)
      throws AbstractApplicationException, ShellException {
    if (mSubstitutedTokens.isEmpty()) {
      return;
    }
    try {
      String inFile = findInput();
      String outFile = findOutput();
      List<String> argsList = findArguments();
      InputStream inStream = (inFile == null) ? stdin 
          : new FileInputStream(Environment.createFile(inFile));
      OutputStream outStream = (outFile == null) ? stdout
          : new FileOutputStream(Environment.createFile(outFile));
      Application app = getApplication(argsList.get(0));
      List<String> argumentsWithoutApp = argsList.subList(1, argsList.size());
      String[] args = argumentsWithoutApp.toArray(new String[argumentsWithoutApp.size()]);
      app.run(args, inStream, outStream); 
    } catch (FileNotFoundException e) {
      throw new ShellException(e);
    }
  }

  public static List<String> substituteAll(List<String> tokens)
      throws AbstractApplicationException, ShellException, IOException {
    ArrayList<String> result = new ArrayList<String>();
    for (String token : tokens) {
      if (Parser.containsBackQuote(token)) {
        result.add(substitute(token));
      } else {
        result.add(token);
      }
    }
    return result;
  }

  public static String substitute(String input)
      throws AbstractApplicationException, ShellException, IOException {
    List<String> tokens = Parser.parseCommandLine(input);
    List<String> tokensWithoutQuotes = new ArrayList<String>();
    for (int i = 0; i < tokens.size(); i++) {
      String token = tokens.get(i);
      if (Parser.isSingleQuoted(token) || Parser.isDoubleQuoted(token)) {
        token = token.substring(1, token.length() - 1);
        tokensWithoutQuotes.addAll(Parser.parseCommandLine(token));
      } else {
        tokensWithoutQuotes.add(token);
      }
    }

    for (int i = 0; i < tokensWithoutQuotes.size(); i++) {
      String token = tokensWithoutQuotes.get(i);

      if (Parser.isBackQuoted(token)) {
        // Remove back quotes
        token = token.substring(1, token.length() - 1);
        Command command = ShellImplementation.getCommand(token);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        command.evaluate(null, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BufferedReader br = new BufferedReader(new InputStreamReader(byteArrayInputStream));
        tokensWithoutQuotes.set(i, br.readLine());
      }
    }
    // TODO: toString trims spaces
    String result = "";
    for (String token : tokensWithoutQuotes) {
      result += " " + token;
    }
    // System.out.println(result.trim());
    return result.trim();
  }
  
  public String findInput() throws ShellException {
    String result = null;
    int currentIndex = 0;
    while (currentIndex < mSubstitutedTokens.size()) {
      String token = mSubstitutedTokens.get(currentIndex++);
      if (Parser.isInStream(token)) {
        if (result != null) {
          throw new ShellException(Consts.Messages.TOO_MANY_INPUT + mCommandLine);
        }
        if (currentIndex == mSubstitutedTokens.size()
            || Parser.isSpecialCharacter(mSubstitutedTokens.get(currentIndex))) {
          throw new ShellException(Consts.Messages.INVALID_INPUT + mCommandLine);
        }
        result = mSubstitutedTokens.get(currentIndex++);
      }
    }
    return result;
  }
  
  public String findOutput() throws ShellException {
    String result = null;
    int currentIndex = 0;
    while (currentIndex < mSubstitutedTokens.size()) {
      String token = mSubstitutedTokens.get(currentIndex++);
      if (Parser.isOutStream(token)) {
        if (result != null) {
          throw new ShellException(Consts.Messages.TOO_MANY_OUTPUT + mCommandLine);
        }
        if (currentIndex == mSubstitutedTokens.size()
            || Parser.isSpecialCharacter(mSubstitutedTokens.get(currentIndex))) {
          throw new ShellException(Consts.Messages.INVALID_OUTPUT + mCommandLine);
        }
        result = mSubstitutedTokens.get(currentIndex++);
      }
    }
    return result;
  }
  
  public List<String> findArguments() {
    ArrayList<String> result = new ArrayList<String>();
    int currentIndex = 0;
    while (currentIndex < mSubstitutedTokens.size()) {
      String token = mSubstitutedTokens.get(currentIndex++);
      if (Parser.isOutStream(token) || Parser.isInStream(token)) {
        // Ignore the next token (possibly file name)
        currentIndex++;
      } else {
        result.add(token);
      }
    }
    return result;
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
