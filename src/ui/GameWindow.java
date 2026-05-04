package ui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    private JLabel menuLabel;
    private JLabel mapLabel;
    private JLabel combatLabel;

    public GameWindow() {
        setTitle("Kill the Pyre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null); // Center on screen

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        BackgroundPanel menuPanel = new BackgroundPanel("Res/kill the pyre background menu.jpg");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        menuLabel = new JLabel("Main Menu");
        menuLabel.setFont(new Font("Arial", Font.BOLD, 32));
        menuLabel.setForeground(Color.WHITE); // Make it readable on background
        menuPanel.add(menuLabel, gbc);

        // Placeholder panels
        JPanel mapPanel = new JPanel(new GridBagLayout());
        mapLabel = new JLabel("Map View");
        mapLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mapPanel.add(mapLabel);

        JPanel combatPanel = new JPanel(new GridBagLayout());
        combatLabel = new JLabel("Combat Screen");
        combatLabel.setFont(new Font("Arial", Font.BOLD, 24));
        combatPanel.add(combatLabel);

        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(mapPanel, "MAP");
        mainContainer.add(combatPanel, "COMBAT");

        add(mainContainer);
        showScreen("MENU");
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }
}
