package client;

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

                // if server sent PUT command, client prepare to receive file
                if(split[0].equalsIgnoreCase("#PUT"))
                    receiveFile(split[1]);
                // otherwise, print the message that server sent
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

    // receive file from server's fileSocket
    public void receiveFile(String fileName) throws Exception {
        File file = new File("client/file/" + fileName);
        if(file.exists()) file.delete();
        file.createNewFile();

        BufferedInputStream bis = new BufferedInputStream(fileSocket.getInputStream());
        FileOutputStream fos = new FileOutputStream(file);

        System.out.println("----- receive start -----");
        while(bis.available() > 0) {
            Thread.sleep(100);
            byte[] buffer = new byte[65536];
            int length = bis.read(buffer);
            fos.write(buffer,0,length);
            System.out.print("#");
        }
        System.out.println("\n----- receive complete -----");
    }

    //send file to server's fileSocket
    public void sendFile(String fileName) throws Exception {
        File file = new File("client/file/" + fileName);
        if(file.exists()) {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            OutputStream os = fileSocket.getOutputStream();

            sendMessage("#PUT " + fileName);

            System.out.println("----- send start -----");
            while(bis.available() > 0) {
                byte[] buffer = new byte[65536];
                int length = bis.read(buffer);
                os.write(buffer,0,length);
                System.out.print("#");
            }
            System.out.println("\n----- send complete -----");
        }
        else System.out.println("You don't have a file with that name.");
    }    
}
