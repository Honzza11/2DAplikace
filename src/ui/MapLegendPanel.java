package ui;

import javax.swing.*;
import java.awt.*;

public class MapLegendPanel extends JPanel {
    
    public MapLegendPanel() {
        setPreferredSize(new Dimension(300, 290));
        setBackground(new Color(240, 218, 181));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int lx = 25;
        int ly = 20;

        g2.setColor(new Color(255, 255, 255, 230));
        g2.fillRoundRect(lx, ly, 230, 240, 15, 15);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(lx, ly, 230, 240, 15, 15);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("MAP LEGEND", lx + 60, ly + 25);

        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        int yOffset = 60;
        drawLegendItem(g2, "😡", "Enemy", lx + 20, ly + yOffset);
        drawLegendItem(g2, "❓", "Random Event", lx + 20, ly + yOffset + 30);
        drawLegendItem(g2, "👿", "Enhanced Enemy", lx + 20, ly + yOffset + 60);
        drawLegendItem(g2, "🔥", "Rest Campfire", lx + 20, ly + yOffset + 90);
        drawLegendItem(g2, "💎", "Treasure", lx + 20, ly + yOffset + 120);
        drawLegendItem(g2, "⚔️", "Boss Battle", lx + 20, ly + yOffset + 150);
    }

    private void drawLegendItem(Graphics2D g2, String symbol, String desc, int x, int y) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillOval(x, y - 15, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawString(symbol, x + 5, y);
        g2.setColor(Color.BLACK);
        g2.drawString(desc, x + 30, y);
    }
}
