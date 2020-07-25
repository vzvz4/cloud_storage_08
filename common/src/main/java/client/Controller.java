package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static client.Commands.DIR;

public class Controller implements Initializable {

  public ListView<String> lv;
  public TextField txt;
  public Button send;
  private Socket socket;
  private DataInputStream is;
  private DataOutputStream os;
  private Callback listViewCB;

  private final String clientFilesPath = "./common/src/main/resources/clientFiles";

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      socket = new Socket("localhost", 8191);
      is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
      os = new DataOutputStream(socket.getOutputStream());
      initCallBacks();
      new Thread(new InputDataHandler(is, os, listViewCB)).start();
      os.write(DIR.getCmd().getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initCallBacks() {
    listViewCB = (s -> Platform.runLater(() -> lv.getItems().add(s)));
  }

  public void sendCommand(ActionEvent actionEvent) throws IOException {
      os.write(txt.getText().getBytes());
      if (txt.getText().equals(DIR.getCmd()))
        lv.getItems().clear();
  }
}
