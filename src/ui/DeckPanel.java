package ui;

import model.Card;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel určený pro přehledné zobrazení seznamu karet v dynamické mřížce (Grid).
 * Využívá se pro zobrazení hráčova balíčku, odměn za souboj nebo karet v obchodě.
 * Pokud je předán Consumer callback, karty reagují na kliknutí a umožňují interakci.
 */
public class DeckPanel extends JPanel {
    private List<Card> cards;
    private Consumer<Card> onCardSelected; // Callback pro zpracování vybrané karty

    // Konstanty pro přesné vykreslení a výpočty hit-boxů mřížky
    private static final int CARD_WIDTH = 130;
    private static final int CARD_HEIGHT = 185;
    private static final int SPACING = 20; // Mezera mezi kartami

    /**
     * Konstruktor pro čistě pasivní zobrazení karet (bez možnosti na karty klikat).
     */
    public DeckPanel(List<Card> cards) {
        this(cards, null);
    }

    /**
     * Konstruktor s podporou interaktivního výběru karty.
     */
    public DeckPanel(List<Card> cards, Consumer<Card> onCardSelected) {
        this.cards = cards;
        this.onCardSelected = onCardSelected;
        setBackground(new Color(20, 20, 30));
        setLayout(null); // Absolutní pozicování, pozice karet se počítají ručně v paintComponent
        updatePreferredSize(800);

        // Sledování změny velikosti panelu pro přepočítání výšky skrolovací oblasti
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updatePreferredSize(getWidth());
            }
        });

        // Pokud byl definován callback pro kliknutí, aktivuje se poslouchaní myši
        if (onCardSelected != null) {
            setCursor(new Cursor(Cursor.HAND_CURSOR)); // Změna kurzoru na ručičku indikující interaktivitu
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int w = getWidth();
                    // Výpočet počtu sloupců a startovního odsazení (shodné s renderovací logikou)
                    int cols = Math.max(1, (w - SPACING) / (CARD_WIDTH + SPACING));
                    int gridWidth = cols * (CARD_WIDTH + SPACING) - SPACING;
                    int startX = (w - gridWidth) / 2;
                    int startY = SPACING + 10;

                    // Detekce, na kterou kartu (pokud vůbec nějakou) uživatel kliknul
                    for (int i = 0; i < cards.size(); i++) {
                        int row = i / cols;
                        int col = i % cols;
                        int x = startX + col * (CARD_WIDTH + SPACING);
                        int y = startY + row * (CARD_HEIGHT + SPACING);

                        // Kontrola zásahu hit-boxu konkrétní karty
                        if (e.getX() >= x && e.getX() <= x + CARD_WIDTH &&
                                e.getY() >= y && e.getY() <= y + CARD_HEIGHT) {
                            onCardSelected.accept(cards.get(i)); // Předání vybrané karty dál
                            break;
                        }
                    }
                }
            });
        }
    }

    /**
     * Dynamicky dopočítává výšku panelu podle počtu karet a aktuální šířky okna.
     * Nezbytné pro správné fungování uvnitř JScrollPane (aby rolování vědělo, jak hluboko může zajít).
     */
    private void updatePreferredSize(int panelWidth) {
        if (panelWidth <= 0) panelWidth = 800;
        int cols = Math.max(1, (panelWidth - SPACING) / (CARD_WIDTH + SPACING));
        int rows = (int) Math.ceil((double) cards.size() / cols);
        // Celková výška = řádky * (výška karty + mezera) + horní/spodní rezerva
        int preferredHeight = rows * (CARD_HEIGHT + SPACING) + SPACING + 40;
        setPreferredSize(new Dimension(panelWidth, preferredHeight));
        revalidate(); // Upozorní nadřazený JScrollPane na změnu rozměrů
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        // Spočítá, kolik sloupců karet se vedle sebe při aktuální šířce panelu vejde
        int cols = Math.max(1, (w - SPACING) / (CARD_WIDTH + SPACING));

        // Centrovaná mřížka: spočítá celkovou šířku karet a vycentruje počáteční X koordinátu
        int gridWidth = cols * (CARD_WIDTH + SPACING) - SPACING;
        int startX = (w - gridWidth) / 2;
        int startY = SPACING + 10;

        // Vykreslení jednotlivých karet na jejich matematicky dopočítané pozice
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            int row = i / cols;
            int col = i % cols;

            int x = startX + col * (CARD_WIDTH + SPACING);
            int y = startY + row * (CARD_HEIGHT + SPACING);

            drawDeckCard(g2, card, x, y);
        }
    }

    /**
     * Kompletně vykreslí vizuální reprezentaci jedné karty v přehledu.
     */
    private void drawDeckCard(Graphics2D g2, Card card, int x, int y) {
        // 1. Pozadí karty
        g2.setColor(new Color(35, 35, 50));
        g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);

        // 2. Ohraničení (červené pro útoky, modré pro skilly/ostatní)
        g2.setColor(card.getType() == Card.CardType.ATTACK ? new Color(180, 50, 50) : new Color(50, 120, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);

        // 3. Název karty (vycentrovaný)
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(card.getName(), x + (CARD_WIDTH - fmTitle.stringWidth(card.getName())) / 2, y + 25);

        // Oddělovací linka pod názvem
        g2.setColor(new Color(80, 80, 100));
        g2.drawLine(x + 15, y + 38, x + CARD_WIDTH - 15, y + 38);

        // 4. Poloprůhledný banner s typem karty
        g2.setColor(card.getType() == Card.CardType.ATTACK ? new Color(180, 50, 50, 80) : new Color(50, 120, 180, 80));
        g2.fillRoundRect(x + 15, y + 45, CARD_WIDTH - 30, 16, 4, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        String typeText = card.getType().toString();
        FontMetrics fmType = g2.getFontMetrics();
        g2.drawString(typeText, x + (CARD_WIDTH - fmType.stringWidth(typeText)) / 2, y + 57);

        // 5. Automaticky zalamovaný popis účinků karty
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

        // 6. Energetická cena karty (oranžový kruh v levém horním rohu)
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