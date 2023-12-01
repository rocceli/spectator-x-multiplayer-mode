import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        String serverAddress = "localhost"; // Change this to the actual IP address or hostname of your server
        int serverPort = 12345; // Use the same port number as your server

        try {
            // Connect to the server
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to the server.");

            // Set up input and output streams
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Receive and print the welcome message from the server
            String welcomeMessage = input.readUTF();
            System.out.println("Server says: " + welcomeMessage);

            // Implement client logic here

            // Close the connection
            socket.close();
            System.out.println("Connection closed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
