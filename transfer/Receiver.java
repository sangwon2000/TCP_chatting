package transfer;

import java.io.*;
import java.net.Socket;

public class Receiver extends Thread {

    private Socket socket; // fileSocket used for receiving
    private File file; // location of file to store

    public Receiver(Socket socket, File file) {
        this.socket = socket;
        this.file = file;
    }

    public void run() {
        try {
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[65536]; // receive 64KB breaks each

            System.out.println("----- receive start -----");
            while(true) {
                int length = bis.read(buffer);
                fos.write(buffer,0,length);
                System.out.print("#");
                if(length < 65536) break;
            }
            System.out.println("\n----- receive complete -----");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
