package ui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public GameWindow() {
        setTitle("Kill the Pyre - Roguelike Deck-Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Placeholder panels for different game states
        mainContainer.add(createPlaceholderPanel("Main Menu"), "MENU");
        mainContainer.add(createPlaceholderPanel("Map View"), "MAP");
        mainContainer.add(createPlaceholderPanel("Combat Screen"), "COMBAT");

        add(mainContainer);
        showScreen("MENU");
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);
        return panel;
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}
