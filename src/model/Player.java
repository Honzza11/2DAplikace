package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Player extends Entity {
    private int energy;
    private int maxEnergy;
    
    private List<Card> deck;
    private List<Card> hand;
    private List<Card> discardPile;
    private List<Relic> relics;
    private boolean attackedThisCombat;


    public Player(String name, int maxHp, int maxEnergy) {
        super(name, maxHp);
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.deck = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.relics = new ArrayList<>();
        this.attackedThisCombat = false;
    }

    public void startTurn() {
        energy = maxEnergy;
        resetBlock();
        drawCards(5);
    }

    public void drawCards(int amount) {
        for (int i = 0; i < amount; i++) {
            if (deck.isEmpty()) {
                if (discardPile.isEmpty()) break;
                shuffleDiscardIntoDeck();
            }
            hand.add(deck.remove(0));
        }
    }

    public void shuffleDiscardIntoDeck() {
        System.out.println("Deck empty. Shuffling discard pile back into deck...");
        deck.addAll(discardPile);
        discardPile.clear();
        Collections.shuffle(deck);
    }

    public void discardHand() {
        discardPile.addAll(hand);
        hand.clear();
    }

    public boolean playCard(Card card, Entity target) {
        if (canAfford(card) && hand.contains(card)) {
            useEnergy(card.getCost());
            hand.remove(card);
            
            System.out.println(name + " plays " + card.getName());
            card.play(this, target);
            onCardPlayed(card);
            
            discardPile.add(card);
            return true;
        }
        System.out.println("Cannot play " + card.getName() + " (Not enough energy or not in hand)");
        return false;
    }

    public void onCardPlayed(Card card) {

    }

    public boolean canAfford(Card card) {
        return energy >= card.getCost();
    }

    public void useEnergy(int amount) {
        energy -= amount;
    }

    public void setDeck(List<Card> newDeck) {
        this.deck = new ArrayList<>(newDeck);
        Collections.shuffle(this.deck);
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public List<Card> getDeck() { return deck; }
    public List<Card> getHand() { return hand; }
    public List<Card> getDiscardPile() { return discardPile; }
    
    public List<Relic> getRelics() { return relics; }
    
    public void addRelic(Relic relic) {
        relics.add(relic);
        if (relic.getName().equalsIgnoreCase("Strawberry")) {
            this.maxHp += 10;
            this.hp = Math.min(this.maxHp, this.hp + 10);
        }
    }
    
    public boolean hasRelic(String relicName) {
        for (Relic r : relics) {
            if (r.getName().equalsIgnoreCase(relicName)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAttackedThisCombat() { return attackedThisCombat; }
    public void setAttackedThisCombat(boolean attacked) { this.attackedThisCombat = attacked; }
}

