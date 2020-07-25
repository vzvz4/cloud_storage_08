package NIO;

import client.FileUtility;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

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
    return "./common/src/main/java/NIO/user_folder/";
  }
  private void handle() throws IOException {
    System.out.println("read key");
    ByteBuffer buffer = ByteBuffer.allocate(80);
    int count = ((SocketChannel) key.channel()).read(buffer);
    if (count == -1)
      key.channel().close();

    buffer.flip();
    StringBuilder s = new StringBuilder();
    while (buffer.hasRemaining()) {
      s.append((char) buffer.get());
    }

    System.out.println(s);
    parseCommand(s.toString());

  }

  private void parseCommand(String s) throws IOException {
    ((SocketChannel) key.channel()).write(ByteBuffer.wrap(s.getBytes()));
    String[] rowCmd = s.split("\\s");
    if (rowCmd.length > 1) {
      if (rowCmd[0].equals(DOWNLOAD.getCmd())) {
        String fileName = Arrays.stream(rowCmd).skip(1).reduce("", (x, y) -> x + " " + y).trim();
        System.out.println("-:download file: " + fileName);
        FileUtility.sendFile(key.channel(), new File(userDirectory + fileName));
      }
    }
  }

}
