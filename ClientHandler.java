import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private Server server;
    private String role; // Player or Spectator
    private Room room; // The room the client is part of

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

            // Receive the role information from the client
            this.role = input.readUTF();
            System.out.println("Client role: " + role);

            // Handle joining the room based on the client's role
            if ("player".equals(role)) {
                joinPlayerRoom();
            } else if ("spectator".equals(role)) {
                joinSpectatorRoom();
            }

            // Notify successful entrance to the match room
            notifySuccess();

            // Continuously listen for messages from the client
            while (true) {
                String message = input.readUTF();
                System.out.println("Received message from client: " + message);

                // Handle the received message, e.g., check for the end of the game signal
                if ("end_game".equals(message)) {
                    // Perform actions for ending the game
                    break; // Exit the loop if the game has ended
                }

                // You can add more logic to handle other messages from the client
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the resources when needed
                if (room != null) {
                    room.removeClient(this);
                }
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void joinPlayerRoom() {
        Room playerRoom = server.findOrCreatePlayerRoom(this);
        if (playerRoom != null) {
            this.room = playerRoom;
            playerRoom.addPlayer(this);
            System.out.println("Player joined room: " + playerRoom.getRoomId());
        } else {
            System.out.println("No available rooms. Cannot join.");
        }
    }
    

    private void joinSpectatorRoom() {
        // Try to join an existing room or create a new one
        Room spectatorRoom = server.findOrCreateSpectatorRoom(this);
        if (spectatorRoom != null) {
            this.room = spectatorRoom;
            spectatorRoom.addSpectator(this);
            System.out.println("Spectator joined room: " + spectatorRoom.getRoomId());
        } else {
            // Handle the case when there are no available rooms
            System.out.println("No available rooms. Cannot join.");
        }
    }

    // Add the setRoom method
    public void setRoom(Room room) {
        this.room = room;
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

    // Getter for the client's role
    public String getRole() {
        return role;
    }
    public Room getRoom() {
        return room;
    }
}
