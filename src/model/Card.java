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
    protected String heroClass;

    public Card(String name, int cost, String description, CardType type, String heroClass) {
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.type = type;
        this.effects = new HashMap<>();
        this.heroClass = heroClass != null ? heroClass : "Neutral";
    }

    public Card(Card other) {
        this.name = other.name;
        this.cost = other.cost;
        this.description = other.description;
        this.type = other.type;
        this.effects = new HashMap<>(other.effects);
        this.heroClass = other.heroClass;
    }


    public void addEffect(String key, int value) {
        effects.put(key, value);
    }

    public void play(Entity user, Entity target) {

        if (effects.containsKey("damage")) {
            int finalDamage = effects.get("damage");
            if (user instanceof AshWalker) {
                finalDamage += ((AshWalker) user).getDamageBonus();
            }
            if (user instanceof Player) {
                Player p = (Player) user;
                if (p.hasRelic("Akabeko") && !p.hasAttackedThisCombat()) {
                    finalDamage += 8;
                    p.setAttackedThisCombat(true);
                    System.out.println("♪ Relic: Akabeko deals +8 additional damage!");
                }
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
    public int getEnergyCost() { return cost; }
    public String getDescription() { return description; }
    public CardType getType() { return type; }
    public String getHeroClass() { return heroClass; }

    @Override
    public String toString() {
        return "[" + type + "] " + name + " (" + cost + " energy): " + description;
    }
}
