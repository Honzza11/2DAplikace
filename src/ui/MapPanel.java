package ui;

import model.GameMap;
import model.MapNode;
import model.NodeType;
import model.Enemy;
import model.EnemyLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.Random;

/**
 * Panel zodpovědný za zobrazení a interakci s herní mapou.
 * Podporuje horizontální posun (panning) pomocí tažení myši a dynamické
 * přepínání herních obrazovek v závislosti na typu zvoleného uzlu.
 */
public class MapPanel extends JPanel {
    private GameMap gameMap;
    private GameWindow gameWindow;
    private boolean interactive = true; // Určuje, zda hrdina může na uzly klikat (např. falešné v náhledovém dialogu)

    // Konstanta definující velikost a rozestupy uzlů na plátně
    private static final int NODE_SIZE = 50;
    private static final int TIER_HEIGHT = 120; // Vertikální vzdálenost mezi patry
    private static final int COL_WIDTH = 150;   // Horizontální šířka sloupce pro uzly

    // Proměnné pro implementaci posouvání (panningu) mapy myší
    private int panX = 0;
    private int startPanX = 0;

    /**
     * Základní konstruktor pro plně interaktivní herní mapu.
     */
    public MapPanel(GameWindow gameWindow, GameMap map) {
        this(gameWindow, map, true);
    }

    /**
     * Detailní konstruktor umožňující vypnout interaktivitu (využíváno při zobrazení mapy jako pasivního dialogu).
     */
    public MapPanel(GameWindow gameWindow, GameMap map, boolean interactive) {
        this.gameWindow = gameWindow;
        this.gameMap = map;
        this.interactive = interactive;
        setLayout(null); // Absolutní pozicování pro vlastní vykreslení spojnic a uzlů
        setBackground(new Color(240, 218, 181)); // Podkladová barva starého pergamenu

        int numTiers = map.getTiers().size();
        int panelHeight = numTiers * TIER_HEIGHT + 100;
        setPreferredSize(new Dimension(1920, panelHeight));

        // Výpočet fixních souřadnic uzlů a inicializace klikatelných komponent
        calculateNodePositions(panelHeight);
        addNodeButtons();

        // --- LOGIKA POSOUVÁNÍ (PANNING) MAPY POMOCÍ MYŠI ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Zaznamenání počáteční pozice stisknutí pro plynulý dopočet posunu
                startPanX = e.getX() - panX;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Aktualizace horizontálního posunu na základě pohybu myši
                panX = e.getX() - startPanX;
                updateNodePositions(); // Přepozicování tlačítek uzlů
                repaint();             // Překreslení vykreslovaných propojovacích čar
            }
        });

        // Přepočítání pozic při změně velikosti okna (např. maximalizace)
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                calculateNodePositions(getHeight());
                updateNodePositions();
                repaint();
            }
        });
    }

    /**
     * Okamžitě vybere náhodnou obrazovku (místnost) a přepne na ni.
     * Nahrazuje dřívější komplexní RandomEventPanel.
     */
    private void triggerMysteryNode() {
        Random random = new Random();
        int roll = random.nextInt(5); // Vygeneruje číslo od 0 do 4

        switch (roll) {
            case 0:
                System.out.println("Mystery node -> Regular Enemy");
                // gameWindow.showScreen("ENEMY");
                // Musíme vygenerovat nepřítele a začít souboj, nestačí jen ukázat obrazovku
                Enemy normalTemplate = EnemyLoader.getRandomEnemyByPool("NORMAL");
                if (normalTemplate != null) {
                    gameWindow.startCombat(new Enemy(normalTemplate));
                } else {
                    gameWindow.startCombat(new Enemy("Mystery Slime", 50));
                }
                break;
            case 1:
                System.out.println("Mystery node -> Elite Enemy");
                Enemy eliteTemplate = EnemyLoader.getRandomEnemyByPool("ELITE");
                if (eliteTemplate != null) {
                    gameWindow.startCombat(new Enemy(eliteTemplate));
                } else {
                    gameWindow.startCombat(new Enemy("Mystery Elite", 100));
                }
                break;
            case 2:
                System.out.println("Mystery node -> Shop");
                gameWindow.showShop();
                break;
            case 3:
                System.out.println("Mystery node -> Rest Site");
                gameWindow.showRestSite();
                break;
            case 4:
                System.out.println("Mystery node -> Treasure Room");
                gameWindow.showTreasureReward();
                break;
        }
    }

    /**
     * Přesune vizuální komponenty uzlů (NodeButton) na základě aktuálního posunu (panX).
     */
    private void updateNodePositions() {
        for (Component c : getComponents()) {
            if (c instanceof NodeButton) {
                MapNode node = ((NodeButton) c).node;
                c.setBounds(node.getUiX() + panX, node.getUiY(), NODE_SIZE, NODE_SIZE);
            }
        }
    }

    /**
     * Matematicky dopočítá souřadnice X a Y pro každý uzel na mapě.
     * K základní mřížce přidává drobnou náhodnou odchylku (random jitter),
     * aby cesty nepůsobily strojově lineárně, ale organickým dojmem.
     */
    private void calculateNodePositions(int panelHeight) {
        List<List<MapNode>> tiers = gameMap.getTiers();
        int currentWidth = Math.max(1600, getWidth());

        // Procházení mapy odspodu (Tier 0) nahoru k Bossovi
        for (int t = 0; t < tiers.size(); t++) {
            List<MapNode> tier = tiers.get(t);
            int y = panelHeight - 100 - (t * TIER_HEIGHT);

            // Centrování aktuálního patra na střed dostupné šířky panelu
            int totalWidth = tier.size() * COL_WIDTH;
            int startX = (currentWidth - totalWidth) / 2 + (COL_WIDTH / 2) - (NODE_SIZE / 2);

            for (int c = 0; c < tier.size(); c++) {
                MapNode node = tier.get(c);
                int x = startX + (c * COL_WIDTH);

                // Přidání mírného vizuálního šumu, aby uzly nebyly dokonale zarovnané
                x += (Math.random() * 40 - 20);
                y += (Math.random() * 20 - 10);

                // Uložení vypočtených souřadnic přímo do datového modelu uzlu
                node.setUiX(x);
                node.setUiY(y);
            }
        }
    }

    /**
     * Vygeneruje a přidá komponenty NodeButton do panelu pro všechny existující uzly.
     */
    private void addNodeButtons() {
        for (List<MapNode> tier : gameMap.getTiers()) {
            for (MapNode node : tier) {
                // Boss má vizuálně větší ikonu než běžné uzly
                int size = (node.getType() == NodeType.BOSS) ? 80 : NODE_SIZE;
                NodeButton btn = new NodeButton(node);

                // Vyrovnání středu zvětšeného tlačítka vůči původní pozici uzlu
                int offsetX = (size - NODE_SIZE) / 2;
                int offsetY = (size - NODE_SIZE) / 2;
                btn.setBounds(node.getUiX() - offsetX + panX, node.getUiY() - offsetY, size, size);

                add(btn);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int currentWidth = Math.max(1600, getWidth());

        // Nastavení vzhledu propojovacích čar (přerušovaná poloprůhledná hnědá linie)
        g2.setColor(new Color(100, 70, 40, 150));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{10}, 0));

        // Vykreslení spojnic mezi uzly a jejich následníky (nextNodes)
        for (List<MapNode> tier : gameMap.getTiers()) {
            for (MapNode node : tier) {
                int startX = node.getUiX() + NODE_SIZE / 2 + panX;
                int startY = node.getUiY() + NODE_SIZE / 2;

                for (MapNode next : node.getNextNodes()) {
                    int endX = next.getUiX() + NODE_SIZE / 2 + panX;
                    int endY = next.getUiY() + NODE_SIZE / 2;
                    g2.drawLine(startX, startY, endX, endY);
                }
            }
        }
    }

    /**
     * Vnitřní privátní komponenta reprezentující jeden interaktivní bod (uzel) na mapě.
     */
    private class NodeButton extends JComponent {
        private MapNode node;

        public NodeButton(MapNode node) {
            this.node = node;
            if (interactive) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Kontrola ze strany herní logiky, zda je uzel pro hráče dosažitelný (sousední patro atd.)
                        if (gameMap.isNodeSelectable(node)) {
                            gameMap.setCurrentNode(node); // Označení uzlu za navštívený/aktuální
                            System.out.println("Moving to " + node.getType() + " at tier " + node.getTier());
                            getParent().repaint();

                            // Rozcestník akcí podle typu uzlu
                            if (node.getType() == NodeType.ENEMY || node.getType() == NodeType.ELITE || node.getType() == NodeType.BOSS) {
                                String poolType = "NORMAL";
                                if (node.getType() == NodeType.ELITE) poolType = "ELITE";
                                if (node.getType() == NodeType.BOSS) poolType = "BOSS";

                                // Vytažení náhodného nepřítele z načteného JSON fondu podle náročnosti
                                Enemy template = EnemyLoader.getRandomEnemyByPool(poolType);
                                if (template != null) {
                                    Enemy enemy = new Enemy(template); // Naklonování šablony do nového souboje
                                    gameWindow.startCombat(enemy);
                                } else {
                                    System.err.println("Warning: No enemy template found for pool: " + poolType);
                                    // Záložní potvora pro případ výpadku JSON dat, aby hra nespadla
                                    gameWindow.startCombat(new Enemy("Placeholder Slime", 50));
                                }
                            } else if (node.getType() == NodeType.TREASURE) {
                                gameWindow.showTreasureReward();
                            } else if (node.getType() == NodeType.EVENT) {
                                triggerMysteryNode();
                            } else if (node.getType() == NodeType.REST) {
                                gameWindow.showRestSite();
                            } else if (node.getType() == NodeType.SHOP) {
                                gameWindow.showShop();
                            }

                        } else {
                            System.out.println("Node not reachable!");
                        }
                    }
                });
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean isSelectable = gameMap.isNodeSelectable(node);
            boolean isVisited = node.isVisited();

            // 1. Určení barvy vnitřní výplně uzlu podle stavu
            if (isVisited) {
                g2.setColor(new Color(100, 80, 40)); // Již navštívený uzel (tmavší podbarvení)
            } else if (isSelectable) {
                g2.setColor(new Color(60, 60, 60));  // Dostupný uzel k výběru
            } else {
                g2.setColor(new Color(40, 40, 40, 150)); // Nedostupná cesta (poloprůhledná zašedlá)
            }
            g2.fillOval(0, 0, getWidth(), getHeight());

            // 2. Vykreslení vnějšího ohraničení kruhu
            if (isSelectable) {
                g2.setColor(Color.ORANGE);
                g2.setStroke(new BasicStroke(3));
            } else if (isVisited) {
                g2.setColor(new Color(255, 215, 0)); // Zlatá linka pro minulost
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(1));
            }
            g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);

            // 3. Vykreslení textového symbolu (Emoji) doprostřed uzlu
            if (isSelectable || isVisited) {
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(new Color(200, 200, 200, 100)); // Vybledlý symbol pro nepřístupné uzly
            }

            int fontSize = (node.getType() == NodeType.BOSS) ? 45 : 25;
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));

            String symbol = getSymbolForType(node.getType());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(symbol)) / 2;
            int ty = (getHeight() / 2) + (fm.getAscent() - fm.getDescent()) / 2;

            // Drobný posun pro obří ikonu Bosse na vrcholu mapy
            if (node.getType() == NodeType.BOSS) {
                ty += 2;
            }

            g2.drawString(symbol, tx, ty);

            // Pomocný prvek pro indikaci navštíveného uzlu (možnost rozšíření o zelené zatržítko)
            if (isVisited) {
                g2.setColor(Color.GREEN);
                g2.setFont(new Font("Arial", Font.BOLD, 15));
                g2.drawString("", 5, 15);
            }
        }

        /**
         * Mapuje typy uzlů z herního modelu na příslušné textové ikony/emoji.
         */
        private String getSymbolForType(NodeType type) {
            switch (type) {
                case ENEMY: return "😡";
                case EVENT: return "❓";
                case SHOP: return "💰";
                case ELITE: return "👿";
                case REST: return "🔥";
                case TREASURE: return "💎";
                case BOSS: return "     ⚔️";
                default: return "";
            }
        }
    }
}