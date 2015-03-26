package sg.edu.nus.comp.cs4218;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class TestHelper {
  private TestHelper() {
    // Empty
  }
  
  public static String readFile(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    try {
      StringBuilder builder = new StringBuilder();
      String line = reader.readLine();

      while (line != null) {
        builder.append(line);
        builder.append(System.lineSeparator());
        line = reader.readLine();
      }
      return builder.toString();
    } finally {
      reader.close();
    }
  }
  
  // Delete folder and all sub folders and files within
  public static void purgeDirectory(File dir) {
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        purgeDirectory(file);
      }
      file.delete();
    }
    dir.delete();
  }
}
