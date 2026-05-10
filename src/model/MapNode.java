package model;

import java.util.ArrayList;
import java.util.List;

public class MapNode {
    private String id;
    private NodeType type;
    private int tier;
    private int column;
    private List<MapNode> nextNodes;
    private boolean visited = false;

    private int uiX;
    private int uiY;

    public MapNode(String id, NodeType type, int tier, int column) {
        this.id = id;
        this.type = type;
        this.tier = tier;
        this.column = column;
        this.nextNodes = new ArrayList<>();
    }

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

    public int getUiX() { return uiX; }
    public void setUiX(int uiX) { this.uiX = uiX; }
    public int getUiY() { return uiY; }
    public void setUiY(int uiY) { this.uiY = uiY; }
}
