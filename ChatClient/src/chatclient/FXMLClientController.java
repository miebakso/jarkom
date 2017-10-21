/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.net.*;
import java.io.*;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Hizkia
 */
public class FXMLClientController implements Initializable {

    @FXML
    private Label lblUsername;

    @FXML
    private TextField txtUsername, txtChatInput;
    
    @FXML
    private Button btnConnect, btnClear, btnSend, btnLogout;
         
    @FXML
    private TextArea txtChatArea;

    @FXML
    private ListView<String> listUsers;
  

    private String username;
    private ArrayList<String> users = new ArrayList();
    private int port = 2015;
    private boolean statusConnected = false;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listUsers.setOnMouseClicked(e -> {
            if(e.getClickCount() == 2){
                txtChatInput.setText("/w "+listUsers.getSelectionModel().getSelectedItem());
            }
        });
    }
    
    @FXML
    public void onEnter(ActionEvent event) {
        sendButtonAction(event);
    }

    @FXML
    private void activeButtonAction(ActionEvent event) {
        if(btnConnect.getText().equals("Connect")){
            btnConnect.setText("Disconnect");
            connectButtonAction(event); 
        } else if(btnConnect.getText().equals("Disconnect")) {
            btnConnect.setText("Connect");
            disconnectButtonAction(event);
            
        }
    }
    
    @FXML
    private void clearButtonAction(ActionEvent event){
        txtChatArea.clear();
    }
    
    @FXML
    private void connectButtonAction(ActionEvent event) {
        if (statusConnected == false) {
            username = txtUsername.getText();
            if (username.equals("")) {
                String human = "human";
                Random generator = new Random();
                int i = generator.nextInt(999) + 1;
                String is = String.valueOf(i);
                human = human.concat(is);
                username = human;
                txtUsername.setText(human);
            }
            txtUsername.setEditable(false);

            try {
                socket = new Socket("localhost", port);
                InputStreamReader streamreader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(streamreader);
                writer = new PrintWriter(socket.getOutputStream());
                writer.println(username + ":has connected.:Connect");
                writer.flush();
                statusConnected = true;
            } catch (IOException ex) {
                txtChatArea.appendText("Connection Failed. Try Again! \n");
                txtUsername.setEditable(true);
            }

            Thread IncomingReader = new Thread(new IncomingReader());
            IncomingReader.start();

        } else if (statusConnected == true) {
            txtChatArea.appendText("You are already connected. \n");
        }
    }

    @FXML
    private void disconnectButtonAction(ActionEvent event) {
        String disconnect = (username + ": :Disconnect");
        try {
            writer.println(disconnect);
            writer.flush();
        } catch (Exception e) {

        }

        try {
            socket.close();
            if (statusConnected == true) {
                txtChatArea.appendText("Disconnected.\n");
                listUsers.getItems().clear();
            }
        } catch (IOException ex) {
            if (statusConnected == false) {
                txtChatArea.appendText("Failed to disconnect. \n");
            }
        }
        statusConnected = false;
        txtUsername.setEditable(true);
    }

    @FXML
    private void logoutButtonAction(ActionEvent event) {
        disconnectButtonAction(event);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLLogin.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene((Pane) loader.load()));
            stage.setTitle("Login");
            stage.show();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void sendButtonAction(ActionEvent event) {
        String nothing = "";
        if ((txtChatInput.getText()).equals(nothing)) {
            txtChatInput.setText("");
            txtChatInput.requestFocus();
        } else {
            try {
                writer.println(username + ":" + txtChatInput.getText() + ":" + "Chat");
                writer.flush();
            } catch (Exception ex) {
                txtChatArea.appendText("Message was not sent. \n");
            }
            txtChatInput.setText("");
            txtChatInput.requestFocus();
        }
        txtChatInput.setText("");
        txtChatInput.requestFocus();
    }

    public class IncomingReader implements Runnable {

        @Override
        public void run() {
            String[] data;
            String message;

            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                    data = message.split(":");
                    if (data[2].equals("Chat")) {
                        txtChatArea.appendText(data[0] + ": " + data[1] + "\n");
                    } else if (data[2].equals("Connect")) {
                        users.add(data[0]);
                    } else if (data[2].equals("Disconnect")) {
                        txtChatArea.appendText(data[0] + " is now offline.\n");
                        
                    } else if (data[2].equals("Done")) {
                        String[] tempList = new String[(users.size())];
                        users.toArray(tempList);
                        users.clear();
                    } else if (data[2].equals("UserList")) {
                        String[] array;
                        array = data[1].split("-");
                        Arrays.sort(array);
                        listUsers.getItems().clear();
                        for(String user:array){
                            listUsers.getItems().add(user);
                        }
                    }
                }
            } catch (IOException ex) {
            }
        }
    }
}
