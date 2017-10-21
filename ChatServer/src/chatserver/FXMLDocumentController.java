/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

/**
 *
 * @author miebakso
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Button startButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private TextArea textArea;
    
    private Thread chatRoom;
    
    @FXML
    private ListView<String> userList;
    
    @FXML
    private void activateButtonHandler(ActionEvent event) {
        if(startButton.getText().equals("Start")){
            chatRoom = new Thread(new ChatRoom(this));
            chatRoom.start();
            textArea.appendText("Chat Room Opened...\n");
            textArea.styleProperty().setValue("-fx-base: #b6e7c9;");
            startButton.styleProperty().setValue("-fx-base: #ff0000;");
            startButton.setText("Closed");
        } else {
            textArea.appendText("Chat Room Closed...\n");
            startButton.setText("Opened");
            //Platform.exit();
            System.exit(0);
        }
    }
    
    @FXML
    private void clearTextAreaButtonHandler(ActionEvent event) {
        textArea.setText("");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }
    
    public void writeMessageToTextArea(String s){
        textArea.appendText(s);
    }
    
    public void updateUserList(String s){
        userList.getItems().clear();
       
        String[] users = s.split("-");
        Arrays.sort(users);
        for(String user: users){
            userList.getItems().add(user);
        }
        
    }
    
}
