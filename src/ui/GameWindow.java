package ui;

import utils.LanguageManager;
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    private JLabel menuLabel;
    private JButton languageButton;
    private JLabel mapLabel;
    private JLabel combatLabel;

    public GameWindow() {
        setTitle("Kill the Pyre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(menuLabel);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Menu Panel
        JPanel menuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        menuLabel = new JLabel(LanguageManager.getString("main_menu"));
        menuLabel.setFont(new Font("Arial", Font.BOLD, 32));
        menuPanel.add(menuLabel, gbc);

        gbc.gridy = 1;
        languageButton = new JButton(LanguageManager.getString("change_language"));
        languageButton.addActionListener(e -> {
            LanguageManager.toggleLanguage();
            updateUIStrings();
        });
        menuPanel.add(languageButton, gbc);

        // Placeholder panels
        JPanel mapPanel = new JPanel(new GridBagLayout());
        mapLabel = new JLabel(LanguageManager.getString("map_view"));
        mapLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mapPanel.add(mapLabel);

        JPanel combatPanel = new JPanel(new GridBagLayout());
        combatLabel = new JLabel(LanguageManager.getString("combat_screen"));
        combatLabel.setFont(new Font("Arial", Font.BOLD, 24));
        combatPanel.add(combatLabel);

        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(mapPanel, "MAP");
        mainContainer.add(combatPanel, "COMBAT");

        add(mainContainer);
        showScreen("MENU");
    }

    private void updateUIStrings() {
        menuLabel.setText(LanguageManager.getString("main_menu"));
        languageButton.setText(LanguageManager.getString("change_language"));
        mapLabel.setText(LanguageManager.getString("map_view"));
        combatLabel.setText(LanguageManager.getString("combat_screen"));
        revalidate();
        repaint();
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }

}
