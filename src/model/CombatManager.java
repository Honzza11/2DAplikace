package model;

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

    public void startCombat() {
        System.out.println("\n=== COMBAT STARTED vs " + enemy.getName() + " ===");
        player.startTurn();
    }

    public void endPlayerTurn() {
        System.out.println("\n--- Player ends turn ---");
        player.discardHand();
        
        if (player instanceof AshWalker) {
            ((AshWalker) player).endTurn();
        }

        isPlayerTurn = false;
        handleEnemyTurn();
    }

    private void handleEnemyTurn() {
        if (enemy.isDead()) return;

        System.out.println("\n--- Enemy turn (" + enemy.getName() + ") ---");

        enemy.takeTurn(player);
        
        turnCount++;
        isPlayerTurn = true;
        player.startTurn();
        System.out.println("\n=== Turn " + turnCount + " (Player) ===");
    }

    public boolean isPlayerTurn() { return isPlayerTurn; }
    public Player getPlayer() { return player; }
    public Enemy getEnemy() { return enemy; }
}
