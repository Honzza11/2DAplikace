package ui;

import model.Card;
import model.CardLoader;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopPanel extends JPanel {
    private GameWindow gameWindow;
    private Player player;
    private List<CardSale> cardsForSale;
    private boolean cardRemovalAvailable = true;
    private int removalCost = 75;
    
    private JLabel goldLabel;
    
    private static class CardSale {
        Card card;
        int price;
        boolean sold = false;
        
        CardSale(Card card, int price) {
            this.card = card;
            this.price = price;
        }
    }

    public ShopPanel(GameWindow gameWindow, Player player) {
        this.gameWindow = gameWindow;
        this.player = player;
        this.cardsForSale = new ArrayList<>();
        
        generateShopInventory();
        
        setLayout(new BorderLayout());
        setBackground(new Color(25, 20, 15)); // Dark shop background
        
        // Top Panel: Title and Gold
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JLabel titleLabel = new JLabel("Merchant");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.ORANGE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        goldLabel = new JLabel("💰 " + player.getGold() + " Gold");
        goldLabel.setFont(new Font("Arial", Font.BOLD, 36));
        goldLabel.setForeground(Color.YELLOW);
        topPanel.add(goldLabel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center: Items for sale
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        cardsPanel.setOpaque(false);
        
        for (CardSale sale : cardsForSale) {
            cardsPanel.add(createCardSalePanel(sale));
        }
        
        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(cardsPanel);
        
        // Removal Option
        JPanel removalPanel = new JPanel();
        removalPanel.setOpaque(false);
        JButton removeBtn = new JButton("Remove a Card (Costs " + removalCost + " 💰)");
        removeBtn.setFont(new Font("Arial", Font.BOLD, 24));
        removeBtn.setBackground(new Color(150, 50, 50));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        
        removeBtn.addActionListener(e -> {
            if (cardRemovalAvailable && player.getGold() >= removalCost) {
                gameWindow.showRemoveDialog(card -> {
                    player.getDeck().remove(card);
                    player.removeGold(removalCost);
                    cardRemovalAvailable = false;
                    removeBtn.setEnabled(false);
                    removeBtn.setText("Card Removed");
                    updateGoldLabel();
                });
            } else if (player.getGold() < removalCost) {
                JOptionPane.showMessageDialog(this, "Not enough gold!");
            }
        });
        
        removalPanel.add(removeBtn);
        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(removalPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom: Leave Button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
        JButton leaveBtn = new JButton("LEAVE SHOP");
        leaveBtn.setFont(new Font("Arial", Font.BOLD, 28));
        leaveBtn.setBackground(new Color(80, 80, 80));
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.setFocusPainted(false);
        leaveBtn.addActionListener(e -> gameWindow.showScreen("MAP"));
        bottomPanel.add(leaveBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void generateShopInventory() {
        List<Card> allCards = CardLoader.loadCards("Res/cards.json");
        if (allCards == null || allCards.isEmpty()) return;
        
        List<Card> validCards = new ArrayList<>();
        String playerClass = player.getClass().getSimpleName();
        for (Card c : allCards) {
            if (c.getHeroClass().equalsIgnoreCase("Neutral") || c.getHeroClass().equalsIgnoreCase(playerClass)) {
                validCards.add(c);
            }
        }
        
        java.util.Collections.shuffle(validCards);
        Random rand = new Random();
        for (int i = 0; i < Math.min(3, validCards.size()); i++) {
            int price = 45 + rand.nextInt(30);
            cardsForSale.add(new CardSale(new Card(validCards.get(i)), price));
        }
    }
    
    private JPanel createCardSalePanel(CardSale sale) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Wrap the single card in a DeckPanel so it renders nicely!
        List<Card> singleCardList = new ArrayList<>();
        singleCardList.add(sale.card);
        DeckPanel cardRenderer = new DeckPanel(singleCardList);
        cardRenderer.setPreferredSize(new Dimension(170, 225));
        panel.add(cardRenderer, BorderLayout.CENTER);
        
        JButton buyBtn = new JButton(sale.price + " 💰");
        buyBtn.setFont(new Font("Arial", Font.BOLD, 20));
        buyBtn.setBackground(new Color(218, 165, 32));
        buyBtn.setFocusPainted(false);
        
        buyBtn.addActionListener(e -> {
            if (!sale.sold) {
                if (player.getGold() >= sale.price) {
                    player.removeGold(sale.price);
                    player.getDeck().add(sale.card);
                    sale.sold = true;
                    buyBtn.setEnabled(false);
                    buyBtn.setText("SOLD");
                    buyBtn.setBackground(Color.GRAY);
                    updateGoldLabel();
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough gold!");
                }
            }
        });
        
        panel.add(buyBtn, BorderLayout.SOUTH);
        return panel;
    }
    
    private void updateGoldLabel() {
        goldLabel.setText("💰 " + player.getGold() + " Gold");
    }
}
