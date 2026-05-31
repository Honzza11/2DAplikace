package ui;

import model.Player;
import javax.swing.*;
import java.awt.*;

/**
 * Závěrečná obrazovka vítězství (Win / Victory Screen).
 * Zobrazuje se v okamžiku, kdy hráč úspěšně dokončí celou hru.
 * Vypisuje atmosférický text o dokončení a jméno postavy, za kterou hráč hrál.
 */
public class WinScreen extends JPanel {
    private GameWindow gameWindow;

    public WinScreen(GameWindow gameWindow, Player player) {
        this.gameWindow = gameWindow;

        setLayout(new BorderLayout());
        setBackground(new Color(15, 30, 20)); // Temně zelené podbarvení symbolizující úspěch a bezpečí
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // --- HLAVNÍ TITULEK OBRAZOVKY ---
        JLabel titleLabel = new JLabel("VICTORY!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(new Color(50, 205, 50)); // Jasně zelená barva (Lime Green)
        add(titleLabel, BorderLayout.NORTH);

        // --- STŘEDOVÝ PANEL S PODROBNOSTMI ---
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Atmosférický podtext (příběhové zakončení)
        JLabel subtitleLabel = new JLabel("The Pyre has been extinguished.", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 28));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Zjištění jména hrdiny (ošetřeno proti null hodnotě)
        String heroName = player != null ? player.getName() : "Hero";
        JLabel heroLabel = new JLabel("Completed as: " + heroName, SwingConstants.CENTER);
        heroLabel.setFont(new Font("Arial", Font.BOLD, 24));
        heroLabel.setForeground(Color.YELLOW);
        heroLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Skládání komponent pod sebe s vertikálními rozestupy
        centerPanel.add(Box.createVerticalStrut(100));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(heroLabel);

        add(centerPanel, BorderLayout.CENTER);
    }
}