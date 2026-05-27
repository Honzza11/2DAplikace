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
        int plays = effects.getOrDefault("multi_attack", 1);
        for (int i = 0; i < plays; i++) {
            resolveSinglePlay(user, target);
        }
    }

    private void resolveSinglePlay(Entity user, Entity target) {
        boolean conditionMet = true;
        if (effects.containsKey("condition_heat_max") && user instanceof AshWalker) {
            if (((AshWalker) user).getHeat() >= effects.get("condition_heat_max")) conditionMet = false;
        }
        if (effects.containsKey("condition_heat_min") && user instanceof AshWalker) {
            if (((AshWalker) user).getHeat() < effects.get("condition_heat_min")) conditionMet = false;
        }
        if (effects.containsKey("condition_heat") && user instanceof AshWalker) {
            if (((AshWalker) user).getHeat() != effects.get("condition_heat")) conditionMet = false;
        }

        if (effects.containsKey("heal") && conditionMet) {
            user.heal(effects.get("heal"));
        }

        if (effects.containsKey("consume_heat_for_block") && user instanceof AshWalker) {
            AshWalker ash = (AshWalker) user;
            int heat = ash.getHeat();
            if (heat > 0) {
                user.addBlock(heat);
                ash.resetHeat();
            }
        }

        if (effects.containsKey("damage")) {
            int finalDamage = effects.get("damage");
            if (!conditionMet && effects.containsKey("condition_heat")) {
                // If condition failed, absolute zero falls back to 6. Let's do a hardcode fallback or assume it just doesn't get bonus.
                // Wait, "V opačném případě způsobí jen 6 DMG." 
                // Let's just handle it via conditionMet. If condition failed and we have base damage, we can deal a smaller damage?
                // For simplicity, if condition_heat fails, we'll just deal the base damage. If it succeeds, we deal base + something?
                // Actually, let's just make Absolute Zero have base damage 6, and if condition meets, it adds 12 damage. We need "conditional_bonus_damage".
                // I will add "condition_bonus_damage".
            }
            if (conditionMet && effects.containsKey("condition_bonus_damage")) {
                finalDamage += effects.get("condition_bonus_damage");
            }

            if (user instanceof AshWalker) {
                finalDamage += ((AshWalker) user).getDamageBonus();
                if (effects.containsKey("heat_bonus_damage")) {
                    finalDamage += ((AshWalker) user).getHeat();
                }
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
            if (target instanceof Enemy && target.getVulnerableTurns() > 0) {
                // Already handled in takeDamage! Wait, the original code had Math.round(finalDamage * 1.5).
                // I should remove that from Card.java since it's in Entity.takeDamage now!
            }
            if (user.getWeakTurns() > 0) {
                finalDamage = (int)(finalDamage * 0.75);
            }

            if (target != null) {
                target.takeDamage(finalDamage);
            }
        }

        if (target != null && conditionMet) {
            if (effects.containsKey("apply_vulnerable")) {
                target.addVulnerable(effects.get("apply_vulnerable"));
            }
            if (effects.containsKey("apply_weak")) {
                target.addWeak(effects.get("apply_weak"));
            }
        }

        if (effects.containsKey("block") && conditionMet) {
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

        if (effects.containsKey("energy_gain") && user instanceof Player && conditionMet) {
            Player p = (Player) user;
            p.setEnergy(p.getEnergy() + effects.get("energy_gain"));
        }

        if (effects.containsKey("draw") && user instanceof Player && conditionMet) {
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
