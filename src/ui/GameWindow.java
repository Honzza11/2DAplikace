package ui;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Hlavní okno hry typu JFrame, které slouží jako hlavní řadič (Controller) celé aplikace.
 * Spravuje životní cyklus hry, drží instanci aktuálního hráče a mapy světa.
 * Využívá CardLayout pro plynulé přepínání mezi hlavními herními obrazovkami
 * (Menu, Výběr postavy, Mapa, Souboj, Obchod, Odpočinek atd.).
 */
public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GameMap currentMap;

    private JLabel mapLabel;
    private JLabel combatLabel;

    private Player currentPlayer;
    private JLabel mapHpLabel;

    public GameWindow() {
        // --- INICIALIZACE OKNA A DATABÁZE ---
        EnemyLoader.loadEnemies("Res/enemies.json"); // Načtení databáze nepřátel
        setTitle("Kill the Pyre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Spuštění okna maximalizovaně přes celou obrazovku
        setResizable(true);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // --- HLAVNÍ MENU (MENU SCREEN) ---
        BackgroundPanel menuPanel = new BackgroundPanel("Res/title pozadi.jpg");
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

        // --- PŘÍPRAVA ZÁKLADNÍCH PANELŮ ---
        JPanel charSelectPanel = createCharacterSelectPanel();
        JPanel mapContainer = new JPanel(new BorderLayout());

        JPanel combatPanel = new JPanel(new GridBagLayout());
        combatLabel = new JLabel("Combat Screen");
        combatLabel.setFont(new Font("Arial", Font.BOLD, 24));
        combatPanel.add(combatLabel);

        // --- REGISTRACE OBRAZOVEK DO CARDLAYOUTU ---
        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(charSelectPanel, "CHAR_SELECT");
        mainContainer.add(mapContainer, "MAP");
        mainContainer.add(combatPanel, "COMBAT");
        mainContainer.add(new GameOverScreen(this), "GAME_OVER");

        add(mainContainer);
        showScreen("MENU"); // Start hry v hlavním menu
    }

    /**
     * Vytvoří obrazovku pro výběr hrdiny se zobrazením herních mechanik a statistik tříd.
     */
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

        // Popisy herních mechanik jednotlivých postav
        String ashWalkerDesc = "Aggressive & Risky.\n\n"
                + "HEAT MECHANIC:\n"
                + "Attack cards generate Heat points, which drastically increase the damage of your subsequent attacks.\n\n"
                + "WARNING: If your Heat exceeds the limit at the end of your turn, you will take overheat damage! "
                + "You must strategically alternate attacks with cooling skills (like Quench & Coils) that safely reduce Heat and generate Block.";

        String bardDesc = "Tactical & Defensive.\n\n"
                + "NOTES MECHANIC:\n"
                + "Playing cards composes songs. Each card adds an Attack or Skill note to your active bar.\n\n";

        heroesBox.add(createHeroOption("Ash Walker", ashWalkerDesc, "ASH_WALKER"));
        heroesBox.add(createHeroOption("Bard", bardDesc, "BARD"));

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 50, 0);
        panel.add(heroesBox, gbc);

        return panel;
    }

    /**
     * Pomocná metoda, která sestaví grafickou kartu (box) jednoho hrdiny pro výběrovou obrazovku.
     */
    private JPanel createHeroOption(String name, String desc, String type) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(480, 640));
        panel.setBackground(new Color(20, 20, 20, 220));
        panel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // Škálování náhledového obrázku hrdiny se zachováním poměru stran
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

    /**
     * Vytvoří instanci vybraného hrdiny a naplní jeho startovní balíček (deck) příslušnými kartami z JSONu.
     */
    private void selectCharacter(String type) {
        List<Card> allCards = CardLoader.loadCards("Res/cards.json"); // Načtení kompletního fondu karet
        List<Card> deck = new ArrayList<>();

        if (type.equals("ASH_WALKER")) {
            currentPlayer = new AshWalker("Ash Walker", 80, 3);
            addCardsToDeck(deck, allCards, "Strike", 3);
            addCardsToDeck(deck, allCards, "Defend", 3);
            addCardsToDeck(deck, allCards, "Cinder Strike", 2);
            addCardsToDeck(deck, allCards, "Obsidian Dagger", 1);
            addCardsToDeck(deck, allCards, "Quench & Coils", 1);
            addCardsToDeck(deck, allCards, "Eruption", 1);
        } else {
            currentPlayer = new Bard("Bard", 80, 3);
            addCardsToDeck(deck, allCards, "Strike", 3);
            addCardsToDeck(deck, allCards, "Defend", 3);
            addCardsToDeck(deck, allCards, "Soul Melody", 2);
            addCardsToDeck(deck, allCards, "Dangerous Note", 2);
            addCardsToDeck(deck, allCards, "Quick Prelude", 1);
            addCardsToDeck(deck, allCards, "Double Staccato", 1);
        }

        currentPlayer.setDeck(deck);
        System.out.println("Selected: " + type + ". Deck size: " + deck.size());

        generateAndShowMap();
    }

    /**
     * Vygeneruje novou procedurální mapu světa a nastaví scrollovací zónu,
     * přičemž automaticky odroluje na spodní okraj, kde cesta začíná.
     */
    private void generateAndShowMap() {
        this.currentMap = new GameMap(15, 7);
        MapPanel mapPanel = new MapPanel(this, currentMap);

        JScrollPane scrollPane = new JScrollPane(mapPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        // Horní lišta mapy s HP, zlatem a funkčními tlačítky
        JPanel mapControls = new JPanel(new BorderLayout());
        mapControls.setBackground(new Color(240, 218, 181)); // Barva pergamenu
        mapControls.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        mapHpLabel = new JLabel("HP: " + currentPlayer.getHealth() + " / " + currentPlayer.getMaxHealth() + "💰 " + currentPlayer.getGold() + " Gold");
        mapHpLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mapHpLabel.setForeground(new Color(0, 0, 0));
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

        // Asynchronní odrolování dolů po inicializaci layoutu
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

        // Vyhledání mapContaineru a vložení lišty s mapou
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

    /**
     * Inicializuje bojovou scénu. Resetuje dočasné bojové stavy hrdiny, zamíchá balíček,
     * aplikuje pasivní efekty vlastněných relikvií a lízne úvodní ruku karet.
     */
    public void startCombat(Enemy enemy) {
        currentPlayer.setAttackedThisCombat(false);
        currentPlayer.resetBlock();
        if (currentPlayer instanceof AshWalker) {
            ((AshWalker) currentPlayer).resetHeat();
        }
        if (currentPlayer instanceof Bard) {
            ((Bard) currentPlayer).resetForCombat();
        }

        currentPlayer.getHand().clear();
        currentPlayer.getDiscardPile().clear();
        currentPlayer.shuffleDeck();

        // Výpočet počtu karet k líznutí (zohlednění Bag of Preparation relikvie)
        int cardsToDraw = 5;
        if (currentPlayer.hasRelic("Bag of Preparation")) {
            cardsToDraw += 2;
        }
        currentPlayer.drawCards(cardsToDraw);

        // Výpočet počáteční energie (zohlednění Lantern relikvie)
        int startEnergy = currentPlayer.getMaxEnergy();
        if (currentPlayer.hasRelic("Lantern")) {
            startEnergy += 1;
        }
        currentPlayer.setEnergy(startEnergy);

        // Aplikace relikvie Anchor (Kotva)
        if (currentPlayer.hasRelic("Anchor")) {
            currentPlayer.addBlock(10);
        }

        CombatPanel combatPanel = new CombatPanel(this, currentPlayer, enemy);
        mainContainer.add(combatPanel, "COMBAT");

        showScreen("COMBAT");
    }

    public void showWinScreen() {
        WinScreen winScreen = new WinScreen(this, currentPlayer);
        mainContainer.add(winScreen, "WIN_SCREEN");
        showScreen("WIN_SCREEN");
    }

    public void showGameOverScreen() {
        showScreen("GAME_OVER");
    }

    /**
     * Otevře bezrežimové (non-modal) dialogové okno s přehledem mapy světa.
     */
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
            horizontal.setValue((max - extent) / 2);
        });
    }

    /**
     * Otevře dialogové okno s mřížkou všech karet, které hráč aktuálně vlastní v celém decku.
     */
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

    /**
     * Otevře modální dialog kováře u ohniště a umožní vylepšit (upgrade) vybranou kartu.
     */
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
        JDialog dialog = new JDialog(this, "Map Legend", false);
        dialog.add(new MapLegendPanel());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /**
     * Pomocná metoda pro vyhledání šablony karty podle jména a její naklonování do balíčku.
     */
    private void addCardsToDeck(List<Card> deck, List<Card> allCards, String name, int count) {
        for (Card c : allCards) {
            if (c.getName().equals(name)) {
                for (int i = 0; i < count; i++) deck.add(new Card(c));
                return;
            }
        }
    }

    /**
     * Aktualizuje textové informace o HP a zlaťácích na horní liště mapy.
     */
    public void updateMapHpLabel() {
        if (mapHpLabel != null) {
            mapHpLabel.setText("HP:"+currentPlayer.getHealth()+"/"+currentPlayer.getMaxHealth()+" | "+"GOLD: "+currentPlayer.getGold());
        }
    }

    /**
     * Přepne aktivní obrazovku CardLayoutu na základě zadaného klíče (ID screeny).
     */
    public void showScreen(String screenName) {
        if (screenName.equals("MAP")) {
            updateMapHpLabel();
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

    /**
     * Otevře modální dialog v obchodě nebo při události pro trvalé odstranění (remove) vybrané karty z balíčku.
     */
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