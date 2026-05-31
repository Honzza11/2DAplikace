package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Třída zajišťující procedurální generování a správu herní mapy (stromu uzlů).
 * Mapa je rozdělena do pater (tiers), kde každé patro obsahuje několik uzlů (nodes).
 * Stará se o logické větvení cest, generování typů místností a validaci pohybu hráče.
 */
public class GameMap {
    private List<List<MapNode>> tiers;
    private int numTiers;
    private int maxColumns;
    private Random random;
    private MapNode currentNode;

    public GameMap(int numTiers, int maxColumns) {
        this.numTiers = numTiers;
        this.maxColumns = maxColumns;
        this.tiers = new ArrayList<>();
        this.random = new Random();
        generateMap();
    }

    /**
     * Hlavní algoritmus pro generování mapy.
     * 1. Nejprve vytvoří uzly v jednotlivých patrech a určí jejich typ (nepřítel, obchod, boss...).
     * 2. Následně uzly mezi patry propojí hranami tak, aby cesty dávaly smysl a žádný uzel nezůstal izolovaný.
     */
    private void generateMap() {

        // --- FÁZE 1: VYTVOŘENÍ UZLŮ ---
        for (int t = 0; t < numTiers; t++) {
            List<MapNode> tier = new ArrayList<>();

            int numNodes;
            if (t == numTiers - 1) numNodes = 1; // Finální boss je vždy sám
            else if (t == 0) numNodes = 3;       // Na začátku hry jsou vždy 3 startovní body
            else numNodes = 3 + random.nextInt(2); // Střední patra mají 3 až 4 uzly

            for (int c = 0; c < numNodes; c++) {
                NodeType type = determineNodeType(t);
                MapNode node = new MapNode(t + "-" + c, type, t, c);
                tier.add(node);
            }
            tiers.add(tier);
        }

        // --- FÁZE 2: PROPOJOVÁNÍ CEST ---
        for (int t = 0; t < numTiers - 1; t++) {
            List<MapNode> currentTier = tiers.get(t);
            List<MapNode> nextTier = tiers.get(t + 1);

            for (int c = 0; c < currentTier.size(); c++) {
                MapNode node = currentTier.get(c);

                // Předposlední patro se celé sbíhá do jednoho finálního uzlu (Boss)
                if (t == numTiers - 2) {
                    node.addNextNode(nextTier.get(0));
                    continue;
                }

                // Výpočet poměrné pozice uzlu pro zachování přirozeného směru cesty nahoru
                float ratio = (float) c / Math.max(1, currentTier.size() - 1);
                int targetCenter = Math.round(ratio * (nextTier.size() - 1));

                // Přidání hlavní (přímé) cesty do dalšího patra
                node.addNextNode(nextTier.get(targetCenter));

                // Šance 40 % na vytvoření odbočky (větvení cesty) do sousedního sloupce
                if (random.nextDouble() < 0.4) {
                    int offset = random.nextBoolean() ? 1 : -1;
                    int extraCol = targetCenter + offset;
                    if (extraCol >= 0 && extraCol < nextTier.size()) {
                        node.addNextNode(nextTier.get(extraCol));
                    }
                }
            }

            // Kontrola osiřelých uzlů: Pokud do nějakého uzlu v dalším patře nevede žádná cesta,
            // zpětně k němu připojíme nejbližší uzel z aktuálního patra.
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

    /**
     * Určuje typ místnosti na základě patra a pravděpodobnosti.
     * První patro je vždy běžný boj, konec je Boss, před koncem je odpočinek a vybraná patra obsahují poklad.
     */
    private NodeType determineNodeType(int tier) {
        if (tier == 0) return NodeType.ENEMY;
        if (tier == numTiers - 1) return NodeType.BOSS;
        if (tier == numTiers - 2) return NodeType.REST; // Bezpečné ohniště před bossem
        if (tier == 4 || tier == 9) return NodeType.TREASURE; // Fixní patra s pokladem

        // Procentuální šance pro generování místností ve zbytku mapy
        int rand = random.nextInt(100);
        if (rand < 30) return NodeType.ENEMY;     // 30 % běžný nepřítel
        if (rand < 50) return NodeType.EVENT;     // 20 % náhodná událost
        if (rand < 65) return NodeType.SHOP;      // 15 % obchodník
        if (rand < 85) return NodeType.REST;      // 20 % odpočinek / kovárna
        return NodeType.ELITE;                    // 15 % silný elitní nepřítel
    }

    public List<List<MapNode>> getTiers() {
        return tiers;
    }

    public MapNode getCurrentNode() {
        return currentNode;
    }

    /**
     * Nastaví aktuální pozici hráče na mapě a označí uzel za navštívený.
     */
    public void setCurrentNode(MapNode node) {
        this.currentNode = node;
        if (node != null) node.setVisited(true);
    }

    /**
     * Ověřuje, zda hráč může na daný uzel kliknout/vstoupit.
     * Pokud hráč ještě neodstartoval, může zvolit jakýkoliv uzel v patře 0.
     * Pokud už na mapě stojí, uzel musí být v seznamu přímých následovníků aktuálního uzlu.
     */
    public boolean isNodeSelectable(MapNode node) {
        if (currentNode == null) {
            return node.getTier() == 0;
        }
        return currentNode.getNextNodes().contains(node);
    }
}