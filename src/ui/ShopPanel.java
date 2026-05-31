package ui;

import model.Card;
import model.CardLoader;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Panel představující obchod u kupce (Merchant).
 * Hráč zde může nakupovat náhodně vygenerované karty vhodné pro jeho třídu
 * nebo zaplatit za permanentní odstranění základní/slabé karty ze svého balíčku.
 */
public class ShopPanel extends JPanel {
    private GameWindow gameWindow;
    private Player player;
    private List<CardSale> cardsForSale; // Seznam karet vystavených na prodej
    private boolean cardRemovalAvailable = true; // Služba odstranění karty je dostupná pouze jednou
    private int removalCost = 75; // Cena za odstranění jedné karty

    private JLabel goldLabel;
    private Image backgroundImage; // Obrázek pozadí obchodu

    /**
     * Pomocná vnitřní struktura pro sledování stavu konkrétního zboží.
     */
    private static class CardSale {
        Card card;
        int price;
        boolean sold = false; // Příznak, zda již byla karta zakoupena

        CardSale(Card card, int price) {
            this.card = card;
            this.price = price;
        }
    }

    public ShopPanel(GameWindow gameWindow, Player player) {
        this.gameWindow = gameWindow;
        this.player = player;
        this.cardsForSale = new ArrayList<>();

        // Načtení atmosférického pozadí středověkého obchodu
        try {
            backgroundImage = new ImageIcon("Res/medieval_shop.jpg").getImage();
        } catch (Exception e) {
            System.err.println("Could not load shop background: " + e.getMessage());
        }

        // Vygenerování aktuálního zboží na pultech
        generateShopInventory();

        setLayout(new BorderLayout());
        setBackground(new Color(25, 20, 15));

        // --- HORNÍ PANEL (Jméno obchodníka a stav konta hráče) ---
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

        // --- STŘEDOVÝ PANEL (Zobrazení karet a mechanika odstranění) ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Sub-panel pro horizontální seřazení karet vedle sebe
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        cardsPanel.setOpaque(false);

        // Vygenerování vizuální komponenty pro každou kartu v prodeji
        for (CardSale sale : cardsForSale) {
            cardsPanel.add(createCardSalePanel(sale));
        }

        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(cardsPanel);

        // Sekce pro placené odstranění karty z balíčku
        JPanel removalPanel = new JPanel();
        removalPanel.setOpaque(false);
        JButton removeBtn = new JButton("Remove a Card (Costs " + removalCost + " 💰)");
        removeBtn.setFont(new Font("Arial", Font.BOLD, 24));
        removeBtn.setBackground(new Color(150, 50, 50));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);

        // Logika tlačítka pro odstranění karty
        removeBtn.addActionListener(e -> {
            if (cardRemovalAvailable && player.getGold() >= removalCost) {
                // Otevření dialogu s výběrem karty k odstranění
                gameWindow.showRemoveDialog(card -> {
                    player.getDeck().remove(card); // Odstranění z balíčku hráče
                    player.removeGold(removalCost); // Odečtení peněz
                    cardRemovalAvailable = false; // Deaktivace služby pro tento nákup
                    removeBtn.setEnabled(false);
                    removeBtn.setText("Card Removed");
                    updateGoldLabel(); // Obnovení zobrazení zlata
                });
            } else if (player.getGold() < removalCost) {
                JOptionPane.showMessageDialog(this, "Not enough gold!");
            }
        });

        removalPanel.add(removeBtn);
        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(removalPanel);

        add(centerPanel, BorderLayout.CENTER);

        // --- SPODNÍ PANEL (Odchod z obchodu zpět na mapu) ---
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

    /**
     * Načte kompletní databázi karet, profiltruje je podle hrdiny a náhodně vybere 3 položky k prodeji.
     */
    private void generateShopInventory() {
        List<Card> allCards = CardLoader.loadCards("Res/cards.json");
        if (allCards == null || allCards.isEmpty()) return;

        List<Card> validCards = new ArrayList<>();
        String playerClass = player.getClass().getSimpleName();

        // Obchodník nabízí pouze neutrální karty nebo karty určené pro aktuální třídu hrdiny
        for (Card c : allCards) {
            if (c.getHeroClass().equalsIgnoreCase("Neutral") || c.getHeroClass().equalsIgnoreCase(playerClass)) {
                validCards.add(c);
            }
        }

        java.util.Collections.shuffle(validCards);
        Random rand = new Random();

        // Výběr tří náhodných karet a nastavení jejich ceny (45 až 74 zlťáků)
        for (int i = 0; i < Math.min(3, validCards.size()); i++) {
            int price = 45 + rand.nextInt(30);
            cardsForSale.add(new CardSale(new Card(validCards.get(i)), price));
        }
    }

    /**
     * Vytvoří malý samostatný panel pro jednu kartu a její nákupní tlačítko pod ní.
     */
    private JPanel createCardSalePanel(CardSale sale) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Pro zobrazení jedné karty zneužijeme DeckPanel zabalením karty do listu
        List<Card> singleCardList = new ArrayList<>();
        singleCardList.add(sale.card);
        DeckPanel cardRenderer = new DeckPanel(singleCardList);
        cardRenderer.setPreferredSize(new Dimension(170, 225));
        panel.add(cardRenderer, BorderLayout.CENTER);

        // Nákupní tlačítko s cenovkou
        JButton buyBtn = new JButton(sale.price + " 💰");
        buyBtn.setFont(new Font("Arial", Font.BOLD, 20));
        buyBtn.setBackground(new Color(218, 165, 32));
        buyBtn.setFocusPainted(false);

        // Logika nákupu karty
        buyBtn.addActionListener(e -> {
            if (!sale.sold) {
                if (player.getGold() >= sale.price) {
                    player.removeGold(sale.price); // Transakce
                    player.getDeck().add(sale.card); // Přidání karty do balíčku
                    sale.sold = true;

                    // Vizuální uzamčení zakoupeného zboží
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

    /**
     * Pomocná metoda pro rychlé překreslení aktuálního množství zlata.
     */
    private void updateGoldLabel() {
        goldLabel.setText("💰 " + player.getGold() + " Gold");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vykreslení obrázku pozadí a nanesení poloprůhledné tmavé vrstvy pro kontrast prvků UI
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(new Color(0, 0, 0, 140)); // Ztmavení
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}