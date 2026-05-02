package model.cards;

import model.AshWalker;
import model.Card;
import model.Entity;

public class CinderStrike extends Card {
    private int baseDamage;

    public CinderStrike() {
        super("Cinder Strike", 1, "Deal 8 damage. Gain 2 Heat.", CardType.ATTACK);
        this.baseDamage = 8;
    }

    @Override
    public void play(Entity user, Entity target) {
        int finalDamage = baseDamage;
        
        if (user instanceof AshWalker) {
            AshWalker walker = (AshWalker) user;
            finalDamage += walker.getDamageBonus();
            walker.addHeat(2);
        }

        if (target != null) {
            target.takeDamage(finalDamage);
        }
    }
}
