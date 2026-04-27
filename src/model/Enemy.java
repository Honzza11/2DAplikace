package model;


public class Enemy extends Entity {
    private String intentDescription;
    private int nextDamage;

    public Enemy(String name, int maxHp) {
        super(name, maxHp);
        this.intentDescription = "Unknown";
    }


    public void decideIntent() {
        nextDamage = 6;
        intentDescription = "Attack for " + nextDamage;
    }

    public String getIntentDescription() {
        return intentDescription;
    }

    public int getNextDamage() {
        return nextDamage;
    }
}
