import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            this.input = new DataInputStream(clientSocket.getInputStream());
            this.output = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Handle communication with the client here
            // You can send and receive messages using input and output streams

            // Example: Send a welcome message to the client
            output.writeUTF("Welcome to the Quiz Game!");

            // Implement game logic here

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the resources when the client disconnects
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

