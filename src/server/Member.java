package server;

import transfer.Receiver;
import transfer.Sender;

import java.io.*;
import java.net.Socket;

public class Member extends Thread {

    private Socket chatSocket; // used for sending message to client's chatSocket
    private Socket fileSocket; // used for file transmission
    private String nickName; // current nickname in room

    public Member(Socket chatSocket, Socket fileSocket) {
        this.chatSocket = chatSocket;
        this.fileSocket = fileSocket;
        this.nickName = "nameless";
    }

    public void run() {
        try {
            while(true) {

                // receive message from client's chatSocket
                BufferedReader br = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
                String input = br.readLine();
                String[] split = input.split(" ");

                // deal with this message calling server's method
                if(split[0].equalsIgnoreCase("#CREATE"))
                    Server.createRoom(this,split[1],split[2]);
                else if(split[0].equalsIgnoreCase("#JOIN"))
                    Server.joinRoom(this,split[1],split[2]);
                else if(split[0].equalsIgnoreCase("#EXIT"))
                    Server.exitRoom(this);
                else if(split[0].equalsIgnoreCase("#STATUS"))
                    Server.statusRoom(this);
                else if(split[0].equalsIgnoreCase("#PUT"))
                    Server.putFile(this,split[1]);
                else if(split[0].equalsIgnoreCase("#GET"))
                    Server.getFile(this,split[1]);
                else Server.chatRoom(this,input);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
//------------------------------------------------------------------------------------------

    // getter and setter for nickName
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    // send message to client's chatSocket
    public void sendMessage(String message) throws Exception {
        DataOutputStream dos = new DataOutputStream(chatSocket.getOutputStream());
        dos.writeBytes(message + "\n");
    }

    // receive file from client's fileSocket
    public void receiveFile(String path) throws Exception {
        File file = new File(path);
        if(file.exists()) file.delete();
        file.createNewFile();

        Receiver receiver = new Receiver(fileSocket,file);
        receiver.start();
    }

    // send file to client's fileSocket
    public void sendFile(String path) throws Exception {
        File file = new File(path);
        if(file.exists()) {
            Sender sender = new Sender(fileSocket,file);
            sender.start();

            String[] split = path.split("/");
            sendMessage("#PUT " + split[3]);
        }
        else sendMessage("File not uploaded to this chat room.");
    }
}
