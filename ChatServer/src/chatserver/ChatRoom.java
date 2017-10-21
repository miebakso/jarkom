/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 *
 * @author miebakso
 */
public class ChatRoom implements Runnable{
    private String message;
    private FXMLDocumentController ta;
    private ArrayList roomChat;
    private ArrayList<String> users;
    
    public ChatRoom(FXMLDocumentController ta){
        this.ta=ta;
    }
    
    @Override
    public void run() {
        roomChat = new ArrayList();
        users = new ArrayList();
        
        ServerSocket serverSock;
        
        try {
            serverSock = new ServerSocket(2015);
            while (true) {
                Socket clientSock = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                roomChat.add(writer);
                Thread listener = new Thread(new ClientHandler(clientSock, writer, this));
                listener.start();
            }
        } catch (Exception ex) {
            System.out.println("err connection");
        }
        
    }
    
    public void tellEveryone(String message) {
	Iterator it = roomChat.iterator();
        String[] data = message.split(":");
        writeMessage("(Notify Everyone) " + data[1] + "\n");

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
		writer.println(message);
                writer.flush();
            } catch (Exception ex){
		writeMessage("(Error occured, failed to notify.) \n");
            }
        }
    }
    
    public void removeClient(String name){
        Iterator it = roomChat.iterator();

        int i=0;
        while (it.hasNext()) {
            PrintWriter writer = (PrintWriter) it.next();
            if(users.get(i).equals(name)){
                it.remove();
                break;
            }
            if(i<users.size()-1) {
                i++;
            }
        }
    }
    
    private void updateClientUserList(String message) {
	Iterator it = roomChat.iterator();
        String[] data = message.split(":");
        
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
		writer.println(message);
                writer.flush();
            } catch (Exception ex){
		writeMessage("(Error occured, failed to notify.) \n");
            }
        }
    }
    
    public void wisper(String name, String message){
        Iterator it = roomChat.iterator();
       
        writeMessage("(Wisper "+name+") " + message + "\n");

        int i=0;
        while (it.hasNext()) {
            PrintWriter writer = (PrintWriter) it.next();
            if(users.get(i).equals(name)){
                try {
                    writer.println(name+" (wisper)"+":"+message+":Chat");
                    writer.flush();
                } catch (Exception ex){
                    writeMessage("(Error occured, failed to notify.) \n");
                }
                break;
            }
            if(i<users.size()-1) {
                i++;
            }
        }
    }
    
    public void userRemove (String data){
        users.remove(data); 
    }
    
    public void userAdd (String data){
        users.add(data);
    }
    
    public String getUserList(){
        message="";
        for (String user : users) {
            message+=user+"-";
        }
        updateClientUserList("users:"+message+":UserList");
        updateUserList(message);
        return message;
    }
    
    public void writeMessage(String message){
        ta.writeMessageToTextArea(message);
    }
    
    private void updateUserList(String users){
        ta.updateUserList(users);
    }
}
