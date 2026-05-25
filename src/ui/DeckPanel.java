package ui;

import model.Card;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DeckPanel extends JPanel {
    private List<Card> cards;
    private static final int CARD_WIDTH = 130;
    private static final int CARD_HEIGHT = 185;
    private static final int SPACING = 20;

    public DeckPanel(List<Card> cards) {
        this.cards = cards;
        setBackground(new Color(20, 20, 30));
        setLayout(null);
        updatePreferredSize(800);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updatePreferredSize(getWidth());
            }
        });
    }

    private void updatePreferredSize(int panelWidth) {
        if (panelWidth <= 0) panelWidth = 800;
        int cols = Math.max(1, (panelWidth - SPACING) / (CARD_WIDTH + SPACING));
        int rows = (int) Math.ceil((double) cards.size() / cols);
        int preferredHeight = rows * (CARD_HEIGHT + SPACING) + SPACING + 40;
        setPreferredSize(new Dimension(panelWidth, preferredHeight));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int cols = Math.max(1, (w - SPACING) / (CARD_WIDTH + SPACING));


        int gridWidth = cols * (CARD_WIDTH + SPACING) - SPACING;
        int startX = (w - gridWidth) / 2;
        int startY = SPACING + 10;

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            int row = i / cols;
            int col = i % cols;

            int x = startX + col * (CARD_WIDTH + SPACING);
            int y = startY + row * (CARD_HEIGHT + SPACING);

            drawDeckCard(g2, card, x, y);
        }
    }

    private void drawDeckCard(Graphics2D g2, Card card, int x, int y) {

        g2.setColor(new Color(35, 35, 50));
        g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);

        g2.setColor(card.getType() == Card.CardType.ATTACK ? new Color(180, 50, 50) : new Color(50, 120, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);


        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(card.getName(), x + (CARD_WIDTH - fmTitle.stringWidth(card.getName())) / 2, y + 25);
        

        g2.setColor(new Color(80, 80, 100));
        g2.drawLine(x + 15, y + 38, x + CARD_WIDTH - 15, y + 38);


        g2.setColor(card.getType() == Card.CardType.ATTACK ? new Color(180, 50, 50, 80) : new Color(50, 120, 180, 80));
        g2.fillRoundRect(x + 15, y + 45, CARD_WIDTH - 30, 16, 4, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        String typeText = card.getType().toString();
        FontMetrics fmType = g2.getFontMetrics();
        g2.drawString(typeText, x + (CARD_WIDTH - fmType.stringWidth(typeText)) / 2, y + 57);

        g2.setColor(new Color(200, 200, 220));
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        String desc = card.getDescription();
        if (desc != null && !desc.isEmpty()) {
            String[] words = desc.split(" ");
            StringBuilder currentLine = new StringBuilder();
            int lineY = y + 85;
            int maxTextWidth = CARD_WIDTH - 20;
            
            for (String word : words) {
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                FontMetrics fm = g2.getFontMetrics();
                if (fm.stringWidth(testLine) > maxTextWidth) {
                    g2.drawString(currentLine.toString(), x + 10, lineY);
                    lineY += 14;
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine = new StringBuilder(testLine);
                }
            }
            if (currentLine.length() > 0) {
                g2.drawString(currentLine.toString(), x + 10, lineY);
            }
        }


        g2.setColor(new Color(230, 90, 40));
        g2.fillOval(x - 8, y - 8, 24, 24);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x - 8, y - 8, 24, 24);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        String costStr = String.valueOf(card.getEnergyCost());
        FontMetrics fmCost = g2.getFontMetrics();
        g2.drawString(costStr, x - 8 + (24 - fmCost.stringWidth(costStr)) / 2, y - 8 + ((24 - fmCost.getHeight()) / 2) + fmCost.getAscent());
    }
}
