package client;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

  public ListView<String> lv;
  public TextField txt;
  public Button send;
  private Socket socket;
  private DataInputStream is;
  private DataOutputStream os;
  private ByteArrayInputStream byteInput;

  private final String clientFilesPath = "./common/src/main/resources/clientFiles";

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      socket = new Socket("localhost", 8191);
      is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
      os = new DataOutputStream(socket.getOutputStream());

    } catch (IOException e) {
      e.printStackTrace();
    }
    File dir = new File(clientFilesPath);
    for (String file : dir.list()) {
      lv.getItems().add(file);
    }
    new Thread(new InputDataReader(is)).start();
  }

  // ./download fileName
  // ./upload fileName
  public void sendCommand(ActionEvent actionEvent) throws IOException {
    String command = txt.getText();
    String[] parsed = command.split("\\s");
    if (parsed.length > 1)
      os.write(command.getBytes());
  }
}
