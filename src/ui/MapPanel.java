package ui;

import model.GameMap;
import model.MapNode;
import model.NodeType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MapPanel extends JPanel {
    private GameMap gameMap;
    private static final int NODE_SIZE = 50;
    private static final int TIER_HEIGHT = 120;
    private static final int COL_WIDTH = 150;

    public MapPanel(GameMap map) {
        this.gameMap = map;
        setLayout(null);
        setBackground(new Color(240, 218, 181)); // Parchment color

        int numTiers = map.getTiers().size();
        int panelHeight = numTiers * TIER_HEIGHT + 100;
        setPreferredSize(new Dimension(1920, panelHeight));

        calculateNodePositions(panelHeight);
        addNodeButtons();
    }

    private void calculateNodePositions(int panelHeight) {
        List<List<MapNode>> tiers = gameMap.getTiers();
        for (int t = 0; t < tiers.size(); t++) {
            List<MapNode> tier = tiers.get(t);

            int y = panelHeight - 100 - (t * TIER_HEIGHT);
            

            int totalWidth = tier.size() * COL_WIDTH;
            int startX = (1920 - totalWidth) / 2 + (COL_WIDTH / 2) - (NODE_SIZE / 2);

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

        drawPaths(g2);
    }

    private void drawPaths(Graphics2D g2) {
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2.setStroke(dashed);
        g2.setColor(new Color(100, 100, 100, 150));

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
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Clicked on " + node.getType() + " at tier " + node.getTier());

                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(Color.DARK_GRAY);
            g2.fillOval(0, 0, getWidth(), getHeight());
            
            g2.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);

            g2.setColor(Color.WHITE);
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
