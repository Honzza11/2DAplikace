package model;


import java.util.HashMap;
import java.util.Map;

public class Card {
    public enum CardType {
        ATTACK, SKILL, POWER
    }

    protected String name;
    protected int cost;
    protected String description;
    protected CardType type;
    protected Map<String, Integer> effects;

    public Card(String name, int cost, String description, CardType type) {
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.type = type;
        this.effects = new HashMap<>();
    }

    public void addEffect(String key, int value) {
        effects.put(key, value);
    }

    public void play(Entity user, Entity target) {
        // Damage logic
        if (effects.containsKey("damage")) {
            int finalDamage = effects.get("damage");
            if (user instanceof AshWalker) {
                finalDamage += ((AshWalker) user).getDamageBonus();
            }
            if (target != null) {
                target.takeDamage(finalDamage);
            }
        }


        if (effects.containsKey("block")) {
            user.addBlock(effects.get("block"));
        }


        if (effects.containsKey("heat_gain") && user instanceof AshWalker) {
            ((AshWalker) user).addHeat(effects.get("heat_gain"));
        }
    }

    public String getName() { return name; }
    public int getCost() { return cost; }
    public String getDescription() { return description; }
    public CardType getType() { return type; }

    @Override
    public String toString() {
        return "[" + type + "] " + name + " (" + cost + " energy): " + description;
    }
}
