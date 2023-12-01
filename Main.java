import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        String serverAddress = "localhost"; // Change this to the actual IP address or hostname of your server
        int serverPort = 12345; // Use the same port number as your server

        Socket socket = null;

        try {
            // Connect to the server
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to the server.");

            // Set up input and output streams
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Receive and print the welcome message from the server
            String welcomeMessage = input.readUTF();
            System.out.println("Server says: " + welcomeMessage);

            // Implement client logic here
            Quiz quiz = new Quiz();

            // Ensure that the Quiz instance is finished before closing the socket
            quiz.join(); // Assuming Quiz extends Thread or implements Runnable

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the connection in a finally block to ensure it happens even if an exception occurs
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("Connection closed.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
