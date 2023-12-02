import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;

    public Server(int port) {
        clients = new ArrayList<>();
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
    }

    // Get the count of connected clients
    public int getConnectedClientsCount() {
        return clients.size();
    }

    public static void main(String[] args) {
        int port = 12345; // You can choose any available port
        new Server(port);
    }
}
