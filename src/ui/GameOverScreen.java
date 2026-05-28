package ui;

import javax.swing.*;
import java.awt.*;

public class GameOverScreen extends JPanel {
    private GameWindow gameWindow;

    public GameOverScreen(GameWindow gameWindow) {
        this.gameWindow = gameWindow;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));


        JLabel titleLabel = new JLabel("YOU DIED", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(new Color(180, 30, 30));
        add(titleLabel, BorderLayout.NORTH);


        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel("Your journey ends here... for now.", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 26));
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(120));
        centerPanel.add(messageLabel);

        add(centerPanel, BorderLayout.CENTER);


    }
}