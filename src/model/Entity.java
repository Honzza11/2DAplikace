package model;


public abstract class Entity {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int block;

    public Entity(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.block = 0;
    }

    public void takeDamage(int amount) {
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
}
