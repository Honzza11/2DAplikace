package ui;

import javax.swing.*;
import java.awt.*;

public class OutlinedLabel extends JLabel {
    private Color outlineColor = new Color(255, 140, 0); // Orange
    private int outlineWidth = 3;

    public OutlinedLabel(String text, int fontSize) {
        super(text);
        setFont(new Font("Arial", Font.BOLD, fontSize));
        setForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String text = getText();
        FontMetrics fm = g2.getFontMetrics();

        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        g2.setColor(outlineColor);
        for (int i = -outlineWidth; i <= outlineWidth; i++) {
            for (int j = -outlineWidth; j <= outlineWidth; j++) {
                if (i != 0 || j != 0) {
                    g2.drawString(text, x + i, y + j);
                }
            }
        }

        g2.setColor(getForeground());
        g2.drawString(text, x, y);
    }
}
