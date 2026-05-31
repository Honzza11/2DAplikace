package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Vlastní komponenta typu JLabel, která umí vykreslit text s výrazným obrysem (outline).
 * Ideální pro herní titulky, nápisy přes obrázková pozadí, kde by byl běžný bílý text
 * kvůli světlejším místům v grafice nečitelný.
 */
public class OutlinedLabel extends JLabel {
    // Barva obrysu - defaultně nastavená na černou
    private Color outlineColor = new Color(0, 0, 0);
    private int outlineWidth = 3; // Šířka obrysu v pixelech

    /**
     * Konstruktor pro snadné vytvoření stylovaného herního nápisu.
     * * @param text     Text, který se má zobrazit
     * @param fontSize Velikost písma (používá se tučné písmo Arial)
     */
    public OutlinedLabel(String text, int fontSize) {
        super(text);
        setFont(new Font("Arial", Font.BOLD, fontSize));
        setForeground(Color.WHITE); // Hlavní vnitřní barva textu bude bílá
        setHorizontalAlignment(SwingConstants.CENTER); // Automatické centrování textu
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Ignorujeme super.paintComponent(g), protože si text kompletně kreslíme sami
        Graphics2D g2 = (Graphics2D) g;

        // Aktivace antialiasingu pro hladké hrany písma a eliminaci zubatých okrajů
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String text = getText();
        FontMetrics fm = g2.getFontMetrics();

        // Matematický výpočet souřadnic pro dokonalé vycentrování textu uvnitř hranic labelu
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        // --- 1. KROK: VYKRESLENÍ OBRYSU ---
        // Nastavíme barvu obrysu
        g2.setColor(outlineColor);

        // Vnořené cykly projedou okolí cílových souřadnic od -outlineWidth do +outlineWidth.
        // Vykreslením textu na všechny tyto posunuté pozice vznikne souvislý hrubý obrys.
        for (int i = -outlineWidth; i <= outlineWidth; i++) {
            for (int j = -outlineWidth; j <= outlineWidth; j++) {
                // Podmínka vynechává přesný střed (0,0), kam přijde hlavní text v dalším kroku
                if (i != 0 || j != 0) {
                    g2.drawString(text, x + i, y + j);
                }
            }
        }

        // --- 2. KROK: VYKRESLENÍ HLAVNÍHO TEXTU ---
        // Nastavíme popředí (bílou barvu) a překryjeme střed připraveného černého obrysu
        g2.setColor(getForeground());
        g2.drawString(text, x, y);
    }
}