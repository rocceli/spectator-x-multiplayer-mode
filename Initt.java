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
    JPanel waitingRoomPanel;
    JButton play;
    JButton spectate;
    JLabel seconds_left;
    int seconds = 10;
    Timer timer;
    private JButton clickedButton;

    public Initt() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setResizable(false);

        background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon image = new ImageIcon("/home/rocceli/Pictures/Pins/856b8641a212ebed27c55a48c0f28168.jpg");
                Image img = image.getImage();
                g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };
        background.setLayout(null);

        createWaitingPanel();
        createWaitingRoomPanel();

        JLabel heading = new JLabel("Stumble Game");
        heading.setFont(new Font("MV Boli", Font.BOLD, 40));
        heading.setForeground(Color.GREEN);
        heading.setBounds(330, 50, 500, 50);

        play = new JButton("Play");
        configureButton(play, 425, 275);

        spectate = new JButton("Spectate");
        configureButton(spectate, frame.getWidth() - 170, frame.getHeight() - 115);

        background.add(heading);
        background.add(play);
        background.add(spectate);
        background.add(waitingPanel);
        background.add(waitingRoomPanel);

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

        seconds_left = new JLabel();
        seconds_left.setBounds(535, 510, 100, 100);
        seconds_left.setBackground(new Color(25, 25, 25));
        seconds_left.setForeground(new Color(255, 0, 0));
        seconds_left.setFont(new Font("Ink Free", Font.BOLD, 60));
        seconds_left.setBorder(BorderFactory.createBevelBorder(1));
        seconds_left.setOpaque(true);
        seconds_left.setHorizontalAlignment(JTextField.CENTER);
        seconds_left.setVisible(false);

        waitingPanel.add(seconds_left);
    }

    private void createWaitingRoomPanel() {
        waitingRoomPanel = new JPanel();
        waitingRoomPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        waitingRoomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        waitingRoomPanel.setOpaque(false);

        JLabel waitingLabel = new JLabel("Waiting for room...");
        waitingLabel.setFont(new Font("MV Boli", Font.BOLD, 30));
        waitingRoomPanel.add(waitingLabel);
        waitingRoomPanel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (timer != null) {
            timer.stop();
        }
        seconds = 10;
        timer = new Timer(1000, ae -> {
            seconds--;
            handleTimerTick();
        });
        timer.start();

        clickedButton = (JButton) e.getSource();

        if (clickedButton == play || clickedButton == spectate) {
            play.setEnabled(false);
            spectate.setEnabled(false);
            waitingPanel.setVisible(false);
            waitingRoomPanel.setVisible(true);
            seconds_left.setVisible(true);

            simulateRoomEntrance();
        }
    }

    private void simulateRoomEntrance() {
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(3000);
                notifyServerAboutEntrance();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void notifyServerAboutEntrance() {
        String serverAddress = "localhost";
        int serverPort = 12345;

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            DataOutputStream serverOutput = new DataOutputStream(socket.getOutputStream());

            String role = (clickedButton == play) ? "player" : "spectator";
            serverOutput.writeUTF(role);

            waitingPanel.setVisible(true);
            waitingRoomPanel.setVisible(false);
             new Thread(() -> {
            try {
                DataInputStream serverInput = new DataInputStream(socket.getInputStream());
                while (true) {
                    String message = serverInput.readUTF();
                    // Check if the received message is "start_game"
                    if ("start_game".equals(message)) {
                        handleStartGameSignal();
                    }
                    // Handle other messages as needed
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        } catch (ConnectException ce) {
            handleUnsuccessfulConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handleStartGameSignal() {
        // Handle the "start_game" signal, for example, by starting the game
        System.out.println("Received start_game signal. Starting the game!");
        // Add your logic to start the game here
    }

    private void handleUnsuccessfulConnection() {
        JOptionPane.showMessageDialog(frame, "Failed to connect to the server. Please try again later.",
                "Connection Error", JOptionPane.ERROR_MESSAGE);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                waitingPanel.setVisible(false);
                Thread.sleep(3000);
                return null;
            }

            @Override
            protected void done() {
                play.setEnabled(true);
                spectate.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void handleTimerTick() {
        seconds_left.setText(String.valueOf(seconds));

        if (seconds <= 0) {
            timer.stop();
            play.setEnabled(true);
            spectate.setEnabled(true);
            waitingPanel.setVisible(false);
            waitingRoomPanel.setVisible(false);
            seconds_left.setVisible(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Initt());
    }
}
