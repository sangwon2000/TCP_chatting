package server;

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

                Server.workMessage(this, input);
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
//------------------------------------------------------------------------------------------
    
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

    // send file to client's fileSocket
    public void sendFile(String path) throws Exception {
        File file = new File(path);
        if(file.exists()) {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            OutputStream os = fileSocket.getOutputStream();

            String[] split = path.split("/");
            sendMessage("#PUT " + split[3]);

            System.out.println("----- send start -----");
            while(bis.available() > 0) {
                byte[] buffer = new byte[65536];
                int length = bis.read(buffer);
                os.write(buffer,0,length);
                System.out.print("#");
            }
            System.out.println("\n----- send complete -----");

        }
        else sendMessage("File not uploaded to this chat room.");
    }
}
