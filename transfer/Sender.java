package transfer;

import java.io.*;
import java.net.Socket;

public class Sender extends Thread {
    private Socket socket;
    private File file;

    public Sender(Socket socket, File file) {
        this.socket = socket; // fileSocket used for sending
        this.file = file; // location of file to send
    }

    public void run() {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            OutputStream os = socket.getOutputStream();
            byte[] buffer = new byte[65536]; // send 64KB breaks each

            System.out.println("----- send start -----");
            while(true) {
                int length = bis.read(buffer);
                os.write(buffer,0,length);
                System.out.print("#");
                if(length < 65536) break;
            }
            System.out.println("\n----- send complete -----");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
