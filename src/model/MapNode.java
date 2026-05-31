package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentuje jeden konkrétní uzel (místnost/bod) na herní mapě.
 * Funguje jako prvek orientovaného grafu, který si pamatuje svou pozici (patro a sloupec),
 * svůj typ, stav navštívení a seznam uzlů, do kterých z něj může hráč pokračovat.
 * Obsahuje také souřadnice pro vykreslení v UI.
 */
public class MapNode {
    private String id;
    private NodeType type;
    private int tier;
    private int column;

    /** Seznam uzlů v následujícím patře, do kterých vedou z tohoto místa cesty. */
    private List<MapNode> nextNodes;
    private boolean visited = false;

    // Souřadnice používané pro vykreslení uzlu na obrazovce
    private int uiX;
    private int uiY;

    public MapNode(String id, NodeType type, int tier, int column) {
        this.id = id;
        this.type = type;
        this.tier = tier;
        this.column = column;
        this.nextNodes = new ArrayList<>();
    }

    /**
     * Přidá propojení (hranu grafu) do dalšího uzlu.
     * Kontrola preventivně brání vzniku duplicitních cest mezi stejnými dvěma uzly.
     * * @param node Cílový uzel v dalším patře.
     */
    public void addNextNode(MapNode node) {
        if (!nextNodes.contains(node)) {
            nextNodes.add(node);
        }
    }

    public String getId() { return id; }
    public NodeType getType() { return type; }
    public int getTier() { return tier; }
    public int getColumn() { return column; }
    public List<MapNode> getNextNodes() { return nextNodes; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }

    // --- GETTERY A SETTERY PRO UI ---
    public int getUiX() { return uiX; }
    /** Alias pro snadnější přístup k X souřadnici při vykreslování komponent. */
    public int getX() { return uiX; }
    public void setUiX(int uiX) { this.uiX = uiX; }

    public int getUiY() { return uiY; }
    /** Alias pro snadnější přístup k Y souřadnici při vykreslování komponent. */
    public int getY() { return uiY; }
    public void setUiY(int uiY) { this.uiY = uiY; }
}