/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Hizkia
 */
public class FXMLLoginController implements Initializable {

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnLogin, btnRegister;

    private BufferedReader reader;
    private PrintWriter writer;
    private String username = "";
    private String password = "";

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML
    private void loginButtonAction(ActionEvent event) {
        username = txtUsername.getText();
        password = txtPassword.getText();
        List list = new ArrayList<String>();
        int total = 0;
        boolean status = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("Clients.txt")))) {
            String line;            
            while ((line = reader.readLine()) != null) {                
                list.add(line);
                total++;

            }
            for (int i = 0; i < total; i++) {
                if (list.get(i).equals(username) && list.get(i+1).equals(password)) {
                    status = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (status) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLClient.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene((Pane) loader.load()));
                stage.setTitle("Client");
                stage.show();
                ((Node) (event.getSource())).getScene().getWindow().hide();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    private void registerButtonAction(ActionEvent event) {

        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("Clients.txt", true)));
            writer.println(txtUsername.getText());
            writer.println(txtPassword.getText());
            writer.flush();
            writer.close();
        } catch (IOException ex) {

        }
    }
}
