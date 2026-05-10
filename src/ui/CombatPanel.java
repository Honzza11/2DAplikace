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
    private static final int HAND_Y = 850;

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
        
        addEndTurnButton();
        addViewMapButton();
    }

    private void addViewMapButton() {
        JButton viewMapBtn = new JButton("VIEW MAP");
        viewMapBtn.setBounds(1720, 20, 150, 40);
        viewMapBtn.setFocusPainted(false);
        viewMapBtn.addActionListener(e -> gameWindow.showMapDialog());
        add(viewMapBtn);
    }

    private void addEndTurnButton() {
        JButton endTurnBtn = new JButton("END TURN");
        endTurnBtn.setBounds(1650, 800, 150, 60);
        endTurnBtn.setFont(new Font("Arial", Font.BOLD, 20));
        endTurnBtn.setBackground(new Color(150, 50, 50));
        endTurnBtn.setForeground(Color.WHITE);
        endTurnBtn.setFocusPainted(false);
        endTurnBtn.addActionListener(e -> {
            System.out.println("End Turn Clicked");
        });
        add(endTurnBtn);
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
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, 1920, 1080, null);

            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillRect(0, 0, 1920, 1080);
        } else {

            GradientPaint gp = new GradientPaint(0, 0, new Color(40, 20, 20), 0, 1080, new Color(10, 10, 10));
            g2.setPaint(gp);
            g2.fillRect(0, 0, 1920, 1080);
        }
        

        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRect(0, 750, 1920, 330); 
    }

    private void drawEntities(Graphics2D g2) {

        drawEntity(g2, player.getName(), 300, 450, player.getHealth(), player.getMaxHealth(), Color.CYAN);
        

        if (enemy != null) {
            drawEntity(g2, enemy.getName(), 1400, 450, enemy.getHealth(), enemy.getMaxHealth(), Color.RED);
        }
    }

    private void drawEntity(Graphics2D g2, String name, int x, int y, int hp, int maxHp, int color) {

    }
    
    private void drawEntity(Graphics2D g2, String name, int x, int y, int hp, int maxHp, Color color) {

        g2.setColor(color);
        g2.fillOval(x, y, 150, 250);
        

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString(name, x + 20, y - 60);

        int barWidth = 200;
        int barHeight = 20;
        int healthWidth = (int) ((double) hp / maxHp * barWidth);
        
        g2.setColor(Color.GRAY);
        g2.fillRect(x - 25, y - 40, barWidth, barHeight);
        g2.setColor(Color.GREEN);
        g2.fillRect(x - 25, y - 40, healthWidth, barHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(x - 25, y - 40, barWidth, barHeight);
        
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(hp + " / " + maxHp, x + 40, y - 25);
    }

    private void drawHand(Graphics2D g2) {
        int startX = (1920 - (hand.size() * (CARD_WIDTH + 10))) / 2;
        for (int i = 0; i < hand.size(); i++) {
            drawCard(g2, hand.get(i), startX + i * (CARD_WIDTH + 15), HAND_Y);
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

        g2.setColor(Color.ORANGE);
        g2.fillOval(50, 850, 100, 100);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        g2.drawString(player.getEnergy() + "/" + player.getMaxEnergy(), 65, 915);
        

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Draw: " + player.getDeck().size(), 50, 820);
        g2.drawString("Discard: " + player.getDiscardPile().size(), 50, 980);
    }
}
