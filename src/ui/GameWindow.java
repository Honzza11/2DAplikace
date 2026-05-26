package ui;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GameMap currentMap;

    private JLabel mapLabel;
    private JLabel combatLabel;
    
    private Player currentPlayer;
    private JLabel mapHpLabel;

    public GameWindow() {
        setTitle("Kill the Pyre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized
        setResizable(true);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        BackgroundPanel menuPanel = new BackgroundPanel("Res/catle pozadi1.png");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        
        OutlinedLabel titleLabel = new OutlinedLabel("Kill The Pyre", 72);
        gbc.gridy = 0;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(100, 0, 0, 0);
        menuPanel.add(titleLabel, gbc);

        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 32));
        playButton.setFocusPainted(false);
        playButton.setPreferredSize(new Dimension(250, 70));
        playButton.addActionListener(e -> showScreen("CHAR_SELECT"));
        
        gbc.gridy = 1;
        gbc.weighty = 0.7;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 150, 0);
        menuPanel.add(playButton, gbc);

        JPanel charSelectPanel = createCharacterSelectPanel();

        JPanel mapContainer = new JPanel(new BorderLayout());

        JPanel combatPanel = new JPanel(new GridBagLayout());
        combatLabel = new JLabel("Combat Screen");
        combatLabel.setFont(new Font("Arial", Font.BOLD, 24));
        combatPanel.add(combatLabel);

        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(charSelectPanel, "CHAR_SELECT");
        mainContainer.add(mapContainer, "MAP");
        mainContainer.add(combatPanel, "COMBAT");

        add(mainContainer);
        showScreen("MENU");
    }

    private JPanel createCharacterSelectPanel() {
        BackgroundPanel panel = new BackgroundPanel("Res/catle pozadi1.png");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 0, 20, 0);
        
        OutlinedLabel title = new OutlinedLabel("Choose Your Hero", 48);
        panel.add(title, gbc);

        JPanel heroesBox = new JPanel(new GridLayout(1, 2, 40, 0));
        heroesBox.setOpaque(false);
        
        heroesBox.add(createHeroOption("Ash Walker", "Aggressive & Risky. Uses Heat to deal massive damage.", "ASH_WALKER"));
        heroesBox.add(createHeroOption("Bard", "Tactical & Defensive. Uses Chords to trigger powerful songs.", "BARD"));
        
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 50, 0);
        panel.add(heroesBox, gbc);
        
        return panel;
    }

    private JPanel createHeroOption(String name, String desc, String type) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(320, 480));
        panel.setBackground(new Color(20, 20, 20, 220));
        panel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        

        String imagePath = type.equals("ASH_WALKER") ? "Res/assasin.png" : "Res/bard111.png";
        ImageIcon heroIcon = null;
        try {
            ImageIcon origIcon = new ImageIcon(imagePath);
            Image img = origIcon.getImage();
            
            int maxWidth = 120;
            int maxHeight = 200;
            int imgW = img.getWidth(null);
            int imgH = img.getHeight(null);
            int newWidth = maxWidth;
            int newHeight = maxHeight;
            
            if (imgW > 0 && imgH > 0) {
                double imgAspect = (double) imgW / imgH;
                double boxAspect = (double) maxWidth / maxHeight;
                if (imgAspect > boxAspect) {
                    newWidth = maxWidth;
                    newHeight = (int) (maxWidth / imgAspect);
                } else {
                    newHeight = maxHeight;
                    newWidth = (int) (maxHeight * imgAspect);
                }
            }
            
            Image scaledImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            heroIcon = new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Could not load hero preview image: " + e.getMessage());
        }

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setOpaque(false);

        if (heroIcon != null) {
            JLabel imgLabel = new JLabel(heroIcon);
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            centerPanel.add(imgLabel, BorderLayout.NORTH);
        }

        JTextArea descArea = new JTextArea(desc);
        descArea.setFont(new Font("Arial", Font.PLAIN, 16));
        descArea.setForeground(Color.LIGHT_GRAY);
        descArea.setBackground(new Color(0,0,0,0));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setMargin(new Insets(10, 20, 10, 20));
        
        centerPanel.add(descArea, BorderLayout.CENTER);

        JButton selectBtn = new JButton("Select");
        selectBtn.setFont(new Font("Arial", Font.BOLD, 20));
        selectBtn.addActionListener(e -> selectCharacter(type));
        
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(selectBtn, BorderLayout.SOUTH);
        
        return panel;
    }

    private void selectCharacter(String type) {
        List<Card> allCards = CardLoader.loadCards("Res/cards.json");
        List<Card> deck = new ArrayList<>();
        
        if (type.equals("ASH_WALKER")) {
            currentPlayer = new AshWalker("Ash Walker", 80, 3);
            addCardsToDeck(deck, allCards, "Strike", 5);
            addCardsToDeck(deck, allCards, "Defend", 4);
            addCardsToDeck(deck, allCards, "Cinder Strike", 1);
        } else {
            currentPlayer = new Bard("Bard", 70, 3);
            addCardsToDeck(deck, allCards, "Strike", 5);
            addCardsToDeck(deck, allCards, "Defend", 4);
            addCardsToDeck(deck, allCards, "Soul Melody", 1);
        }
        
        currentPlayer.setDeck(deck);
        System.out.println("Selected: " + type + ". Deck size: " + deck.size());
        
        generateAndShowMap();
    }

    private void generateAndShowMap() {
        this.currentMap = new GameMap(15, 7);
        MapPanel mapPanel = new MapPanel(this, currentMap);
        
        JScrollPane scrollPane = new JScrollPane(mapPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        JPanel mapControls = new JPanel(new BorderLayout());
        mapControls.setBackground(new Color(240, 218, 181)); // Match parchment
        mapControls.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        mapHpLabel = new JLabel("❤️ HP: " + currentPlayer.getHealth() + " / " + currentPlayer.getMaxHealth() + "   💰 " + currentPlayer.getGold());
        mapHpLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mapHpLabel.setForeground(new Color(180, 40, 40));
        mapControls.add(mapHpLabel, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        JButton deckBtn = new JButton("View Deck");
        deckBtn.setFocusPainted(false);
        deckBtn.addActionListener(e -> showDeckDialog());
        buttonsPanel.add(deckBtn);

        JButton legendBtn = new JButton("Show Legend");
        legendBtn.setFocusPainted(false);
        legendBtn.addActionListener(e -> showLegendDialog());
        buttonsPanel.add(legendBtn);

        mapControls.add(buttonsPanel, BorderLayout.EAST);

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

        for (Component c : mainContainer.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.getLayout() instanceof BorderLayout && p.getComponentCount() == 0) {
                    p.add(mapControls, BorderLayout.NORTH);
                    p.add(scrollPane, BorderLayout.CENTER);
                    p.revalidate();
                    p.repaint();
                    break;
                }
            }
        }
        
        showScreen("MAP");
    }

    public void startCombat(Enemy enemy) {

        currentPlayer.getHand().clear();
        currentPlayer.getDiscardPile().clear();
        currentPlayer.shuffleDeck();
        currentPlayer.drawCards(5);
        currentPlayer.setEnergy(currentPlayer.getMaxEnergy());

        CombatPanel combatPanel = new CombatPanel(this, currentPlayer, enemy);
        mainContainer.add(combatPanel, "COMBAT");
        
        showScreen("COMBAT");
    }

    public void showMapDialog() {
        if (currentMap == null) return;
        
        JDialog dialog = new JDialog(this, "World Map", false);
        MapPanel mapPanel = new MapPanel(this, currentMap, false);
        JScrollPane scrollPane = new JScrollPane(mapPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        dialog.add(scrollPane);
        dialog.setSize(1000, 800);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);


        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
            
            JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
            int max = horizontal.getMaximum();
            int extent = horizontal.getModel().getExtent();
            horizontal.setValue((max - extent) / 2); // Center it
        });
    }

    public void showDeckDialog() {
        if (currentPlayer == null) return;
        
        List<Card> fullDeck = new ArrayList<>();
        fullDeck.addAll(currentPlayer.getDeck());
        fullDeck.addAll(currentPlayer.getHand());
        fullDeck.addAll(currentPlayer.getDiscardPile());
        
        JDialog dialog = new JDialog(this, "Current Deck (" + fullDeck.size() + " cards)", false);
        DeckPanel deckPanel = new DeckPanel(fullDeck);
        JScrollPane scrollPane = new JScrollPane(deckPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        dialog.add(scrollPane);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void showSmithDialog(java.util.function.Consumer<Card> onUpgrade) {
        List<Card> upgradable = new ArrayList<>();
        for (Card c : currentPlayer.getDeck()) {
            if (!c.isUpgraded()) {
                upgradable.add(c);
            }
        }
        
        JDialog dialog = new JDialog(this, "Select a card to Upgrade", true);
        DeckPanel deckPanel = new DeckPanel(upgradable, card -> {
            onUpgrade.accept(card);
            dialog.dispose();
        });
        JScrollPane scrollPane = new JScrollPane(deckPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        dialog.add(scrollPane);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showLegendDialog() {
        JDialog dialog = new JDialog(this, "Map Legend", false); // Non-modal
        dialog.add(new MapLegendPanel());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void addCardsToDeck(List<Card> deck, List<Card> allCards, String name, int count) {
        for (Card c : allCards) {
            if (c.getName().equals(name)) {
                for (int i = 0; i < count; i++) deck.add(new Card(c));
                return;
            }
        }
    }

    public void showScreen(String screenName) {
        if (screenName.equals("MAP") && mapHpLabel != null && currentPlayer != null) {
            mapHpLabel.setText("❤️ HP: " + currentPlayer.getHealth() + " / " + currentPlayer.getMaxHealth() + "   💰 " + currentPlayer.getGold());
        }
        cardLayout.show(mainContainer, screenName);
    }

    public void showRestSite() {
        RestPanel restPanel = new RestPanel(this, currentPlayer);
        mainContainer.add(restPanel, "REST");
        showScreen("REST");
    }

    public void showShop() {
        ShopPanel shopPanel = new ShopPanel(this, currentPlayer);
        mainContainer.add(shopPanel, "SHOP");
        showScreen("SHOP");
    }

    public void showRemoveDialog(java.util.function.Consumer<Card> onRemove) {
        JDialog dialog = new JDialog(this, "Select a card to Remove", true);
        DeckPanel deckPanel = new DeckPanel(currentPlayer.getDeck(), card -> {
            onRemove.accept(card);
            dialog.dispose();
        });
        JScrollPane scrollPane = new JScrollPane(deckPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        dialog.add(scrollPane);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void showCombatReward(boolean isElite) {
        RewardPanel rewardPanel = new RewardPanel(this, currentPlayer, isElite, false);
        mainContainer.add(rewardPanel, "REWARD");
        showScreen("REWARD");
    }

    public void showTreasureReward() {
        RewardPanel rewardPanel = new RewardPanel(this, currentPlayer, false, true);
        mainContainer.add(rewardPanel, "REWARD");
        showScreen("REWARD");
    }
}

