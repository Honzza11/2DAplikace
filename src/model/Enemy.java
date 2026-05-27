package model;


public class Enemy extends Entity {
    private String intentDescription;
    private int nextDamage;
    private int weaknessTurns = 0;

    public Enemy(String name, int maxHp) {
        super(name, maxHp);
        this.intentDescription = "Unknown";
    }


    public void decideIntent() {
        nextDamage = 6;
        intentDescription = "Attack for " + nextDamage;
    }

    public void takeTurn(Player player) {
        System.out.println(name + " performs: " + intentDescription);
        player.takeDamage(nextDamage);
        decideIntent();

        if (weaknessTurns > 0) {
            weaknessTurns--;
        }
    }

    public String getIntentDescription() {
        return intentDescription;
    }

    public int getNextDamage() {
        return nextDamage;
    }

    public void applyWeakness(int turns) {
        weaknessTurns = Math.max(weaknessTurns, turns);
        System.out.println(name + " gains Weakness (" + weaknessTurns + " turns)");
    }

    public boolean isWeakened() {
        return weaknessTurns > 0;
    }
}
