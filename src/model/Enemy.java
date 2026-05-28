package model;

import java.util.Random;

public class Enemy extends Entity {
    public enum IntentType {
        ATTACK, BLOCK, DEBUFF
    }

    private IntentType currentIntent;
    private int intentValue;
    private String intentDescription;

    public Enemy(String name, int maxHp) {
        super(name, maxHp);
        decideIntent();
    }

    public void decideIntent() {
        Random rand = new Random();
        int roll = rand.nextInt(100);

        if (roll < 60) {
            currentIntent = IntentType.ATTACK;
            intentValue = 6 + rand.nextInt(5);
            intentDescription = "Attack for " + getCalculatedDamage(intentValue);
        } else if (roll < 85) {
            currentIntent = IntentType.BLOCK;
            intentValue = 5 + rand.nextInt(6);
            intentDescription = "Defend for " + intentValue;
        } else {
            currentIntent = IntentType.DEBUFF;
            intentValue = 1 + rand.nextInt(2);
            intentDescription = roll % 2 == 0 ? "Apply Vulnerable (" + intentValue + ")" : "Apply Weak (" + intentValue + ")";
        }
    }

    private int getCalculatedDamage(int baseDamage) {
        if (getWeakTurns() > 0) {
            return (int)(baseDamage * 0.75);
        }
        return baseDamage;
    }

    public void takeTurn(Player player) {

        this.resetBlock();

        System.out.println(name + " performs: " + intentDescription);

        if (currentIntent == IntentType.ATTACK) {
            player.takeDamage(getCalculatedDamage(intentValue));
        } else if (currentIntent == IntentType.BLOCK) {
            this.addBlock(intentValue);
        } else if (currentIntent == IntentType.DEBUFF) {
            // 🌟 TRIK: Přidáme o 1 kolo navíc (+ 1), protože víme,
            // že herní smyčka mu hned na startu jeho tahu jedno kolo odečte.
            // Tím pádem mu do nového kola zbyde přesně ta správná hodnota!
            if (intentDescription.contains("Vulnerable")) {
                player.addVulnerable(intentValue + 1);
            } else {
                player.addWeak(intentValue + 1);
            }
        }

        decrementStatuses();
        decideIntent();
    }

    public IntentType getCurrentIntent() { return currentIntent; }
    public String getIntentDescription() { return intentDescription; }
    public int getIntentValue() { return intentValue; }

    public void applyWeakness(int turns) {
        addWeak(turns);
    }

    public boolean isWeakened() {
        return getWeakTurns() > 0;
    }
}