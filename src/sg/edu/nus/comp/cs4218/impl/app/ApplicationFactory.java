package sg.edu.nus.comp.cs4218.impl.app;

import java.util.HashMap;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.AppNotFoundException;

public class ApplicationFactory {
  HashMap<String, Application> maps = new HashMap<String, Application>();

  public ApplicationFactory() {
    maps.put("cd", new CdApp());
    maps.put("ls", new LsApp());
    maps.put("pwd", new PwdApp());
    maps.put("cat", new CatApp());
    maps.put("echo", new EchoApp());
    maps.put("head", new HeadApp());
  }

  public Application getApplication(String appId) throws AppNotFoundException {
    if (maps.containsKey(appId)) {
      return maps.get(appId);
    } else {
      throw new AppNotFoundException(appId + " not found");
    }
  }
}
