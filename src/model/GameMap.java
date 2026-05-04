package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMap {
    private List<List<MapNode>> tiers;
    private int numTiers;
    private int maxColumns;
    private Random random;

    public GameMap(int numTiers, int maxColumns) {
        this.numTiers = numTiers;
        this.maxColumns = maxColumns;
        this.tiers = new ArrayList<>();
        this.random = new Random();
        generateMap();
    }

    private void generateMap() {

        for (int t = 0; t < numTiers; t++) {
            List<MapNode> tier = new ArrayList<>();

            int numNodes;
            if (t == numTiers - 1) numNodes = 1;
            else if (t == 0) numNodes = 3;
            else numNodes = 3 + random.nextInt(2);
            
            for (int c = 0; c < numNodes; c++) {
                NodeType type = determineNodeType(t);
                MapNode node = new MapNode(t + "-" + c, type, t, c);
                tier.add(node);
            }
            tiers.add(tier);
        }


        for (int t = 0; t < numTiers - 1; t++) {
            List<MapNode> currentTier = tiers.get(t);
            List<MapNode> nextTier = tiers.get(t + 1);
            
            for (int c = 0; c < currentTier.size(); c++) {
                MapNode node = currentTier.get(c);
                
                if (t == numTiers - 2) {

                    node.addNextNode(nextTier.get(0));
                    continue;
                }


                float ratio = (float) c / Math.max(1, currentTier.size() - 1);
                int targetCenter = Math.round(ratio * (nextTier.size() - 1));
                

                node.addNextNode(nextTier.get(targetCenter));
                

                if (random.nextDouble() < 0.4) {
                    int offset = random.nextBoolean() ? 1 : -1;
                    int extraCol = targetCenter + offset;
                    if (extraCol >= 0 && extraCol < nextTier.size()) {
                        node.addNextNode(nextTier.get(extraCol));
                    }
                }
            }
            

            for (int nc = 0; nc < nextTier.size(); nc++) {
                MapNode nextNode = nextTier.get(nc);
                boolean hasIncoming = false;
                for (MapNode currNode : currentTier) {
                    if (currNode.getNextNodes().contains(nextNode)) {
                        hasIncoming = true;
                        break;
                    }
                }
                
                if (!hasIncoming) {

                    float ratio = (float) nc / Math.max(1, nextTier.size() - 1);
                    int closestCurr = Math.round(ratio * (currentTier.size() - 1));
                    currentTier.get(closestCurr).addNextNode(nextNode);
                }
            }
        }
    }

    private NodeType determineNodeType(int tier) {
        if (tier == 0) return NodeType.ENEMY; 
        if (tier == numTiers - 1) return NodeType.BOSS;

        int rand = random.nextInt(100);
        if (rand < 40) return NodeType.ENEMY;
        if (rand < 65) return NodeType.EVENT;
        if (rand < 80) return NodeType.SHOP;
        if (rand < 90) return NodeType.REST;
        return NodeType.ELITE;
    }

    public List<List<MapNode>> getTiers() {
        return tiers;
    }
}
