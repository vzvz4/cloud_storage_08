package NIO;

import client.FileUtility;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import static client.Commands.*;

public class InputDataHandler {
  private SelectionKey key;
  private Selector selector;
  private String userDirectory;

  public InputDataHandler(SelectionKey key, Selector selector) throws IOException {
    this.key = key;
    this.selector = selector;
    userDirectory = checkUser();
    handle();
  }

  private String checkUser() {
    //some user validation
    return "./common/src/main/java/NIO/user_folder/";
  }

  private void handle() throws IOException {
    System.out.println("-:read key:");
    ByteBuffer buffer = ByteBuffer.allocate(80);
    int count = ((SocketChannel) key.channel()).read(buffer);
    if (count == -1)
      key.channel().close();

    buffer.flip();
    StringBuilder s = new StringBuilder();
    while (buffer.hasRemaining()) {
      s.append((char) buffer.get());
    }
    System.out.println("-:command from client: [" + s + "]");
    parseCommand(s.toString());

  }

  private void parseCommand(String s) throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();
    String[] rowCmd = s.split("\\s");
    if (rowCmd.length > 1) {
      if (rowCmd[0].equals(DOWNLOAD.getCmd())) {
        String fileName = FileUtility.getFileName(rowCmd);
        System.out.println("-:download file: [" + fileName + "]");
        if (checkIfExist(userDirectory + fileName)) {
          channel.write(ByteBuffer.wrap((DOWNLOAD.getCmd() + " " + fileName).getBytes()));
          FileUtility.sendFile(key.channel(), new File(userDirectory + fileName));
        }
      } else if (rowCmd[0].equals(UPLOAD.getCmd())) {
        String fileName = FileUtility.getFileName(rowCmd);
        channel.write(ByteBuffer.wrap((UPLOAD.getCmd() + " " + fileName).getBytes()));
        FileUtility.placeFile(key.channel(), new File(userDirectory + fileName));
      }
    }
    if (rowCmd[0].equals(DIR.getCmd())) {
      channel.write(ByteBuffer.wrap((DIR.getCmd() + getDirectories()).getBytes()));
      System.out.println(getDirectories());
    }
  }

  private boolean checkIfExist(String path) {
    return new File(path).exists();
  }

  private String getDirectories() {
    return new ArrayList<>(FileUtility.showDirs(userDirectory))
        .stream()
        .map(File::getName)
        .reduce("", (x, y) -> x + ":" + y);
  }

}
