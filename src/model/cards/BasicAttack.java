package model.cards;

import model.Card;
import model.Entity;

public class BasicAttack extends Card {
    private int damage;

    public BasicAttack() {
        super("Strike", 1, "Deal 6 damage.", CardType.ATTACK);
        this.damage = 6;
    }

    @Override
    public void play(Entity user, Entity target) {
        if (target != null) {
            target.takeDamage(damage);
        }
    }
}
