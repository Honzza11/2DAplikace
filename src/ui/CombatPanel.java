package ui;

import model.Card;
import model.Enemy;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CombatPanel extends JPanel {
    private Player player;
    private Enemy enemy;
    private List<Card> hand;
    private Image backgroundImage;
    private GameWindow gameWindow;
    

    private static final int CARD_WIDTH = 120;
    private static final int CARD_HEIGHT = 170;
    private JButton endTurnBtn;
    private JButton viewMapBtn;

    public CombatPanel(GameWindow gameWindow, Player player, Enemy enemy) {
        this.gameWindow = gameWindow;
        this.player = player;
        this.enemy = enemy;
        this.hand = player.getHand();
        

        try {
            backgroundImage = new ImageIcon("c:\\Users\\jenik\\IdeaProjects\\2D Aplikace-Killthepyre\\Res\\dungeon (1).jpg").getImage();
        } catch (Exception e) {
            System.err.println("Could not load background image: " + e.getMessage());
        }

        setLayout(null);
        setBackground(new Color(30, 30, 30)); 
        
        createButtons();
        

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleCardClick(e.getX(), e.getY());
            }
        });


        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateLayout();
            }
        });
    }

    private void handleCardClick(int mouseX, int mouseY) {
        int w = getWidth();
        int h = getHeight();
        int uiStartY = (int)(h * 0.7);
        int uiHeight = (int)(h * 0.3);
        int handSize = hand.size();
        if (handSize == 0) return;

        int totalHandWidth = handSize * (CARD_WIDTH + 15);
        int startX = (w - totalHandWidth) / 2;
        int handY = uiStartY + (uiHeight - CARD_HEIGHT) / 2;


        for (int i = 0; i < handSize; i++) {
            int cardX = startX + i * (CARD_WIDTH + 15);
            if (mouseX >= cardX && mouseX <= cardX + CARD_WIDTH &&
                mouseY >= handY && mouseY <= handY + CARD_HEIGHT) {
                
                Card card = hand.get(i);

                if (player.playCard(card, enemy)) {
                    System.out.println("Played: " + card.getName());
                    repaint();
                    

                    if (enemy.isDead()) {
                        System.out.println("Enemy defeated!");

                    }
                }
                break;
            }
        }
    }

    private void createButtons() {
        viewMapBtn = new JButton("VIEW MAP");
        viewMapBtn.setFocusPainted(false);
        viewMapBtn.addActionListener(e -> gameWindow.showMapDialog());
        add(viewMapBtn);

        endTurnBtn = new JButton("END TURN");
        endTurnBtn.setFont(new Font("Arial", Font.BOLD, 20));
        endTurnBtn.setBackground(new Color(150, 50, 50));
        endTurnBtn.setForeground(Color.WHITE);
        endTurnBtn.setFocusPainted(false);
        endTurnBtn.addActionListener(e -> {

            player.discardHand();
            

            if (enemy != null && !enemy.isDead()) {
                enemy.takeTurn(player);
            }

            player.startTurn();
            
            repaint();
        });
        add(endTurnBtn);
    }

    private void updateLayout() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;
        int battleHeight = (int)(h * 0.7);

        viewMapBtn.setBounds(w - 180, 20, 150, 40);
        endTurnBtn.setBounds(w - 200, h - 100, 150, 60);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawBackground(g2);
        drawEntities(g2);
        drawHand(g2);
        drawUIOverlay(g2);
    }

    private void drawBackground(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int uiHeight = (int)(h * 0.3); // Bottom 30% for UI
        int battleHeight = h - uiHeight;
        

        if (backgroundImage != null) {

            g2.drawImage(backgroundImage, 0, 0, w, battleHeight, null);

            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRect(0, 0, w, battleHeight);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(40, 20, 20), 0, battleHeight, new Color(10, 10, 10));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, battleHeight);
        }
        

        g2.setColor(Color.BLACK);
        g2.fillRect(0, battleHeight, w, uiHeight);
        

        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, battleHeight, w, battleHeight);
    }

    private void drawEntities(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int battleHeight = (int)(h * 0.7);
        

        drawEntity(g2, player.getName(), (int)(w * 0.2) - 75, battleHeight - 320, player.getHealth(), player.getMaxHealth(), player.getBlock(), Color.CYAN);
        

        if (enemy != null) {
            drawEntity(g2, enemy.getName(), (int)(w * 0.8) - 75, battleHeight - 320, enemy.getHealth(), enemy.getMaxHealth(), enemy.getBlock(), Color.RED);
        }
    }

    private void drawEntity(Graphics2D g2, String name, int x, int y, int hp, int maxHp, int block, Color color) {
        g2.setColor(color);
        g2.fillOval(x, y, 150, 250);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString(name, x + 20, y - 60);


        if (enemy != null && name.equals(enemy.getName())) {
            g2.setColor(new Color(255, 200, 0));
            g2.setFont(new Font("Arial", Font.ITALIC, 18));
            g2.drawString("Intent: " + enemy.getIntentDescription(), x + 20, y - 90);
        }
        
        int barWidth = 200;
        int barHeight = 20;
        int healthWidth = (int) ((double) hp / Math.max(1, maxHp) * barWidth);
        
        g2.setColor(Color.GRAY);
        g2.fillRect(x - 25, y - 40, barWidth, barHeight);
        g2.setColor(Color.GREEN);
        g2.fillRect(x - 25, y - 40, healthWidth, barHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(x - 25, y - 40, barWidth, barHeight);
        
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(hp + " / " + maxHp, x + 40, y - 25);


        if (block > 0) {
            g2.setColor(new Color(50, 150, 250));
            g2.fillRoundRect(x - 58, y - 44, 28, 28, 6, 6);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(x - 58, y - 44, 28, 28, 6, 6);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            
            String blockStr = String.valueOf(block);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (x - 58) + (28 - fm.stringWidth(blockStr)) / 2;
            int textY = (y - 44) + ((28 - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(blockStr, textX, textY);
        }
    }

    private void drawHand(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int uiStartY = (int)(h * 0.7);
        int uiHeight = (int)(h * 0.3);
        
        int totalHandWidth = hand.size() * (CARD_WIDTH + 15);
        int startX = (w - totalHandWidth) / 2;
        int handY = uiStartY + (uiHeight - CARD_HEIGHT) / 2;
        
        for (int i = 0; i < hand.size(); i++) {
            drawCard(g2, hand.get(i), startX + i * (CARD_WIDTH + 15), handY);
        }
    }

    private void drawCard(Graphics2D g2, Card card, int x, int y) {
        g2.setColor(new Color(50, 50, 70));
        g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);
        g2.setColor(Color.ORANGE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(card.getName(), x + 10, y + 25);
        
        g2.setFont(new Font("Arial", Font.PLAIN, 12));

        g2.drawString("Cost: " + card.getEnergyCost(), x + 10, y + 50);
    }

    private void drawUIOverlay(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int uiStartY = (int)(h * 0.7);
        int uiHeight = (int)(h * 0.3);
        

        g2.setColor(Color.ORANGE);
        g2.fillOval(50, uiStartY + (uiHeight - 100) / 2, 100, 100);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        g2.drawString(player.getEnergy() + "/" + player.getMaxEnergy(), 65, uiStartY + (uiHeight + 25) / 2);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Draw: " + player.getDeck().size(), 50, uiStartY + 30);
        g2.drawString("Discard: " + player.getDiscardPile().size(), 50, h - 30);
    }
}
