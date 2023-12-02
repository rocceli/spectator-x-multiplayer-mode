import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private List<Room> playerRooms;
    private List<Room> spectatorRooms;

    public Server(int port) {
        clients = new ArrayList<>();
        playerRooms = new ArrayList<>();
        spectatorRooms = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from " + clientSocket.getInetAddress());

                // Create a new ClientHandler for the connected client
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);

                // Add the new client handler to the list
                clients.add(clientHandler);

                // Start the client handler in a new thread
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast a message to all connected clients
    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    // Remove a client handler from the list
    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);

        // Remove the client from their room if they are in one
        Room clientRoom = clientHandler.getRoom();
        if (clientRoom != null) {
            clientRoom.removeClient(clientHandler);
        }
    }

    // Find or create a player room
    public Room findOrCreatePlayerRoom(ClientHandler player) {
        for (Room room : playerRooms) {
            if (room.canAddPlayer()) {
                return room;
            }
        }

        // Create a new player room if none is available
        Room newRoom = new Room();
        playerRooms.add(newRoom);
        return newRoom;
    }

    // Find or create a spectator room
    public Room findOrCreateSpectatorRoom(ClientHandler spectator) {
        for (Room room : spectatorRooms) {
            if (room.canAddSpectator()) {
                return room;
            }
        }

        // Create a new spectator room if none is available
        Room newRoom = new Room();
        spectatorRooms.add(newRoom);
        return newRoom;
    }

    public static void main(String[] args) {
        int port = 12345; // You can choose any available port
        new Server(port);
    }
}
