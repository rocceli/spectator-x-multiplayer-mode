import java.util.ArrayList;
import java.util.List;

public class Room {
    private static int roomCounter = 1;
    private int roomId;
    private List<ClientHandler> players = new ArrayList<>();
    private List<ClientHandler> spectators = new ArrayList<>();
    private static final int MAX_PLAYERS = 10;
    private static final int MAX_SPECTATORS = 10;

    public Room() {
        this.roomId = roomCounter++;
    }

    public int getRoomId() {
        return roomId;
    }

    public synchronized boolean canAddPlayer() {
        return players.size() < MAX_PLAYERS;
    }

    public synchronized boolean canAddSpectator() {
        return spectators.size() < MAX_SPECTATORS;
    }

    public synchronized void addPlayer(ClientHandler player) {
        if (canAddPlayer()) {
            players.add(player);
            player.setRoom(this);

            // Check if the room has at least two players and start the game
            if (players.size() >= 2) {
                startGame();
            }
        } else {
            System.out.println("Cannot add player to the room. Room is full for players.");
        }
    }

    public synchronized void addSpectator(ClientHandler spectator) {
        if (canAddSpectator()) {
            spectators.add(spectator);
            spectator.setRoom(this);
        } else {
            System.out.println("Cannot add spectator to the room. Room is full for spectators.");
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        players.remove(client);
        spectators.remove(client);
        client.setRoom(null);
    }

    private void startGame() {
        for (ClientHandler player : players) {
            player.sendMessage("start_game");
        }
    }
}
