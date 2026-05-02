package model.cards;

import model.Card;
import model.Entity;

public class BasicDefend extends Card {
    private int block;

    public BasicDefend() {
        super("Defend", 1, "Gain 5 Block.", CardType.SKILL);
        this.block = 5;
    }

    @Override
    public void play(Entity user, Entity target) {
        user.addBlock(block);
    }
}
