package client;

import transfer.Receiver;
import transfer.Sender;

import java.io.*;
import java.net.Socket;

public class Agent extends Thread {

    private Socket chatSocket; // used for chatting
    private Socket fileSocket; // used for file transmission

    public Agent(Socket chatSocket, Socket fileSocket) {
        this.chatSocket = chatSocket;
        this.fileSocket = fileSocket;
    }

    public void run() {
        try {
            while(true) {

                // receive message from server's chatSocket
                BufferedReader br = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
                String input = br.readLine();
                String[] split = input.split(" ");

                // if server send PUT command, client prepare to receive file
                if(split[0].equalsIgnoreCase("#PUT"))
                    receiveFile(split[1]);
                // otherwise, print server's message
                else System.out.println(input);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
//-----------------------------------------------------------------------------------

    // send message to server's chatSocket
    public void sendMessage(String message) throws Exception {
        DataOutputStream dos = new DataOutputStream(chatSocket.getOutputStream());
        dos.writeBytes(message + "\n");
    }

    //send file to server's fileSocket
    public void sendFile(String fileName) throws Exception {
        File file = new File("client/file/" + fileName);
        if(file.exists()) {
            sendMessage("#PUT " + fileName);
            Sender sender = new Sender(fileSocket,file);
            sender.start();
        }
        else System.out.println("You don't have a file with that name.");
    }

    // receive file from server's fileSocket
    public void receiveFile(String fileName) throws Exception {
        File file = new File("client/file/" + fileName);
        if(file.exists()) file.delete();
        file.createNewFile();

        Receiver receiver = new Receiver(fileSocket,file);
        receiver.start();
    }
}
