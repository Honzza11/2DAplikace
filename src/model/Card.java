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
    
    protected boolean isUpgraded = false;
    protected Map<String, Integer> upgradeEffects;
    protected String upgradeDescription;
    protected Integer upgradeCostOverride;

    public Card(String name, int cost, String description, CardType type, String heroClass) {
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.type = type;
        this.effects = new HashMap<>();
        this.upgradeEffects = new HashMap<>();
        this.heroClass = heroClass != null ? heroClass : "Neutral";
    }

    public Card(Card other) {
        this.name = other.name;
        this.cost = other.cost;
        this.description = other.description;
        this.type = other.type;
        this.effects = new HashMap<>(other.effects);
        this.heroClass = other.heroClass;
        this.isUpgraded = other.isUpgraded;
        this.upgradeEffects = new HashMap<>(other.upgradeEffects);
        this.upgradeDescription = other.upgradeDescription;
        this.upgradeCostOverride = other.upgradeCostOverride;
    }


    public void addEffect(String key, int value) {
        effects.put(key, value);
    }
    
    public void addUpgradeEffect(String key, int value) {
        upgradeEffects.put(key, value);
    }

    public void setUpgradeDescription(String desc) {
        this.upgradeDescription = desc;
    }

    public void setUpgradeCostOverride(int cost) {
        this.upgradeCostOverride = cost;
    }

    public boolean hasEffect(String key) {
        return effects.containsKey(key);
    }

    public int getEffectValue(String key) {
        if (!effects.containsKey(key)) return 0;
        return effects.get(key);
    }

    public void play(Entity user, Entity target) {

        if (effects.containsKey("damage")) {
            int finalDamage = effects.get("damage");
            if (user instanceof AshWalker) {
                finalDamage += ((AshWalker) user).getDamageBonus();
            }
            if (user instanceof Bard) {
                Bard bard = (Bard) user;
                if (bard.isRhythmTriggered(this.type) && effects.containsKey("rhythm_bonus_damage")) {
                    finalDamage += effects.get("rhythm_bonus_damage");
                }
            }
            if (user instanceof Player) {
                Player p = (Player) user;
                if (p.hasRelic("Akabeko") && !p.hasAttackedThisCombat()) {
                    finalDamage += 8;
                    p.setAttackedThisCombat(true);
                    System.out.println("♪ Relic: Akabeko deals +8 additional damage!");
                }
            }
            if (target instanceof Enemy && ((Enemy) target).isWeakened()) {

                finalDamage = (int) Math.round(finalDamage * 1.5);
            }
            if (target != null) {
                target.takeDamage(finalDamage);
            }
        }



        if (effects.containsKey("block")) {
            int blockAmount = effects.get("block");
            if (user instanceof Bard) {
                Bard bard = (Bard) user;
                if (bard.isRhythmTriggered(this.type) && effects.containsKey("rhythm_bonus_block")) {
                    blockAmount += effects.get("rhythm_bonus_block");
                }
            }
            user.addBlock(blockAmount);
        }


        if (effects.containsKey("heat_gain") && user instanceof AshWalker) {
            ((AshWalker) user).addHeat(effects.get("heat_gain"));
        }

        if (effects.containsKey("heat_loss") && user instanceof AshWalker) {
            ((AshWalker) user).reduceHeat(effects.get("heat_loss"));
        }

        if (effects.containsKey("energy_gain") && user instanceof Player) {
            Player p = (Player) user;
            p.setEnergy(p.getEnergy() + effects.get("energy_gain"));
        }

        if (effects.containsKey("draw") && user instanceof Player) {
            Player p = (Player) user;
            p.drawCards(effects.get("draw"));
        }
    }

    public String getName() { return name; }
    public int getCost() { return cost; }
    public int getEnergyCost() { return cost; }
    public String getDescription() { return description; }
    public CardType getType() { return type; }
    public String getHeroClass() { return heroClass; }
    public boolean isUpgraded() { return isUpgraded; }
    
    public void upgrade() {
        if (!isUpgraded) {
            isUpgraded = true;
            name += "+";
            
            if (upgradeDescription != null) {
                description = upgradeDescription;
            }
            if (upgradeCostOverride != null) {
                cost = upgradeCostOverride;
            }
            
            for (Map.Entry<String, Integer> entry : upgradeEffects.entrySet()) {
                String key = entry.getKey();
                int upgradeAmount = entry.getValue();
                if (effects.containsKey(key)) {
                    effects.put(key, effects.get(key) + upgradeAmount);
                } else {
                    effects.put(key, upgradeAmount);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "[" + type + "] " + name + " (" + cost + " energy): " + description;
    }
}
