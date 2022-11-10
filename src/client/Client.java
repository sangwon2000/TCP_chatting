package client;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static Agent agent;

    public static void main(String[] args) {
        try {
            // IP, port setting
            InetAddress serverAddress = InetAddress.getByName(args[0]);
            InetAddress clientAddress = InetAddress.getByName("localhost");
            int serverPort1 = 1004;
            int serverPort2 = 1005;
            int clientPort1 = Integer.parseInt(args[1]);
            int clientPort2 = Integer.parseInt(args[2]);

            // create folder for file transmission
            File folder = new File("client/file");
            if(!folder.exists()) folder.mkdirs();

            // chatSocket used for chatting, file Socket used for file transmission
            Socket chatSocket = new Socket(serverAddress, serverPort1,null,clientPort1);
            Socket fileSocket = new Socket(serverAddress, serverPort2, null, clientPort2);

            // Agent Thread help TCP send and receive
            agent = new Agent(chatSocket, fileSocket);
            agent.start();

            System.out.println("***** welcome to chatting server *****");

            // get input from user
            Scanner scan = new Scanner(System.in);
            while(true) {
                String input = scan.nextLine();
                String[] split = input.split(" ");

                // PUT command
                if(split[0].equalsIgnoreCase("#PUT")) agent.sendFile(split[1]);
                // other command or message
                else agent.sendMessage(input);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
