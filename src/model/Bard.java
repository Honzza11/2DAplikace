package model;

import java.util.ArrayList;
import java.util.List;

public class Bard extends Player {
    public enum Tone {
        RED, BLUE, GREEN
    }

    private static final int CHORD_SIZE = 3;

    private final List<Tone> currentTones;
    private Card.CardType lastPlayedCardType = null;


    private final List<Card> pendingEchoCards;

    public Bard(String name, int maxHp, int maxEnergy) {
        super(name, maxHp, maxEnergy);
        this.currentTones = new ArrayList<>();
        this.pendingEchoCards = new ArrayList<>();
    }

    @Override
    public void onCardPlayed(Card card, Entity target) {

        if (card.hasEffect("echo")) {
            int echoCount = card.getEffectValue("echo");
            for (int i = 0; i < Math.max(1, echoCount); i++) {
                pendingEchoCards.add(card);
            }
        }

        Tone tone = getToneForCard(card);
        currentTones.add(tone);


        lastPlayedCardType = card.getType();

        if (currentTones.size() >= CHORD_SIZE) {
            playSong(target);
            currentTones.clear();
        }
    }

    private Tone getToneForCard(Card card) {

        if (card.getType() == Card.CardType.ATTACK) return Tone.RED;
        if (card.getType() == Card.CardType.POWER) return Tone.GREEN;
        if (card.getType() == Card.CardType.SKILL) {
            return card.hasEffect("block") ? Tone.BLUE : Tone.GREEN;
        }
        return Tone.GREEN;
    }

    public boolean isRhythmTriggered(Card.CardType currentType) {
        return lastPlayedCardType != null && lastPlayedCardType != currentType;
    }

    private void playSong(Entity target) {
        if (!(target instanceof Enemy)) {
            return;
        }

        Enemy enemy = (Enemy) target;

        boolean allRed = currentTones.size() == 3 && currentTones.get(0) == Tone.RED && currentTones.get(1) == Tone.RED && currentTones.get(2) == Tone.RED;
        boolean allBlue = currentTones.size() == 3 && currentTones.get(0) == Tone.BLUE && currentTones.get(1) == Tone.BLUE && currentTones.get(2) == Tone.BLUE;

        if (allRed) {

            int songDamage = 14;
            enemy.takeDamage(songDamage);
            System.out.println("♪ SONG: Destructive Anthem! (" + songDamage + " damage)");
            return;
        }

        if (allBlue) {

            int songBlock = 12;
            addBlock(songBlock);
            enemy.applyWeakness(1);
            System.out.println("♪ SONG: Serenity Lullaby! (" + songBlock + " block, Weakness 1)");
            return;
        }

        int energyGain = 1;
        int drawAmount = 1;
        setEnergy(getEnergy() + energyGain);
        drawCards(drawAmount);
        System.out.println("♪ SONG: Resonant Echo! (+" + energyGain + " energy, +" + drawAmount + " cards)");
    }

    public void playEchoCards(Entity target) {
        if (pendingEchoCards.isEmpty()) return;
        List<Card> toPlay = new ArrayList<>(pendingEchoCards);
        pendingEchoCards.clear();

        for (Card c : toPlay) {

            c.play(this, target);
        }
    }

    public List<Tone> getCurrentTones() {
        return currentTones;
    }

    public void resetForCombat() {
        currentTones.clear();
        pendingEchoCards.clear();
        lastPlayedCardType = null;
    }
}
