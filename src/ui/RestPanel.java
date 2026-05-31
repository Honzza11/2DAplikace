package ui;

import model.Player;

import javax.swing.*;
import java.awt.*;

/**
 * Panel představující tábořiště (Rest Site / Campfire).
 * Hráč zde má jednorázovou možnost buď odpočívat a vyléčit si 30 % max HP,
 * nebo kovat (Smith) a permanentně vylepšit jednu kartu ze svého balíčku.
 */
public class RestPanel extends JPanel {
    private GameWindow gameWindow;
    private Player player;
    private boolean rested; // Flag zabraňující vykonání více akcí u jednoho táboráku
    private JLabel statsLabel;

    public RestPanel(GameWindow gameWindow, Player player) {
        this.gameWindow = gameWindow;
        this.player = player;
        this.rested = false;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 15, 10)); // Tmavé, hřejivé podbarvení evokující noc u ohně

        // --- HORNÍ PANEL (Indikace místa a aktuální statistiky hráče) ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));

        JLabel areaLabel = new JLabel("Rest Site");
        areaLabel.setFont(new Font("Arial", Font.BOLD, 24));
        areaLabel.setForeground(Color.ORANGE);

        statsLabel = new JLabel("HP: " + player.getHealth() + " / " + player.getMaxHealth() + " | " + player.getGold() + " $");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statsLabel.setForeground(Color.LIGHT_GRAY);

        topBar.add(areaLabel, BorderLayout.WEST);
        topBar.add(statsLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // --- STŘEDOVÝ PANEL (Atmosférické texty a akční tlačítka) ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Campfire");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("A warm campfire offers a moment of respite.");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Výpočet hodnoty léčení (konstantně 30 % z maximálního zdraví hrdiny)
        int healAmount = (int)(player.getMaxHealth() * 0.3);

        // Tlačítko pro odpočinek (léčení)
        JButton restBtn = new JButton("REST (Heal " + healAmount + " HP)");
        restBtn.setFont(new Font("Arial", Font.BOLD, 28));
        restBtn.setBackground(new Color(40, 140, 60));
        restBtn.setForeground(Color.WHITE);
        restBtn.setFocusPainted(false);
        restBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tlačítko pro odchod (zpočátku skryté, objeví se až po vykonání akce)
        JButton leaveBtn = new JButton("PROCEED");
        leaveBtn.setFont(new Font("Arial", Font.BOLD, 28));
        leaveBtn.setBackground(new Color(80, 80, 80));
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.setFocusPainted(false);
        leaveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaveBtn.setVisible(false);

        // Tlačítko pro kovářství (vylepšení karty)
        JButton smithBtn = new JButton("SMITH (Upgrade Card)");
        smithBtn.setFont(new Font("Arial", Font.BOLD, 28));
        smithBtn.setBackground(new Color(140, 60, 40));
        smithBtn.setForeground(Color.WHITE);
        smithBtn.setFocusPainted(false);
        smithBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- AKČNÍ POSLUCHAČE (LOGIKA TLAČÍTEK) ---

        // Logika stisknutí tlačítka REST
        restBtn.addActionListener(e -> {
            if (!rested) {
                player.heal(healAmount); // Vyléčení hráče
                rested = true;

                // Přepnutí viditelnosti prvků rozhraní
                restBtn.setVisible(false);
                smithBtn.setVisible(false);
                leaveBtn.setVisible(true);

                // Změna textu na zelenou barvu potvrzující odpočinek
                subtitleLabel.setText("You feel revitalized.");
                subtitleLabel.setForeground(Color.GREEN);
                updateStats(); // Aktualizace HP v horní liště
            }
        });

        // Logika stisknutí tlačítka SMITH
        smithBtn.addActionListener(e -> {
            if (!rested) {
                // Otevření dialogového okna pro výběr karty k vylepšení
                gameWindow.showSmithDialog(card -> {
                    card.upgrade(); // Volání vylepšovací logiky na zvolené kartě
                    rested = true;

                    // Přepnutí viditelnosti prvků rozhraní
                    restBtn.setVisible(false);
                    smithBtn.setVisible(false);
                    leaveBtn.setVisible(true);

                    // Změna textu na žlutou barvu s názvem vylepšené karty
                    subtitleLabel.setText("You strike the anvil, upgrading " + card.getName() + ".");
                    subtitleLabel.setForeground(Color.YELLOW);
                });
            }
        });

        // Logika stisknutí tlačítka PROCEED (Návrat na mapu)
        leaveBtn.addActionListener(e -> {
            gameWindow.showScreen("MAP");
        });

        // --- SKLÁDÁNÍ KOMPONENT DO STŘEDOVÉHO PANELU ---
        centerPanel.add(Box.createVerticalStrut(100));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(80));
        centerPanel.add(restBtn);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(smithBtn);
        centerPanel.add(leaveBtn);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Pomocná metoda pro překreslení aktuálního stavu životů a zlata v horním rohu panelu.
     */
    private void updateStats() {
        if (statsLabel != null) {
            statsLabel.setText("HP: " + player.getHealth() + " / " + player.getMaxHealth() + " | " + player.getGold() + " $");
        }
    }
}