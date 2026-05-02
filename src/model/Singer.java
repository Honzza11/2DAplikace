package model;

import java.util.ArrayList;
import java.util.List;

public class Singer extends Player {
    private List<Card.CardType> currentChord;
    private static final int CHORD_SIZE = 3;

    public Singer(String name, int maxHp, int maxEnergy) {
        super(name, maxHp, maxEnergy);
        this.currentChord = new ArrayList<>();
    }

    @Override
    public void onCardPlayed(Card card) {
        currentChord.add(card.getType());
        System.out.println(name + " plays a note: " + card.getType());

        if (currentChord.size() >= CHORD_SIZE) {
            playSong();
            currentChord.clear();
        }
    }

    private void playSong() {
        int attacks = 0;
        int skills = 0;

        for (Card.CardType type : currentChord) {
            if (type == Card.CardType.ATTACK) attacks++;
            if (type == Card.CardType.SKILL) skills++;
        }

        if (attacks == 3) {
            System.out.println("♪ SONG: Destructive Anthem! (Heavy Damage to all)");
        } else if (skills == 3) {
            System.out.println("♪ SONG: Serenity Lullaby! (Massive Block)");
            addBlock(15);
        } else {
            System.out.println("♪ SONG: Inspiring Echo! (Gain 1 Energy)");
        }
    }

    public List<Card.CardType> getCurrentChord() {
        return currentChord;
    }
}
