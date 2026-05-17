package ui;

import model.GameMap;
import model.MapNode;
import model.NodeType;
import model.Enemy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MapPanel extends JPanel {
    private GameMap gameMap;
    private GameWindow gameWindow;
    private boolean interactive = true;
    private static final int NODE_SIZE = 50;
    private static final int TIER_HEIGHT = 120;
    private static final int COL_WIDTH = 150;

    public MapPanel(GameWindow gameWindow, GameMap map) {
        this(gameWindow, map, true);
    }

    public MapPanel(GameWindow gameWindow, GameMap map, boolean interactive) {
        this.gameWindow = gameWindow;
        this.gameMap = map;
        this.interactive = interactive;
        setLayout(null);
        setBackground(new Color(240, 218, 181)); // Parchment color

        int numTiers = map.getTiers().size();
        int panelHeight = numTiers * TIER_HEIGHT + 100;
        setPreferredSize(new Dimension(1920, panelHeight)); // Base width, will center relative to actual width

        calculateNodePositions(panelHeight);
        addNodeButtons();


        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                calculateNodePositions(getHeight());
                for (Component c : getComponents()) {
                    if (c instanceof NodeButton) {
                        MapNode node = ((NodeButton) c).node;
                        c.setBounds(node.getX(), node.getY(), NODE_SIZE, NODE_SIZE);
                    }
                }
                repaint();
            }
        });
    }

    private void calculateNodePositions(int panelHeight) {
        List<List<MapNode>> tiers = gameMap.getTiers();
        int currentWidth = Math.max(1600, getWidth()); // Ensure a minimum width for centering
        
        for (int t = 0; t < tiers.size(); t++) {
            List<MapNode> tier = tiers.get(t);
            int y = panelHeight - 100 - (t * TIER_HEIGHT);
            
            int totalWidth = tier.size() * COL_WIDTH;
            int startX = (currentWidth - totalWidth) / 2 + (COL_WIDTH / 2) - (NODE_SIZE / 2);

            for (int c = 0; c < tier.size(); c++) {
                MapNode node = tier.get(c);
                int x = startX + (c * COL_WIDTH);

                x += (Math.random() * 40 - 20);
                y += (Math.random() * 20 - 10);
                
                node.setUiX(x);
                node.setUiY(y);
            }
        }
    }

    private void addNodeButtons() {
        for (List<MapNode> tier : gameMap.getTiers()) {
            for (MapNode node : tier) {
                int size = (node.getType() == NodeType.BOSS) ? 80 : NODE_SIZE;
                NodeButton btn = new NodeButton(node);
                

                int offsetX = (size - NODE_SIZE) / 2;
                int offsetY = (size - NODE_SIZE) / 2;
                btn.setBounds(node.getUiX() - offsetX, node.getUiY() - offsetY, size, size);
                
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
        
        g2.setColor(new Color(100, 70, 40, 150));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{10}, 0));

        for (List<MapNode> tier : gameMap.getTiers()) {
            for (MapNode node : tier) {
                int startX = node.getUiX() + NODE_SIZE / 2;
                int startY = node.getUiY() + NODE_SIZE / 2;

                for (MapNode next : node.getNextNodes()) {
                    int endX = next.getUiX() + NODE_SIZE / 2;
                    int endY = next.getUiY() + NODE_SIZE / 2;
                    g2.drawLine(startX, startY, endX, endY);
                }
            }
        }
    }


    private class NodeButton extends JComponent {
        private MapNode node;

        public NodeButton(MapNode node) {
            this.node = node;
            if (interactive) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameMap.isNodeSelectable(node)) {
                        gameMap.setCurrentNode(node);
                        System.out.println("Moving to " + node.getType() + " at tier " + node.getTier());
                        getParent().repaint();
                        

                        if (node.getType() == NodeType.ENEMY || node.getType() == NodeType.ELITE) {
                            String enemyName = (node.getType() == NodeType.ELITE) ? "Elite Slime" : "Slime";
                            int hp = (node.getType() == NodeType.ELITE) ? 80 : 50;
                            Enemy enemy = new Enemy(enemyName, hp);
                            gameWindow.startCombat(enemy);
                        } else if (node.getType() == NodeType.TREASURE) {
                            gameWindow.showTreasureReward();
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
            

            if (isVisited) {
                g2.setColor(new Color(100, 80, 40));
            } else if (isSelectable) {
                g2.setColor(new Color(60, 60, 60));
            } else {
                g2.setColor(new Color(40, 40, 40, 150));
            }
            g2.fillOval(0, 0, getWidth(), getHeight());
            

            if (isSelectable) {
                g2.setColor(Color.ORANGE);
                g2.setStroke(new BasicStroke(3));
            } else if (isVisited) {
                g2.setColor(new Color(255, 215, 0));
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(1));
            }
            g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);


            if (isSelectable || isVisited) {
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(new Color(200, 200, 200, 100));
            }

            int fontSize = (node.getType() == NodeType.BOSS) ? 45 : 25;
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
            
            String symbol = getSymbolForType(node.getType());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(symbol)) / 2;
            int ty = (getHeight() / 2) + (fm.getAscent() - fm.getDescent()) / 2;
            
            if (node.getType() == NodeType.BOSS) {
                ty += 2; 
            }

            g2.drawString(symbol, tx, ty);
            

            if (isVisited) {
                g2.setColor(Color.GREEN);
                g2.setFont(new Font("Arial", Font.BOLD, 15));
                g2.drawString("", 5, 15);
            }
        }

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
