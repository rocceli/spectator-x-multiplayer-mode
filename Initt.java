import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Initt implements ActionListener {
    JFrame frame;
    JPanel background;
    JPanel waitingPanel;
    JPanel waitingRoomPanel; // New waiting room panel
    JButton play;
    JButton spectate;
    JLabel seconds_left;
    int seconds = 10;
    Timer timer;

    public Initt() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setResizable(false);

        // Background panel
        background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon image = new ImageIcon("/home/rocceli/Pictures/Pins/856b8641a212ebed27c55a48c0f28168.jpg"); // Replace
                                                                                                                     // with
                                                                                                                     // your
                                                                                                                     // image
                                                                                                                     // path
                Image img = image.getImage();
                g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };
        background.setLayout(null);

        // Initialize waiting panels
        createWaitingPanel();
        createWaitingRoomPanel(); // New waiting room panel

        // Heading label
        JLabel heading = new JLabel("Stumble Game");
        heading.setFont(new Font("MV Boli", Font.BOLD, 40));
        heading.setForeground(Color.GREEN);
        heading.setBounds(330, 50, 500, 50);

        // Set the 'Play' button at the center
        play = new JButton("Play");
        configureButton(play, 425, 275);

        // Set the 'Spectate' button at the center
        spectate = new JButton("Spectate");
        configureButton(spectate, frame.getWidth() - 170, frame.getHeight() - 115);

        // Add components to the background panel
        background.add(heading);
        background.add(play);
        background.add(spectate);
        background.add(waitingPanel);
        background.add(waitingRoomPanel); // Add new waiting room panel

        // Add the background panel to the frame
        frame.add(background);
        frame.setVisible(true);
    }

    private void configureButton(JButton button, int x, int y) {
        button.setBounds(x, y, 150, 100);
        button.setFont(new Font("MV Boli", Font.BOLD, 35));
        button.setFocusable(false);
        button.addActionListener(this);
        button.setBackground(Color.lightGray);
        button.setBorder(BorderFactory.createLineBorder(Color.green));
    }

    private void createWaitingPanel() {
        waitingPanel = new JPanel();
        waitingPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        waitingPanel.setLayout(null);
        waitingPanel.setOpaque(false);

        // Seconds Left label
        seconds_left = new JLabel();
        seconds_left.setBounds(535, 510, 100, 100);
        seconds_left.setBackground(new Color(25, 25, 25));
        seconds_left.setForeground(new Color(255, 0, 0));
        seconds_left.setFont(new Font("Ink Free", Font.BOLD, 60));
        seconds_left.setBorder(BorderFactory.createBevelBorder(1));
        seconds_left.setOpaque(true);
        seconds_left.setHorizontalAlignment(JTextField.CENTER);
        seconds_left.setVisible(false);

        // Add components to the waiting panel
        waitingPanel.add(seconds_left);
    }

    private void createWaitingRoomPanel() {
        waitingRoomPanel = new JPanel();
        waitingRoomPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        waitingRoomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        waitingRoomPanel.setOpaque(false);

        // JLabel to indicate waiting for room
        JLabel waitingLabel = new JLabel("Waiting for room...");
        waitingLabel.setFont(new Font("MV Boli", Font.BOLD, 30));
        waitingRoomPanel.add(waitingLabel);
        waitingRoomPanel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (timer != null) {
            timer.stop(); // Stop the existing timer if it exists
        }
        seconds = 10; // Reset seconds to 10
        timer = new Timer(1000, ae -> {
            seconds--;
            handleTimerTick();
        });
        timer.start(); // Start the new timer

        if (e.getSource() == play || e.getSource() == spectate) {
            play.setEnabled(false);
            spectate.setEnabled(false);
            waitingPanel.setVisible(false);
            waitingRoomPanel.setVisible(true); // Show the new waiting room panel
            seconds_left.setVisible(true);
            // Simulate successful entrance to the room
            simulateRoomEntrance();
        }
    }

    private void simulateRoomEntrance() {
        // Simulate joining the room after a delay (you can replace this with actual
        // logic)
        SwingUtilities.invokeLater(() -> {
            // Simulate successful room entrance after 3 seconds
            try {
                Thread.sleep(3000);
                // Notify the server about successful entrance
                notifyServerAboutEntrance();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void notifyServerAboutEntrance() {
        // You should replace "localhost" and 12345 with your server's address and port
        String serverAddress = "localhost";
        int serverPort = 12345;

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            DataOutputStream serverOutput = new DataOutputStream(socket.getOutputStream());

            // Send a message to the server indicating successful entrance
            serverOutput.writeUTF("Successfully entered the match room!");

            // Once successfully entered the room, start the countdown
            waitingPanel.setVisible(true);
            waitingRoomPanel.setVisible(false);

            socket.close(); // Close the socket once the connection is successful

        } catch (ConnectException ce) {
            // Handle the case when the connection is not successful
            handleUnsuccessfulConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUnsuccessfulConnection() {
        // Code to handle unsuccessful connection
        // For example, display an error message to the user
        JOptionPane.showMessageDialog(frame, "Failed to connect to the server. Please try again later.", "Connection Error", JOptionPane.ERROR_MESSAGE);
    
        // Use SwingWorker for background task with a shorter delay
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Sleep for 2000 ms (2 seconds)
                Thread.sleep(2000);
                return null;
            }
    
            @Override
            protected void done() {
                // This code will be executed on the EDT after the sleep
                waitingPanel.setVisible(false);
                play.setEnabled(true);
                spectate.setEnabled(true);
            }
        };
    
        worker.execute(); // Start the SwingWorker
    }
       private void handleTimerTick() {
        seconds_left.setText(String.valueOf(seconds));

        if (seconds <= 0) {
            timer.stop(); // Stop the timer when the countdown reaches 0
            play.setEnabled(true); // Enable the 'Play' button
            spectate.setEnabled(true); // Enable the 'Spectate' button
            waitingPanel.setVisible(false);
            waitingRoomPanel.setVisible(false); // Hide the new waiting room panel
            seconds_left.setVisible(false);
        }
    }

}
