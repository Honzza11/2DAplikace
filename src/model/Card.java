package model;


public abstract class Card {
    public enum CardType {
        ATTACK, SKILL, POWER
    }

    protected String name;
    protected int cost;
    protected String description;
    protected CardType type;

    public Card(String name, int cost, String description, CardType type) {
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.type = type;
    }

    public abstract void play(Entity user, Entity target);

    public String getName() { return name; }
    public int getCost() { return cost; }
    public String getDescription() { return description; }
    public CardType getType() { return type; }

    @Override
    public String toString() {
        return "[" + type + "] " + name + " (" + cost + " energy): " + description;
    }
}
