import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Initt implements ActionListener {
    JFrame frame = new JFrame();
    JButton play = new JButton();
    JButton spectate = new JButton();
    JLabel seconds_left = new JLabel();
    int seconds = 10;
    Timer timer;
    Socket socket;
    DataInputStream input;
    DataOutputStream output;

    public Initt() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setResizable(false);

        // Background panel
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon image = new ImageIcon("/home/rocceli/Pictures/Pins/856b8641a212ebed27c55a48c0f28168.jpg"); // Replace with your image path
                Image img = image.getImage();
                g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };
        background.setLayout(null);

        // Heading label
        JLabel heading = new JLabel("Stumble Game");
        heading.setFont(new Font("MV Boli", Font.BOLD, 40));
        heading.setForeground(Color.GREEN);
        heading.setBounds(330, 50, 500, 50);

        // Set the 'Play' button at the center
        play.setBounds(425, 275, 150, 100);
        play.setFont(new Font("MV Boli", Font.BOLD, 35));
        play.setFocusable(false);
        play.addActionListener(this);
        play.setText("Play");
        play.setBackground(Color.lightGray);
        play.setBorder(BorderFactory.createLineBorder(Color.green));

        seconds_left.setBounds(535, 510, 100, 100);
        seconds_left.setBackground(new Color(25, 25, 25));
        seconds_left.setForeground(new Color(255, 0, 0));
        seconds_left.setFont(new Font("Ink Free", Font.BOLD, 60));
        seconds_left.setBorder(BorderFactory.createBevelBorder(1));
        seconds_left.setOpaque(true);
        seconds_left.setHorizontalAlignment(JTextField.CENTER);
        seconds_left.setText(String.valueOf(seconds));

        int buttonWidth = 150;
        int buttonHeight = 35;
        int buttonX = frame.getWidth() - buttonWidth - 20;
        int buttonY = frame.getHeight() - buttonHeight - 50;
        spectate.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        spectate.setFont(new Font("MV Boli", Font.BOLD, 20));
        spectate.setFocusable(false);
        spectate.addActionListener(this);
        spectate.setText("Spectate");
        spectate.setBackground(Color.lightGray);
        spectate.setBorder(BorderFactory.createLineBorder(Color.green));

        // Add components to the background panel
        background.add(heading);
        background.add(seconds_left);
        background.add(play);
        background.add(spectate);

        // Add the background panel to the frame
        frame.add(background);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (timer != null) {
            timer.stop(); // Stop the existing timer if it exists
        }
        seconds = 10; // Reset seconds to 10
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                seconds--;
                handleTimerTick();
            }
        });
        timer.start(); // Start the new timer

        try {
            socket = new Socket("localhost", 12345);  // Replace with your server details
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            if (e.getSource() == play) {
                play.setEnabled(false);
                spectate.setEnabled(false);

                // Send the role information to the server
                output.writeUTF("player");
            } else if (e.getSource() == spectate) {
                play.setEnabled(false);
                spectate.setEnabled(false);

                // Send the role information to the server
                output.writeUTF("spectator");
            }

            // Read the confirmation message from the server
            String confirmationMessage = input.readUTF();
            System.out.println("Server message: " + confirmationMessage);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void handleTimerTick() {
        seconds_left.setText(String.valueOf(seconds));

        if (seconds <= 0) {
            timer.stop(); // Stop the timer when the countdown reaches 0
            play.setEnabled(true); // Enable the 'Play' button
            spectate.setEnabled(true); // Enable the 'Spectate' button
        }
    }

    public static void main(String[] args) {
        new Initt();
    }
}
