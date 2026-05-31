package ui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Hlavní herní panel zajišťující vizuální vykreslení a interakci během souboje.
 * Stará se o vykreslení pozadí, postav (hráče a nepřítele), ukazatelů životů,
 * karet na ruce, relikvií a specifických herních mechanik (hudební lišta, přehřívání).
 * Zpracovává kliknutí myší pro zahrání karet a konec tahu.
 */
public class CombatPanel extends JPanel {
    private Player player;
    private Enemy enemy;
    private List<Card> hand;
    private Image backgroundImage;
    private Image playerImage;
    private Image enemyImage;
    private Image energyOrbImage;
    private GameWindow gameWindow;

    private int mouseX = -1;
    private int mouseY = -1;

    // Fixní rozměry karet pro výpočet hit-boxů při kliknutí
    private static final int CARD_WIDTH = 120;
    private static final int CARD_HEIGHT = 170;
    private JButton endTurnBtn;
    private JButton viewMapBtn;
    private JButton viewDeckBtn;

    public CombatPanel(GameWindow gameWindow, Player player, Enemy enemy) {
        this.gameWindow = gameWindow;
        this.player = player;
        this.enemy = enemy;
        this.hand = player.getHand();

        // --- NAČÍTÁNÍ GRAFICKÝCH ASSETŮ ---
        try {
            backgroundImage = new ImageIcon("Res/pozadi dung.jpg").getImage();

            // Výběr textury hráče podle konkrétní podtřídy
            if (player instanceof AshWalker) {
                playerImage = new ImageIcon("Res/assasin.png").getImage();
            } else if (player instanceof Bard) {
                playerImage = new ImageIcon("Res/bard111.png").getImage();
            }

            // Výběr textury nepřítele s fallbackem na sliza
            if (enemy != null) {
                String imgPath = enemy.getImagePath();

                if (imgPath == null || imgPath.isEmpty()) {
                    if (enemy.getName().toLowerCase().contains("slime")) {
                        imgPath = "Res/slime (1).png";
                    } else {
                        imgPath = "Res/slime (1).png";
                    }
                }
                enemyImage = new ImageIcon(imgPath).getImage();
            }

            energyOrbImage = new ImageIcon("Res/pngtree-red-energy-orb-png-image_19759672 (1).png").getImage();
        } catch (Exception e) {
            System.err.println("Could not load images: " + e.getMessage());
        }

        setLayout(null); // Absolutní pozicování prvků kvůli vlastnímu vykreslování
        setBackground(new Color(30, 30, 30));

        createButtons();

        // --- POSLUCHAČE UDÁLOSTÍ MYŠI ---
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleCardClick(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint(); // Překreslení kvůli tooltipům relikvií
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateLayout(); // Přepočítání pozic tlačítek při změně velikosti okna
            }
        });
    }

    /**
     * Přepočítává pozice karet na ruce a zjišťuje, zda uživatel kliknul do hitboxu některé z nich.
     * Pokud ano, pokusí se kartu zahrát a ověří případnou smrt nepřítele/vítězství v souboji.
     */
    private void handleCardClick(int mouseX, int mouseY) {
        int w = getWidth();
        int h = getHeight();
        int uiStartY = (int)(h * 0.7);
        int uiHeight = (int)(h * 0.3);
        int handSize = hand.size();
        if (handSize == 0) return;

        // Výpočet dynamického středu pro vějíř/řadu karet
        int totalHandWidth = handSize * (CARD_WIDTH + 15);
        int startX = (w - totalHandWidth) / 2;
        int handY = uiStartY + (uiHeight - CARD_HEIGHT) / 2;

        for (int i = 0; i < handSize; i++) {
            int cardX = startX + i * (CARD_WIDTH + 15);
            // Kontrola kolize myši s obdélníkem karty
            if (mouseX >= cardX && mouseX <= cardX + CARD_WIDTH &&
                    mouseY >= handY && mouseY <= handY + CARD_HEIGHT) {

                Card card = hand.get(i);

                if (player.playCard(card, enemy)) {
                    System.out.println("Played: " + card.getName());
                    repaint();

                    // 🌟 KONTROLA: Smrt nepřítele / Vítězství
                    if (enemy.isDead()) {
                        System.out.println("Enemy defeated!");

                        // Vyčištění bojových balíčků zpět do hlavního decku
                        player.getDeck().addAll(player.getHand());
                        player.getDeck().addAll(player.getDiscardPile());
                        player.getHand().clear();
                        player.getDiscardPile().clear();
                        player.resetBlock();
                        player.clearStatuses();

                        // Rozhodnutí o typu výhry podle jména nepřítele
                        if (enemy.getName().toLowerCase().contains("boss") || enemy.getName().toLowerCase().contains("pyre")) {
                            gameWindow.showWinScreen();
                        } else {
                            boolean isElite = enemy.getName().toLowerCase().contains("elite");
                            gameWindow.showCombatReward(isElite);
                        }
                        return;
                    }
                }
                break;
            }
        }
    }

    /**
     * Inicializuje ovládací tlačítka na obrazovce a definuje logiku tlačítka END TURN
     * (zahazování karet, tah nepřítele, spuštění ozvěnových karet Barda a příprava nového kola).
     */
    private void createButtons() {
        viewMapBtn = new JButton("VIEW MAP");
        viewMapBtn.setFocusPainted(false);
        viewMapBtn.addActionListener(e -> gameWindow.showMapDialog());
        add(viewMapBtn);

        viewDeckBtn = new JButton("VIEW DECK");
        viewDeckBtn.setFocusPainted(false);
        viewDeckBtn.addActionListener(e -> gameWindow.showDeckDialog());
        add(viewDeckBtn);

        endTurnBtn = new JButton("END TURN");
        endTurnBtn.setFont(new Font("Arial", Font.BOLD, 20));
        endTurnBtn.setBackground(new Color(150, 50, 50));
        endTurnBtn.setForeground(Color.WHITE);
        endTurnBtn.setFocusPainted(false);
        endTurnBtn.addActionListener(e -> {
            player.discardHand();

            if (player instanceof AshWalker) {
                ((AshWalker) player).endTurn();
            }

            if (player.getHealth() <= 0) {
                gameWindow.showGameOverScreen();
                return;
            }

            // --- TAH NEPŘÍTELE ---
            if (enemy != null && !enemy.isDead()) {
                enemy.takeTurn(player);
            }

            if (player.getHealth() <= 0) {
                gameWindow.showGameOverScreen();
                return;
            }

            // Příprava na nový tah hráče
            player.startTurn();

            // Specifická mechanika Barda: Zahrání "Echo" karet na začátku nového tahu
            if (player instanceof Bard && enemy != null) {
                ((Bard) player).playEchoCards(enemy);

                if (enemy.isDead()) {
                    player.getDeck().addAll(player.getHand());
                    player.getDeck().addAll(player.getDiscardPile());
                    player.getHand().clear();
                    player.getDiscardPile().clear();
                    player.resetBlock();
                    player.clearStatuses();

                    if (enemy.getName().toLowerCase().contains("boss") || enemy.getName().toLowerCase().contains("pyre")) {
                        gameWindow.showWinScreen();
                    } else {
                        gameWindow.showCombatReward(enemy.getName().toLowerCase().contains("elite"));
                    }
                    return;
                }
            }

            repaint();
        });
        add(endTurnBtn);
    }

    /**
     * Zarovná tlačítka do rohů obrazovky podle aktuálních rozměrů okna.
     */
    private void updateLayout() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        viewMapBtn.setBounds(w - 180, 20, 150, 40);
        viewDeckBtn.setBounds(w - 340, 20, 150, 40);
        endTurnBtn.setBounds(w - 200, h - 100, 150, 60);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Zapnutí antialiasingu pro vyhlazení textů a tvarů
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2);
        drawEntities(g2);
        drawHand(g2);
        drawUIOverlay(g2);
        drawTooltips(g2);
    }

    /**
     * Vykreslí bojiště (horních 70 % s obrázkem dungu) a spodní lištu pro karty (30 % černá plocha).
     */
    private void drawBackground(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int uiHeight = (int)(h * 0.3);
        int battleHeight = h - uiHeight;

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, w, battleHeight, null);
            g2.setColor(new Color(0, 0, 0, 40)); // Jemné ztmavení pozadí
            g2.fillRect(0, 0, w, battleHeight);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(40, 20, 20), 0, battleHeight, new Color(10, 10, 10));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, battleHeight);
        }

        // Spodní panel pro karty
        g2.setColor(Color.BLACK);
        g2.fillRect(0, battleHeight, w, uiHeight);

        // Předělová linie mezi bojištěm a kartami
        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, battleHeight, w, battleHeight);
    }

    /**
     * Spočítá pozice a vykreslí hráče a nepřítele na bojišti.
     */
    private void drawEntities(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int battleHeight = (int)(h * 0.7);

        drawEntity(g2, player, (int)(w * 0.3) - 100, battleHeight - 370, Color.CYAN);

        if (enemy != null) {
            drawEntity(g2, enemy, (int)(w * 0.7) - 100, battleHeight - 370, Color.RED);
        }
    }

    /**
     * Vykreslí konkrétní entitu (stín pod nohama, texturu, jméno, záměr u potvor,
     * bar životů s číselným stavem, štíty a ikonky aktivních statusů).
     */
    private void drawEntity(Graphics2D g2, Entity entity, int x, int y, Color color) {
        String name = entity.getName();
        int hp = entity.getHealth();
        int maxHp = entity.getMaxHealth();
        int block = entity.getBlock();

        // Stín pod postavou
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillOval(x - 15, y + 285, 230, 25);

        // Vykreslení obrázku / zástupného oválu
        if (playerImage != null && name.equals(player.getName())) {
            drawImagePreservingAspectRatio(g2, playerImage, x, y, 200, 300);
        } else if (enemyImage != null && enemy != null && name.equals(enemy.getName())) {
            drawImagePreservingAspectRatio(g2, enemyImage, x, y, 200, 300);
        } else {
            g2.setColor(color);
            g2.fillOval(x, y, 200, 300);
        }

        // Vykreslení jména s černým obrysem (outline) pro lepší čitelnost
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fmEntity = g2.getFontMetrics();
        int nameX = x + (200 - fmEntity.stringWidth(name)) / 2;
        int nameY = y - 60;

        g2.setColor(Color.WHITE);
        g2.drawString(name, nameX - 1, nameY - 1);
        g2.drawString(name, nameX + 1, nameY - 1);
        g2.drawString(name, nameX - 1, nameY + 1);
        g2.drawString(name, nameX + 1, nameY + 1);
        g2.drawString(name, nameX - 1, nameY);
        g2.drawString(name, nameX + 1, nameY);
        g2.drawString(name, nameX, nameY - 1);
        g2.drawString(name, nameX, nameY + 1);

        g2.setColor(Color.BLACK);
        g2.drawString(name, nameX, nameY);

        // Pokud jde o nepřítele, vykreslíme nad ním jeho chystaný tah (Intent)
        if (entity instanceof Enemy) {
            Enemy e = (Enemy) entity;
            g2.setColor(new Color(255, 200, 0));
            g2.setFont(new Font("Arial", Font.ITALIC, 18));
            String intentStr = "Intent: " + e.getIntentDescription();
            FontMetrics fmIntent = g2.getFontMetrics();
            int intentX = x + (200 - fmIntent.stringWidth(intentStr)) / 2;
            g2.drawString(intentStr, intentX, y - 90);
        }

        // --- HEALTH BAR ---
        int barWidth = 200;
        int barHeight = 20;
        int healthWidth = (int) ((double) hp / Math.max(1, maxHp) * barWidth);

        g2.setColor(Color.GRAY);
        g2.fillRect(x, y - 40, barWidth, barHeight);
        g2.setColor(Color.GREEN);
        g2.fillRect(x, y - 40, healthWidth, barHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y - 40, barWidth, barHeight);

        // Textový stav životů uvnitř baru
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String hpStr = hp + " / " + maxHp;
        FontMetrics fmHp = g2.getFontMetrics();
        int hpX = x + (barWidth - fmHp.stringWidth(hpStr)) / 2;
        g2.drawString(hpStr, hpX, y - 25);

        // --- BLOCK ICON (Modrý štít) ---
        if (block > 0) {
            g2.setColor(new Color(50, 150, 250));
            g2.fillRoundRect(x - 33, y - 44, 28, 28, 6, 6);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(x - 33, y - 44, 28, 28, 6, 6);
            g2.setFont(new Font("Arial", Font.BOLD, 13));

            String blockStr = String.valueOf(block);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (x - 33) + (28 - fm.stringWidth(blockStr)) / 2;
            int textY = (y - 44) + ((28 - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(blockStr, textX, textY);
        }

        // --- STATUS ICONS (Vulnerable / Weak) ---
        int statusY = y - 75;
        int statusX = x;
        if (entity.getVulnerableTurns() > 0) {
            g2.setColor(new Color(150, 0, 200));
            g2.fillRoundRect(statusX, statusY, 24, 24, 4, 4);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("V" + entity.getVulnerableTurns(), statusX + 4, statusY + 16);
            statusX += 30;
        }
        if (entity.getWeakTurns() > 0) {
            g2.setColor(new Color(100, 150, 50));
            g2.fillRoundRect(statusX, statusY, 24, 24, 4, 4);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("W" + entity.getWeakTurns(), statusX + 4, statusY + 16);
        }
    }

    /**
     * Vykreslí vějíř/řadu karet na ruce hráče do spodní UI zóny.
     */
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

    /**
     * Kompletně vykreslí vizuální podobu jedné karty (rám, jméno, typ, popis s wrappingem textu a cenu).
     */
    private void drawCard(Graphics2D g2, Card card, int x, int y) {
        // Tělo karty
        g2.setColor(new Color(30, 30, 45));
        g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 12, 12);
        g2.setColor(new Color(255, 170, 0, 200));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 12, 12);

        // Jméno karty
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(card.getName(), x + 20, y + 25);

        g2.setColor(new Color(80, 80, 100));
        g2.drawLine(x + 10, y + 35, x + CARD_WIDTH - 10, y + 35);

        // Barevný štítek typu karty (Útok = červený, Skill = modrý)
        g2.setColor(card.getType() == Card.CardType.ATTACK ? new Color(180, 50, 50) : new Color(50, 120, 180));
        g2.fillRoundRect(x + 15, y + 42, CARD_WIDTH - 30, 16, 4, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 9));
        String typeText = card.getType().toString();
        FontMetrics fmType = g2.getFontMetrics();
        g2.drawString(typeText, x + (CARD_WIDTH - fmType.stringWidth(typeText)) / 2, y + 54);

        // --- ZALOMENÍ (WRAPPING) POPISU KARTY ---
        g2.setColor(new Color(220, 220, 220));
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        String desc = card.getDescription();
        if (desc != null && !desc.isEmpty()) {
            String[] words = desc.split(" ");
            StringBuilder currentLine = new StringBuilder();
            int lineY = y + 80;
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

        // --- ENERGETICKÁ CENA (Kolečko v levém horním rohu) ---
        g2.setColor(new Color(230, 90, 40));
        g2.fillOval(x - 8, y - 8, 22, 22);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x - 8, y - 8, 22, 22);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        String costStr = String.valueOf(card.getEnergyCost());
        FontMetrics fmCost = g2.getFontMetrics();
        g2.drawString(costStr, x - 8 + (22 - fmCost.stringWidth(costStr)) / 2, y - 8 + ((22 - fmCost.getHeight()) / 2) + fmCost.getAscent());
    }

    /**
     * Vykreslí globální UI prvky jako energetický orb, počty karet v balíčcích,
     * seznam relikvií a specifické ukazatele hrdinů (Heat bar u AshWalkera, notovou osnovu u Barda).
     */
    private void drawUIOverlay(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int uiStartY = (int)(h * 0.7);
        int uiHeight = (int)(h * 0.3);

        // --- ENERGETICKÝ ORB ---
        int orbX = 50;
        int orbY = uiStartY + (uiHeight - 100) / 2;
        if (energyOrbImage != null) {
            g2.drawImage(energyOrbImage, orbX, orbY, 100, 100, null);
        } else {
            g2.setColor(Color.ORANGE);
            g2.fillOval(orbX, orbY, 100, 100);
        }

        String energyText = player.getEnergy() + "/" + player.getMaxEnergy();
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fmEnergy = g2.getFontMetrics();
        int textX = orbX + (100 - fmEnergy.stringWidth(energyText)) / 2;
        int textY = orbY + ((100 - fmEnergy.getHeight()) / 2) + fmEnergy.getAscent();

        g2.setColor(Color.BLACK);
        g2.drawString(energyText, textX + 2, textY + 2); // Stín textu
        g2.setColor(Color.WHITE);
        g2.drawString(energyText, textX, textY);

        // Počty karet v balíčcích
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Draw: " + player.getDeck().size(), 50, uiStartY + 30);
        g2.drawString("Discard: " + player.getDiscardPile().size(), 50, h - 30);

        // --- VYKRESLENÍ RELIKVIÍ (v levém horním rohu) ---
        int rx = 20;
        int ry = 20;
        for (Relic relic : player.getRelics()) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillOval(rx + 2, ry + 2, 36, 36);

            g2.setColor(new Color(60, 45, 20));
            g2.fillOval(rx, ry, 36, 36);
            g2.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(rx, ry, 36, 36);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            String initial = relic.getName().substring(0, 1).toUpperCase();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(initial, rx + (36 - fm.stringWidth(initial)) / 2, ry + ((36 - fm.getHeight()) / 2) + fm.getAscent());

            rx += 46;
        }

        // --- SPECIFICKÉ UI PRO ASHWALKERA (HEAT) ---
        if (player instanceof AshWalker) {
            AshWalker ash = (AshWalker) player;
            int heat = ash.getHeat();
            int bonusDmg = ash.getDamageBonus();

            g2.setFont(new Font("Arial", Font.BOLD, 18));
            if (heat <= 10) {
                g2.setColor(Color.ORANGE);
                g2.drawString("Heat: " + heat + " (Damage Bonus: +" + bonusDmg + ")", 50, uiStartY + 60);
            } else {
                g2.setColor(Color.RED);
                g2.drawString("OVERHEAT! Heat: " + heat + " (Will take 5 DMG, Bonus: +" + bonusDmg + ")", 50, uiStartY + 60);
            }
        }

        // --- SPECIFICKÉ UI PRO BARDA (NOTY A KOMBA) ---
        if (player instanceof Bard) {
            Bard bard = (Bard) player;
            List<Bard.Tone> tones = bard.getCurrentTones();

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString("Song Bar:", 20, 90);

            // Vykreslení tří kruhových slotů pro zahrané tóny
            int startX = 20;
            int cy = 100;
            int circle = 16;
            int chordSize = 3;
            for (int i = 0; i < chordSize; i++) {
                int cx = startX + i * 26;
                boolean filled = i < tones.size();
                Color fill = Color.GRAY;
                if (filled) {
                    Bard.Tone t = tones.get(i);
                    if (t == Bard.Tone.RED) fill = Color.RED;
                    if (t == Bard.Tone.BLUE) fill = new Color(80, 160, 255);
                    if (t == Bard.Tone.GREEN) fill = new Color(70, 200, 120);
                }
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillOval(cx + 2, cy + 2, circle, circle);
                g2.setColor(fill);
                g2.fillOval(cx, cy, circle, circle);
                g2.setColor(Color.WHITE);
                g2.drawOval(cx, cy, circle, circle);
            }

            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.setColor(new Color(220, 220, 220));
            g2.drawString("Last Note: " + (tones.isEmpty() ? "None" : tones.get(tones.size() - 1)), 20, 135);

            // Tahák na kombinace tónů (Cheat sheet) v pravé části
            int comboX = w - 280;
            int comboY = 80;
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(comboX, comboY, 260, 100, 10, 10);
            g2.setColor(new Color(150, 150, 150));
            g2.drawRoundRect(comboX, comboY, 260, 100, 10, 10);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("Combo Cheat Sheet", comboX + 10, comboY + 20);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(new Color(255, 100, 100));
            g2.drawString("🔴🔴🔴: Destructive Anthem (14 DMG)", comboX + 10, comboY + 45);
            g2.setColor(new Color(100, 200, 255));
            g2.drawString("🔵🔵🔵: Serenity Lullaby (12 BLK, Weak)", comboX + 10, comboY + 65);
            g2.setColor(new Color(100, 255, 150));
            g2.drawString("Mixed / 🟢🟢🟢: Resonant Echo (+1 EN, 1 Card)", comboX + 10, comboY + 85);
        }
    }

    /**
     * Kontroluje, zda se kurzor myši nenachází nad některou z relikvií.
     * Pokud ano, aktivuje vykreslení popisku (tooltipu).
     */
    private void drawTooltips(Graphics2D g2) {
        if (mouseX < 0 || mouseY < 0) return;

        int panelW = getWidth();
        int panelH = getHeight();
        int rx = 20;
        int ry = 20;

        for (Relic relic : player.getRelics()) {
            Rectangle iconBounds = new Rectangle(rx, ry, 36, 36);
            if (iconBounds.contains(mouseX, mouseY)) {
                renderRelicTooltip(g2, relic, mouseX, mouseY, panelW, panelH);
                return;
            }
            rx += 46;
        }
    }

    /**
     * Vykreslí elegantní poloprůhledné okno s názvem a popisem relikvie vedle kurzoru myši.
     * Automaticky hlídá okraje obrazovky, aby tooltip neodcestoval mimo viditelnou plochu.
     */
    private void renderRelicTooltip(Graphics2D g2, Relic relic, int mouseX, int mouseY, int panelW, int panelH) {
        final int padding = 8;
        final int corner = 12;

        Font titleFont = new Font("Arial", Font.BOLD, 16);
        Font descFont = new Font("Arial", Font.PLAIN, 13);
        g2.setFont(titleFont);
        FontMetrics fmTitle = g2.getFontMetrics();

        String title = relic.getName();
        String desc = relic.getDescription() != null ? relic.getDescription() : "";

        int maxWrapWidth = Math.max(120, Math.min(260, panelW - 40));
        g2.setFont(descFont);
        FontMetrics fmDesc = g2.getFontMetrics();

        List<String> lines = wrapText(g2, desc, maxWrapWidth);

        int titleWidth = fmTitle.stringWidth(title);
        int maxLineWidth = titleWidth;
        for (String line : lines) {
            maxLineWidth = Math.max(maxLineWidth, fmDesc.stringWidth(line));
        }

        int tooltipW = maxLineWidth + padding * 2;
        int lineHeight = fmDesc.getHeight();
        int tooltipH = padding * 2 + fmTitle.getHeight() + 6 + lines.size() * lineHeight;

        int tooltipX = mouseX + 15;
        int tooltipY = mouseY + 15;

        // Korekce pozice tooltipu u okrajů obrazovky
        if (tooltipX + tooltipW > panelW - 5) tooltipX = mouseX - 15 - tooltipW;
        if (tooltipX < 5) tooltipX = 5;
        if (tooltipY + tooltipH > panelH - 5) tooltipY = mouseY - 15 - tooltipH;
        if (tooltipY < 5) tooltipY = 5;

        // Pozadí a ohraničení okna tooltipu
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, corner, corner);
        g2.setColor(new Color(255, 180, 50, 160));
        g2.drawRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, corner, corner);

        // Vykreslení textu nad tooltipem
        int textX = tooltipX + padding;
        int y = tooltipY + padding + fmTitle.getAscent();
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        g2.drawString(title, textX, y);

        y += 6 + fmDesc.getAscent();
        g2.setFont(descFont);
        g2.setColor(new Color(230, 230, 230));
        for (String line : lines) {
            g2.drawString(line, textX, y);
            y += lineHeight;
        }
    }

    /**
     * Pomocná metoda pro rozsekání dlouhého textu popisku do více řádků
     * na základě maximální povolené šířky v pixelech.
     */
    private List<String> wrapText(Graphics2D g2, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            if (word == null || word.isEmpty()) continue;

            String test = current.length() == 0 ? word : current + " " + word;
            if (fm.stringWidth(test) > maxWidth && current.length() > 0) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                if (current.length() == 0) current.append(word);
                else current.append(" ").append(word);
            }
        }

        if (current.length() > 0) {
            lines.add(current.toString());
        }

        return lines;
    }

    /**
     * Zajišťuje správné škálování obrázků postav bez deformace poměru stran.
     * Zarovnává obrázek na střed vymezeného boxu a spodní hranu ukotvuje na "podlahu".
     */
    private void drawImagePreservingAspectRatio(Graphics2D g2, Image img, int boxX, int boxY, int boxW, int boxH) {
        int imgW = img.getWidth(null);
        int imgH = img.getHeight(null);
        if (imgW <= 0 || imgH <= 0) {
            g2.drawImage(img, boxX, boxY, boxW, boxH, null);
            return;
        }

        double imgAspect = (double) imgW / imgH;
        double boxAspect = (double) boxW / boxH;

        int drawW = boxW;
        int drawH = boxH;

        // Přepočet velikosti podle dominantní strany poměru
        if (imgAspect > boxAspect) {
            drawW = boxW;
            drawH = (int) (boxW / imgAspect);
        } else {
            drawH = boxH;
            drawW = (int) (boxH * imgAspect);
        }

        int drawX = boxX + (boxW - drawW) / 2;
        int drawY = boxY + (boxH - drawH); // Ukotvení k zemi bojiště

        g2.drawImage(img, drawX, drawY, drawW, drawH, null);
    }
}