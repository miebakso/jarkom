/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 *
 * @author miebakso
 */
public class ClientHandler implements Runnable{
    
    private BufferedReader reader;
    private Socket sock;
    private PrintWriter client;
    private ChatRoom ch;
   

    public ClientHandler(Socket clientSocket, PrintWriter user, ChatRoom ch) {
        this.ch=ch;
        client = user;
        try {
            sock = clientSocket;
            InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(isReader);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    @Override
    public void run() {
        String message;
        String[] data;

        try {
            while ((message = reader.readLine()) != null) {
                data = message.split(":");
                if(data[2].equals("Chat")){
                    ch.writeMessage("(Message Received From "+data[0]+") "+ data[1] + "\n");
                }

                if (data[2].equals("Connect")) {
                    ch.tellEveryone(("users:"+data[0]+" has connected.:Connect"));
                    ch.userAdd(data[0]);
                    ch.getUserList();
                } else if (data[2].equals("Disconnect")) {
                    ch.removeClient(data[0]);
                    ch.tellEveryone(("users:"+data[0]+" has disconnected.:Disconnect"));
                    ch.userRemove(data[0]);
                    ch.getUserList();
                } else if (data[2].equals("Chat")) {
                    String[] wisperChat = data[1].split(" ");
                    if(wisperChat.length<3){
                        if(!wisperChat[0].equals("/w")){
                            ch.tellEveryone(message);
                        }
                    } else {
                        if(wisperChat[0].equals("/w")){
                            message="";
                            for(int i=2;i<wisperChat.length;i++){ 
                                if(i==wisperChat.length-1){
                                    message+=wisperChat[i];
                                } else {
                                    message+=wisperChat[i]+" ";
                                }
                            }

                            ch.wisper(wisperChat[1],message);
                        } else {
                            ch.tellEveryone(message);
                        }
                    } 
                    
                }
            }
        } catch (Exception ex) {
            ch.writeMessage(ex.toString());
        }
    }
}
