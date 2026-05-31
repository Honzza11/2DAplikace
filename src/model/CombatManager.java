package model;

/**
 * Třída řídící průběh a logiku souboje mezi hráčem a nepřítelem.
 * Střídá tahy, spouští efekty relikvií na začátku souboje a předává
 * řízení specifickým mechanikám jednotlivých hrdinů (např. přehřívání AshWalkera).
 */
public class CombatManager {
    private Player player;
    private Enemy enemy;
    private int turnCount;
    private boolean isPlayerTurn;

    public CombatManager(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.turnCount = 1;
        this.isPlayerTurn = true;
    }

    /**
     * Inicializuje souboj. Resetuje statusy hráče a spouští efekty relikvií,
     * které se mají aktivovat hned na začátku střetnutí (Anchor, Lantern, Bag of Preparation).
     */
    public void startCombat() {
        System.out.println("\n=== COMBAT STARTED vs " + enemy.getName() + " ===");
        player.setAttackedThisCombat(false);
        player.clearStatuses();
        player.startTurn();

        // Kontrola a vyhodnocení relikvií na začátku souboje
        for (Relic r : player.getRelics()) {
            if (r.getName().equalsIgnoreCase("Anchor")) {
                player.addBlock(10);
            }
            if (r.getName().equalsIgnoreCase("Lantern")) {
                player.setEnergy(player.getEnergy() + 1);
            }
            if (r.getName().equalsIgnoreCase("Bag of Preparation")) {
                player.drawCards(2);
            }
        }
    }

    /**
     * Ukončí tah hráče. Zahodí zbývající karty z ruky do odhazovacího balíčku,
     * spustí specifickou end-turn logiku (např. kontrolu přehřátí u AshWalkera)
     * a předá řízení nepříteli.
     */
    public void endPlayerTurn() {
        System.out.println("\n--- Player ends turn ---");
        player.discardHand();

        // Kontrola specifické třídy hráče pro ukončení tahu
        if (player instanceof AshWalker) {
            ((AshWalker) player).endTurn();
        }

        isPlayerTurn = false;
        handleEnemyTurn();
    }

    /**
     * Provede tah nepřítele, pokud je naživu. Resetuje nepříteli štíty (Block),
     * provede jeho akce vůči hráči a následně inkrementuje počítadlo kol
     * a připraví nový tah pro hráče (doplnění energie, líznutí karet).
     */
    private void handleEnemyTurn() {
        if (enemy.isDead()) return;

        System.out.println("\n--- Enemy turn (" + enemy.getName() + ") ---");
        enemy.resetBlock();
        enemy.takeTurn(player);
        // Přechod zpět na tah hráče
        turnCount++;
        isPlayerTurn = true;
        player.startTurn();
        System.out.println("\n=== Turn " + turnCount + " (Player) ===");
    }

    public boolean isPlayerTurn() { return isPlayerTurn; }
    public Player getPlayer() { return player; }
    public Enemy getEnemy() { return enemy; }
}