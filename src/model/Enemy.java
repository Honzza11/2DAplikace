package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy extends Entity {
    public enum IntentType {
        ATTACK, BLOCK, DEBUFF
    }
    private String pool;
    private String id;
    private int minHp;

    private String imagePath;
    private IntentType currentIntent;
    private int intentValue;
    private String intentDescription;
    private String debuffType;

    private List<EnemyMove> moves;


    public Enemy(String name, int hp) {
        super(name, hp);
        this.minHp = hp;
        this.maxHp = hp;
        this.moves = new java.util.ArrayList<>();
    }


    public Enemy(Enemy template) {
        super(template.getName(), calculateHp(template.getMinHp(), template.getMaxHp()));
        this.id = template.getId();
        this.pool = template.getPool();
        this.moves = template.getMoves();
        this.imagePath = template.getImagePath();
        decideIntent();
    }

    private static int calculateHp(int min, int max) {
        if (max <= min) return min;
        return min + new Random().nextInt((max - min) + 1);
    }

    public void decideIntent() {
        if (moves == null || moves.isEmpty()) return;

        Random rand = new Random();
        int totalWeight = 0;
        for (EnemyMove move : moves) {
            totalWeight += move.getChanceWeight();
        }

        int roll = rand.nextInt(totalWeight);
        int currentWeight = 0;
        EnemyMove selectedMove = moves.get(0);

        for (EnemyMove move : moves) {
            currentWeight += move.getChanceWeight();
            if (roll < currentWeight) {
                selectedMove = move;
                break;
            }
        }

        intentValue = selectedMove.getMinVal();
        if (selectedMove.getMaxVal() > selectedMove.getMinVal()) {
            intentValue += rand.nextInt((selectedMove.getMaxVal() - selectedMove.getMinVal()) + 1);
        }

        switch (selectedMove.getType()) {
            case "ATTACK":
                currentIntent = IntentType.ATTACK;
                intentDescription = "Attack for " + getCalculatedDamage(intentValue);
                break;
            case "BLOCK":
                currentIntent = IntentType.BLOCK;
                intentDescription = "Defend for " + intentValue;
                break;
            case "VULNERABLE":
                currentIntent = IntentType.DEBUFF;
                debuffType = "Vulnerable";
                intentDescription = "Apply Vulnerable (" + intentValue + ")";
                break;
            case "WEAK":
                currentIntent = IntentType.DEBUFF;
                debuffType = "Weak";
                intentDescription = "Apply Weak (" + intentValue + ")";
                break;
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
            if ("Vulnerable".equals(debuffType)) {
                player.addVulnerable(intentValue + 1);
            } else if ("Weak".equals(debuffType)) {
                player.addWeak(intentValue + 1);
            }
        }

        decrementStatuses();
        decideIntent();
    }

    // 🌟 UPRAVENÉ SETTERY A GETTERY PRO GSON
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    public void setPool(String pool) { this.pool = pool; }
    public String getPool() { return pool; }

    public void setMinHp(int minHp) { this.minHp = minHp; }
    public int getMinHp() { return minHp; }


    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
    public int getMaxHp() {
        return getMaxHealth();
    }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getImagePath() { return imagePath; }

    public List<EnemyMove> getMoves() { return moves; }
    public IntentType getCurrentIntent() { return currentIntent; }
    public String getIntentDescription() { return intentDescription; }
    public int getIntentValue() { return intentValue; }
    public boolean isWeakened() { return getWeakTurns() > 0; }
}