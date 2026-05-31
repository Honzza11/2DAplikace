package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Třída reprezentující lidského hráče.
 * Rozšiřuje základní entitu a spravuje kompletní stav karetních balíčků,
 * aktuální energii, finance (zlato) a vlastněné relikvie. Obsahuje také
 * klíčovou herní logiku pro lízání, zahazování a hraní karet.
 */
public class Player extends Entity {
    private int energy;
    private int maxEnergy;

    // Karetní zóny hráče
    private List<Card> deck;          // Dobírací balíček (Draw pile)
    private List<Card> hand;          // Karty na ruce (Hand)
    private List<Card> discardPile;    // Odhazovací balíček (Discard pile)
    private List<Relic> relics;

    private boolean attackedThisCombat;
    private int gold;

    public Player(String name, int maxHp, int maxEnergy) {
        super(name, maxHp);
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.deck = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.relics = new ArrayList<>();
        this.attackedThisCombat = false;
        this.gold = 99; // Hráč začíná s drobným kapitálem
    }

    /**
     * Spustí nový tah hráče. Sníží trvání negativních statusů,
     * plně obnoví energii na maximum, vyresetuje zbývající štíty a lízne 5 karet.
     */
    public void startTurn() {
        decrementStatuses();

        energy = maxEnergy;
        resetBlock();
        drawCards(5);
    }

    @Override
    public void clearStatuses() {
        super.clearStatuses();
    }

    /**
     * Přesune zadaný počet karet z dobíracího balíčku do ruky.
     * Pokud je dobírací balíček prázdný, automaticky přemíchá odhazovací balíček zpět.
     * * @param amount Počet karet k líznutí.
     */
    public void drawCards(int amount) {
        for (int i = 0; i < amount; i++) {
            if (deck.isEmpty()) {
                if (discardPile.isEmpty()) break; // Už není co lízat
                shuffleDiscardIntoDeck();
            }
            hand.add(deck.remove(0));
        }
    }

    /**
     * Přemíchá odhazovací balíček (discardPile) do dobíracího (deck) a náhodně jej zamíchá.
     */
    public void shuffleDiscardIntoDeck() {
        System.out.println("Deck empty. Shuffling discard pile back into deck...");
        deck.addAll(discardPile);
        discardPile.clear();
        Collections.shuffle(deck);
    }

    /**
     * Přesune všechny zbývající karty z ruky do odhazovacího balíčku (volá se na konci tahu).
     */
    public void discardHand() {
        discardPile.addAll(hand);
        hand.clear();
    }

    /**
     * Pokusí se zahrát vybranou kartu na zadaný cíl.
     * Ověří, zda má hráč dostatek energie a karta se nachází na ruce.
     * Pokud ano, odečte energii, kartu vyhodnotí, oznámí herním podtřídám (např. Bardovi)
     * její zahrání a přesune ji do odhazovacího balíčku.
     * * @return true, pokud byla karta úspěšně zahrána.
     */
    public boolean playCard(Card card, Entity target) {
        if (canAfford(card) && hand.contains(card)) {
            useEnergy(card.getCost());
            hand.remove(card);

            System.out.println(name + " plays " + card.getName());
            card.play(this, target);
            onCardPlayed(card, target); // Hák (hook) pro specifické mechaniky podtříd

            discardPile.add(card);
            return true;
        }
        System.out.println("Cannot play " + card.getName() + " (Not enough energy or not in hand)");
        return false;
    }

    /**
     * Prázdná metoda sloužící jako hook (přepsatelná metoda) pro potomky,
     * např. pro generování tónů u třídy Bard.
     */
    public void onCardPlayed(Card card, Entity target) {}

    public boolean canAfford(Card card) { return energy >= card.getCost(); }
    public void useEnergy(int amount) { energy -= amount; }

    /**
     * Nastaví hráči nový balíček (např. na začátku souboje) a náhodně ho zamíchá.
     */
    public void setDeck(List<Card> newDeck) {
        this.deck = new ArrayList<>(newDeck);
        Collections.shuffle(this.deck);
    }

    public void shuffleDeck() { Collections.shuffle(deck); }
    public void setEnergy(int energy) { this.energy = energy; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public List<Card> getDeck() { return deck; }
    public List<Card> getHand() { return hand; }
    public List<Card> getDiscardPile() { return discardPile; }
    public List<Relic> getRelics() { return relics; }

    /**
     * Přidá hráči relikvii do inventáře. Pokud se jedná o relikvii "Strawberry",
     * permanentně zvýší hráči maximální a aktuální životy o 10.
     */
    public void addRelic(Relic relic) {
        relics.add(relic);
        if (relic.getName().equalsIgnoreCase("Strawberry")) {
            this.maxHp += 10;
            this.hp = Math.min(this.maxHp, this.hp + 10);
        }
    }

    /**
     * Zjistí, zda hráč vlastní relikvii s daným názvem.
     */
    public boolean hasRelic(String relicName) {
        for (Relic r : relics) {
            if (r.getName().equalsIgnoreCase(relicName)) return true;
        }
        return false;
    }

    public boolean hasAttackedThisCombat() { return attackedThisCombat; }
    public void setAttackedThisCombat(boolean attacked) { this.attackedThisCombat = attacked; }
    public int getGold() { return gold; }
    public void addGold(int amount) { this.gold += amount; }
    public void removeGold(int amount) { this.gold = Math.max(0, this.gold - amount); }
}