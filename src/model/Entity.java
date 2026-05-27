package model;


public abstract class Entity {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int block;
    protected int vulnerableTurns;
    protected int weakTurns;

    public Entity(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.block = 0;
        this.vulnerableTurns = 0;
        this.weakTurns = 0;
    }

    public void takeDamage(int amount) {
        if (vulnerableTurns > 0) {
            amount = (int)(amount * 1.5);
        }
        
        if (block >= amount) {
            block -= amount;
        } else {
            int remainingDamage = amount - block;
            block = 0;
            hp = Math.max(0, hp - remainingDamage);
        }
        System.out.println(name + " takes " + amount + " damage. Current HP: " + hp + ", Block: " + block);
    }

    public void addBlock(int amount) {
        block += amount;
        System.out.println(name + " gains " + amount + " block.");
    }

    public void resetBlock() {
        block = 0;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getHealth() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMaxHealth() { return maxHp; }
    public int getBlock() { return block; }
    public boolean isDead() { return hp <= 0; }
    
    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
        System.out.println(name + " heals for " + amount + " HP. Current HP: " + hp);
    }
    
    public void addVulnerable(int turns) { vulnerableTurns += turns; }
    public void addWeak(int turns) { weakTurns += turns; }
    
    public int getVulnerableTurns() { return vulnerableTurns; }
    public int getWeakTurns() { return weakTurns; }
    
    public void decrementStatuses() {
        if (vulnerableTurns > 0) vulnerableTurns--;
        if (weakTurns > 0) weakTurns--;
    }
    
    public void clearStatuses() {
        vulnerableTurns = 0;
        weakTurns = 0;
    }
}
