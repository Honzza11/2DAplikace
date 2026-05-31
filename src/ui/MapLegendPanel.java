package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel představující legendu k herní mapě.
 * Zobrazuje přehledný seznam ikon (emoji) a jejich významů (nepřátelé, obchody, odpočinek atd.).
 * Využívá přímé kreslení přes Graphics2D pro přesné vycentrování symbolů v kruzích.
 */
public class MapLegendPanel extends JPanel {

    public MapLegendPanel() {
        // Nastavení fixních rozměrů, aby se do okna pohodlně vešly všechny typy místností včetně obchodu
        setPreferredSize(new Dimension(300, 330));
        setBackground(new Color(240, 218, 181)); // Podkladová barva imitující pergamen/starý papír
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Aktivace antialiasingu pro hladké hrany kruhů a textu
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int lx = 25;
        int ly = 20;

        // 1. Vykreslení poloprůhledného bílého boxu na pozadí legendy
        g2.setColor(new Color(255, 255, 255, 230));
        g2.fillRoundRect(lx, ly, 230, 270, 15, 15);

        // Černé ohraničení boxu legendy
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(lx, ly, 230, 270, 15, 15);

        // 2. Nadpis legendy
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("MAP LEGEND", lx + 60, ly + 25);

        int yOffset = 60;

        // 3. Vykreslení jednotlivých položek legendy s vertikálními rozestupy po 30 pixelech
        drawLegendItem(g2, "😡", "Enemy", lx + 20, ly + yOffset);
        drawLegendItem(g2, "❓", "Random Event", lx + 20, ly + yOffset + 30);
        drawLegendItem(g2, "👿", "Enhanced Enemy", lx + 20, ly + yOffset + 60);
        drawLegendItem(g2, "🔥", "Rest Campfire", lx + 20, ly + yOffset + 90);
        drawLegendItem(g2, "💎", "Treasure", lx + 20, ly + yOffset + 120);
        drawLegendItem(g2, "💰", "Merchant / Shop", lx + 20, ly + yOffset + 150); // Přidán Shop s pytlem peněz
        drawLegendItem(g2, "   ⚔️", "Boss Battle", lx + 20, ly + yOffset + 180);
    }

    /**
     * Pomocná metoda pro vykreslení jednoho řádku legendy (kruh s emoji + textový popis).
     */
    private void drawLegendItem(Graphics2D g2, String symbol, String desc, int x, int y) {
        // Vykreslení tmavě šedého kruhového pozadí pro ikonu
        g2.setColor(Color.DARK_GRAY);
        g2.fillOval(x, y - 18, 24, 24);

        // Záloha původního fontu před přepnutím na emoji font
        Font originalFont = g2.getFont();
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Podpora zobrazení emoji symbolů
        g2.setColor(Color.WHITE);

        // Výpočet matematického středu kruhu pro dokonalé vycentrování emoji textu
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (24 - fm.stringWidth(symbol)) / 2;
        int textY = (y - 18) + (24 / 2) + (fm.getAscent() - fm.getDescent()) / 2;

        // Drobná optická korekce pozice pro specifický symbol zkřížených mečů
        if (symbol.contains("⚔️")) {
            textX += 1;
        }

        // Vykreslení ikony (emoji) do středu kruhu
        g2.drawString(symbol, textX, textY);

        // Vykreslení textového popisu vedle kruhu
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(desc, x + 35, y - 2);

        // Obnovení původního fontu pro zbytek kreslení
        g2.setFont(originalFont);
    }
}