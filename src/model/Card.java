package model;


public abstract class Card {
    protected String name;
    protected int cost;
    protected String description;

    public Card(String name, int cost, String description) {
        this.name = name;
        this.cost = cost;
        this.description = description;
    }


    public abstract void play(Entity user, Entity target);

    public String getName() { return name; }
    public int getCost() { return cost; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return name + " (" + cost + " energy): " + description;
    }
}
