package client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static client.Commands.*;

public class InputDataReader implements Runnable {
  private InputStream is;
  private static final String USER_FOLDER = "./common/src/main/java/client/folder/";

  public InputDataReader(InputStream is) {
    this.is = is;
  }

  @Override
  public void run() {
    System.out.println(":input reader thread started");
    String cmd = "";
    while (!cmd.equals(CLOSE.getCmd())) {
      byte[] data = new byte[8194];
      try {
        while (is.available() != -1) {
          int off = is.read(data);
          cmd = readBuffer(data, off);
          System.out.println(cmd);
          parseCommand(cmd);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void parseCommand(String cmd) throws IOException {
    String[] rowCmd = cmd.split("\\s");
    cmd = rowCmd[0];
    if (rowCmd.length > 1) {
      if (cmd.equals(DOWNLOAD.getCmd()))
        download(rowCmd);
      else if (cmd.equals(DIR.getCmd()))
        appendDirs(rowCmd);
    }
  }

  private void appendDirs(String[] rowCmd) {

  }

  private void download(String[] rowCmd) throws IOException {
    System.out.println(":command from server: " + DOWNLOAD.getCmd() + " :fileName: " + rowCmd[1]);
    FileUtility.placeFile(is, new File(USER_FOLDER + rowCmd[1]));
  }

  private String readBuffer(byte[] data, int off) {
    StringBuilder cmd = new StringBuilder();
    for (int i = 0; i < off; i++) {
      cmd.append((char) data[i]);
    }
    return cmd.toString();
  }

}
