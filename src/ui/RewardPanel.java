package ui;

import model.Card;
import model.Player;
import model.Relic;
import model.CardLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RewardPanel extends JPanel {
    private GameWindow gameWindow;
    private Player player;
    private boolean isElite;
    private boolean isTreasure;

    private List<Card> cardChoices;
    private Card selectedCard;
    private Relic relicReward;
    private int goldReward = 0;

    private JButton claimBtn;
    private JButton skipBtn;

    private Image treasureBgImage;

    private static final int CARD_WIDTH = 160;
    private static final int CARD_HEIGHT = 240;

    public RewardPanel(GameWindow gameWindow, Player player, boolean isElite, boolean isTreasure) {
        this.gameWindow = gameWindow;
        this.player = player;
        this.isElite = isElite;
        this.isTreasure = isTreasure;

        this.cardChoices = new ArrayList<>();
        this.selectedCard = null;
        this.relicReward = null;

        try {
            treasureBgImage = new ImageIcon("Res/treasure chest backgroudn.jpg").getImage();
        } catch (Exception e) {
            System.err.println("Could not load treasure background: " + e.getMessage());
        }

        if (!isTreasure) {
            goldReward = isElite ? 30 + new Random().nextInt(20) : 15 + new Random().nextInt(15);
            List<Card> allCards = CardLoader.loadCards("Res/cards.json");
            if (allCards != null && !allCards.isEmpty()) {
                List<Card> validCards = new ArrayList<>();
                String playerClass = player.getClass().getSimpleName();
                for (Card c : allCards) {
                    if (c.getHeroClass().equalsIgnoreCase("Neutral") || c.getHeroClass().equalsIgnoreCase(playerClass)) {
                        validCards.add(c);
                    }
                }

                java.util.Collections.shuffle(validCards);
                for (int i = 0; i < Math.min(3, validCards.size()); i++) {
                    cardChoices.add(new Card(validCards.get(i)));
                }
            }
        }

        if (isElite || isTreasure) {
            relicReward = Relic.getRandomRelic(player.getRelics());
        }

        setLayout(null);
        setBackground(new Color(20, 20, 30));

        createButtons();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateLayout();
            }
        });
    }

    private void createButtons() {
        claimBtn = new JButton(isTreasure ? "CLAIM RELIC & CONTINUE" : "CLAIM REWARDS");
        claimBtn.setFont(new Font("Arial", Font.BOLD, 18));
        claimBtn.setBackground(new Color(218, 165, 32));
        claimBtn.setForeground(Color.WHITE);
        claimBtn.setFocusPainted(false);
        claimBtn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        claimBtn.addActionListener(e -> claimRewards());
        add(claimBtn);

        if (!isTreasure) {
            skipBtn = new JButton("SKIP CARD & CONTINUE");
            skipBtn.setFont(new Font("Arial", Font.BOLD, 16));
            skipBtn.setBackground(new Color(90, 90, 100));
            skipBtn.setForeground(Color.WHITE);
            skipBtn.setFocusPainted(false);
            skipBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            skipBtn.addActionListener(e -> skipRewards());
            add(skipBtn);
        }
    }

    private void updateLayout() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        if (isTreasure) {
            claimBtn.setBounds((w - 300) / 2, h / 2 + 120, 300, 50);
        } else {
            claimBtn.setBounds((w - 500) / 2, h - 100, 240, 50);
            skipBtn.setBounds((w - 500) / 2 + 260, h - 100, 240, 50);
        }
        repaint();
    }

    private void handleMouseClick(int mouseX, int mouseY) {
        if (isTreasure) return;

        int w = getWidth();
        int h = getHeight();
        int totalWidth = cardChoices.size() * (CARD_WIDTH + 30) - 30;
        int startX = (w - totalWidth) / 2;
        int cardY = h / 2 - 80;

        for (int i = 0; i < cardChoices.size(); i++) {
            int cx = startX + i * (CARD_WIDTH + 30);
            if (mouseX >= cx && mouseX <= cx + CARD_WIDTH &&
                    mouseY >= cardY && mouseY <= cardY + CARD_HEIGHT) {
                selectedCard = cardChoices.get(i);
                repaint();
                break;
            }
        }
    }

    private void claimRewards() {
        if (!isTreasure) {
            player.addGold(goldReward);
            System.out.println("Claimed " + goldReward + " gold.");
            if (selectedCard != null) {
                player.getDeck().add(selectedCard);
                System.out.println("Added to deck: " + selectedCard.getName());
            }
        }
        if (relicReward != null) {
            player.addRelic(relicReward);
            System.out.println("Added Relic: " + relicReward.getName());
        }
        gameWindow.showScreen("MAP");
    }

    private void skipRewards() {
        if (!isTreasure) {
            player.addGold(goldReward);
            System.out.println("Claimed " + goldReward + " gold (skipped card).");
        }
        if (relicReward != null) {
            player.addRelic(relicReward);
            System.out.println("Claimed Elite Relic: " + relicReward.getName() + " (skipped card)");
        }
        gameWindow.showScreen("MAP");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (isTreasure && treasureBgImage != null) {
            g2.drawImage(treasureBgImage, 0, 0, w, h, null);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 20, 35), 0, h, new Color(5, 5, 10));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        String titleStr = isTreasure ? "TREASURE CHEST!" : "VICTORY!";
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(titleStr, (w - fmTitle.stringWidth(titleStr)) / 2, 70);

        if (!isTreasure && goldReward > 0) {
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String goldStr = "Reward: " + goldReward + " Gold 💰";
            FontMetrics fmGold = g2.getFontMetrics();
            g2.drawString(goldStr, (w - fmGold.stringWidth(goldStr)) / 2, 110);
        }

        if (relicReward != null) {
            int ry = isTreasure ? h / 2 - 140 : 120;

            g2.setColor(new Color(255, 215, 0, 40));
            g2.fillOval(w / 2 - 45, ry - 5, 90, 90);

            g2.setColor(new Color(60, 45, 20));
            g2.fillOval(w / 2 - 40, ry, 80, 80);
            g2.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(w / 2 - 40, ry, 80, 80);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            String initial = relicReward.getName().substring(0, 1).toUpperCase();
            FontMetrics fmInit = g2.getFontMetrics();
            g2.drawString(initial, w / 2 - fmInit.stringWidth(initial) / 2, ry + 40 + fmInit.getAscent() / 2 - 5);

            g2.setFont(new Font("Arial", Font.BOLD, 22));
            g2.setColor(Color.ORANGE);
            String relicName = "Relic: " + relicReward.getName();
            FontMetrics fmRelicName = g2.getFontMetrics();
            g2.drawString(relicName, (w - fmRelicName.stringWidth(relicName)) / 2, ry + 115);

            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            g2.setColor(Color.LIGHT_GRAY);
            String relicDesc = relicReward.getDescription();
            FontMetrics fmRelicDesc = g2.getFontMetrics();
            g2.drawString(relicDesc, (w - fmRelicDesc.stringWidth(relicDesc)) / 2, ry + 140);
        }

        if (!isTreasure) {
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.setColor(Color.WHITE);
            String subTitle = "Choose a card to add to your deck:";
            FontMetrics fmSub = g2.getFontMetrics();
            int subTitleY = (isElite) ? 310 : 150;
            g2.drawString(subTitle, (w - fmSub.stringWidth(subTitle)) / 2, subTitleY);

            int totalWidth = cardChoices.size() * (CARD_WIDTH + 30) - 30;
            int startX = (w - totalWidth) / 2;
            int cardY = h / 2 - 80;

            for (int i = 0; i < cardChoices.size(); i++) {
                Card card = cardChoices.get(i);
                int cx = startX + i * (CARD_WIDTH + 30);
                drawChoiceCard(g2, card, cx, cardY, card == selectedCard);
            }
        }
    }

    private void drawChoiceCard(Graphics2D g2, Card card, int x, int y, boolean isSelected) {
        g2.setColor(new Color(30, 30, 45));
        g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);

        if (isSelected) {
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(4));
        } else {
            g2.setColor(new Color(255, 170, 0, 200));
            g2.setStroke(new BasicStroke(2));
        }
        g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(card.getName(), x + (CARD_WIDTH - fmTitle.stringWidth(card.getName())) / 2, y + 35);

        g2.setColor(new Color(80, 80, 100));
        g2.drawLine(x + 15, y + 50, x + CARD_WIDTH - 15, y + 50);

        g2.setColor(card.getType() == Card.CardType.ATTACK ? new Color(180, 50, 50) : new Color(50, 120, 180));
        g2.fillRoundRect(x + 20, y + 60, CARD_WIDTH - 40, 20, 4, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        String typeText = card.getType().toString();
        FontMetrics fmType = g2.getFontMetrics();
        g2.drawString(typeText, x + (CARD_WIDTH - fmType.stringWidth(typeText)) / 2, y + 74);

        g2.setColor(new Color(220, 220, 220));
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        String desc = card.getDescription();
        if (desc != null && !desc.isEmpty()) {
            String[] words = desc.split(" ");
            StringBuilder currentLine = new StringBuilder();
            int lineY = y + 115;
            int maxTextWidth = CARD_WIDTH - 24;

            for (String word : words) {
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                FontMetrics fm = g2.getFontMetrics();
                if (fm.stringWidth(testLine) > maxTextWidth) {
                    g2.drawString(currentLine.toString(), x + 12, lineY);
                    lineY += 16;
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine = new StringBuilder(testLine);
                }
            }
            if (currentLine.length() > 0) {
                g2.drawString(currentLine.toString(), x + 12, lineY);
            }
        }

        g2.setColor(new Color(230, 90, 40));
        g2.fillOval(x - 10, y - 10, 28, 28);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x - 10, y - 10, 28, 28);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String costStr = String.valueOf(card.getEnergyCost());
        FontMetrics fmCost = g2.getFontMetrics();
        g2.drawString(costStr, x - 10 + (28 - fmCost.stringWidth(costStr)) / 2, y - 10 + ((28 - fmCost.getHeight()) / 2) + fmCost.getAscent());
    }
}