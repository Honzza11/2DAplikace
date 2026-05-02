package model.cards;

import model.Card;
import model.Entity;

public class SoulMelody extends Card {
    private int block;

    public SoulMelody() {
        super("Soul Melody", 1, "Gain 7 Block. Adds a Skill note.", CardType.SKILL);
        this.block = 7;
    }

    @Override
    public void play(Entity user, Entity target) {
        user.addBlock(block);

    }
}
