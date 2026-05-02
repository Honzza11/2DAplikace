package model;

import java.util.ArrayList;
import java.util.List;


public class Player extends Entity {
    private int energy;
    private int maxEnergy;
    
    private List<Card> deck;
    private List<Card> hand;
    private List<Card> discardPile;

    public Player(String name, int maxHp, int maxEnergy) {
        super(name, maxHp);
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.deck = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.discardPile = new ArrayList<>();
    }

    public void startTurn() {
        energy = maxEnergy;
        resetBlock();
    }

    public void onCardPlayed(Card card) {
    }

    public boolean canAfford(Card card) {
        return energy >= card.getCost();
    }

    public void useEnergy(int amount) {
        energy -= amount;
    }

    public int getEnergy() { return energy; }
    public List<Card> getDeck() { return deck; }
    public List<Card> getHand() { return hand; }
    public List<Card> getDiscardPile() { return discardPile; }
}
