package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel reprezentující obrazovku "Game Over" (Konec hry).
 * Zobrazí se v momentě, kdy zdraví hráče klesne na nulu nebo méně.
 * Využívá temný, rudo-černý vizuální styl a atmosférický text.
 */
public class GameOverScreen extends JPanel {
    private GameWindow gameWindow;

    public GameOverScreen(GameWindow gameWindow) {
        this.gameWindow = gameWindow;

        // Nastavení rozvržení s vnitřním okrajem (paddingem) 50 pixelů ze všech stran
        setLayout(new BorderLayout());
        setBackground(new Color(20, 10, 10)); // Velmi tmavě červené/černé pozadí
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // --- HLAVNÍ TITULEK (Nahoře) ---
        JLabel titleLabel = new JLabel("YOU DIED", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(new Color(180, 30, 30)); // Temně červená barva nápisu
        add(titleLabel, BorderLayout.NORTH);

        // --- STŘEDOVÝ PANEL PRO DOPROVODNÝ TEXT ---
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false); // Průhledný panel, aby prosvítalo tmavé pozadí
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); // Řazení prvků vertikálně pod sebe

        // Podtitulek s atmosférickým textem
        JLabel messageLabel = new JLabel("Your journey ends here... for now.", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 26));
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Vycentrování na střed osy X

        // Vytvoření vertikálního prostoru (odsazení) mezi titulkem a podtitulkem
        centerPanel.add(Box.createVerticalStrut(120));
        centerPanel.add(messageLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Poznámka: Zde je prostor pro případné přidání tlačítek "Main Menu" nebo "Try Again"
    }
}