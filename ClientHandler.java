import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private Server server;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;

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

            // Example: Receive the role information from the client
            String role = input.readUTF();
            System.out.println("Client role: " + role);

            // Implement game logic here based on the client's role
            if ("player".equals(role)) {
                handlePlayerLogic();
            } else if ("spectator".equals(role)) {
                handleSpectatorLogic();
            }

            // Notify successful entrance to the match room
            notifySuccess();

            // Continuously listen for messages from the client
            while (true) {
                String message = input.readUTF();
                System.out.println("Received message from client: " + message);

                // Handle the received message, e.g., check for end of game signal
                if ("end_game".equals(message)) {
                    // Perform actions for ending the game
                    break;  // exit the loop if the game has ended
                }

                // You can add more logic to handle other messages from the client
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the resources when needed
                server.removeClient(this);
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePlayerLogic() {
        // Implement logic for players
        System.out.println("Player Logic");
    }

    private void handleSpectatorLogic() {
        // Implement logic for spectators
        System.out.println("Spectator Logic");
    }

    private void notifySuccess() {
        try {
            // Send a confirmation message to the client
            output.writeUTF("Successfully entered the match room!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send a message to this client
    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
