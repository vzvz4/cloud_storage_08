package client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static client.Commands.*;


public class InputDataHandler implements Runnable {
  private InputStream is;
  private OutputStream os;
  private Callback listViewCallback;

  private static final String USER_FOLDER = "./common/src/main/java/client/folder/";

  public InputDataHandler(InputStream is, OutputStream os, Callback... callbacks) {
    this.listViewCallback = callbacks[0];
    this.is = is;
    this.os = os;
  }

  @Override
  public void run() {
    System.out.println("-:input reader thread started");
    String cmd = "";
    while (!cmd.equals(CLOSE.getCmd())) {
      byte[] data = new byte[8194];
      try {
        while (is.available() > 0) {
          int off = is.read(data);
          cmd = readBuffer(data, off);
          System.out.println("-:data from server: [" + cmd + "]");
          parseCommand(cmd);
          System.out.println("-:command executed:");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void parseCommand(String cmd) throws IOException {
    String[] rowCmd = cmd.split("\\s");
    if (rowCmd.length > 1) {
      if (rowCmd[0].equals(DOWNLOAD.getCmd()))
        download(rowCmd);
      else if (rowCmd[0].equals(UPLOAD.getCmd()))
        upload(cmd);
      else if (rowCmd[0].startsWith(DIR.getCmd()))
        appendServerDirs(cmd);
    }
  }

  private void upload(String cmd) throws IOException {
    String fileName = FileUtility.getFileName(cmd.split(" "));
    printLog(UPLOAD, fileName);
    FileUtility.sendFile(os, new File(USER_FOLDER + fileName));
  }

  private void appendServerDirs(String rowCmd) {
    printLog(DIR, rowCmd);
    List<String> serverFiles;
    serverFiles = Arrays.stream(rowCmd.split(":"))
        .skip(1)
        .collect(Collectors.toList());
    serverFiles.forEach(x -> listViewCallback.call(x));
  }

  private void download(String[] rowCmd) throws IOException {
    String fileName = FileUtility.getFileName(rowCmd);
    printLog(DOWNLOAD, fileName);
    FileUtility.placeFile(is, new File(USER_FOLDER + fileName));
  }

  private String readBuffer(byte[] data, int off) {
    StringBuilder cmd = new StringBuilder();
    for (int i = 0; i < off; i++) {
      cmd.append((char) data[i]);
    }
    return cmd.toString();
  }



  private void printLog(Commands command, String payload) {
    System.out.println(String.format(
        "-:command: [%s] :payload: [%s]", command.getCmd(), payload));
  }

}
