package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Třída představující hratelnou postavu "Bard".
 * Bard je specifický herní styl založený na hudebních tónech (Tones). Hraním karet generuje tóny,
 * které při poskládání tří tónů (akordu) automaticky spustí mocnou píseň (Song).
 * Dále disponuje mechanikou "Echo" pro opakované automatické sesílání karet na konci tahu.
 */
public class Bard extends Player {

    /** Výčet možných tónů, které může Bard vygenerovat. */
    public enum Tone {
        RED, BLUE, GREEN
    }

    /** Počet tónů potřebný k vytvoření akordu a spuštění písně. */
    private static final int CHORD_SIZE = 3;

    /** Seznam aktuálně nastřádaných tónů v tomto akordu. */
    private final List<Tone> currentTones;

    /** Typ naposledy zahrané karty. Slouží pro kontrolu rytmu (Rhythm). */
    private Card.CardType lastPlayedCardType = null;

    /** Seznam karet s efektem Echo, které se automaticky zopakují na konci tahu. */
    private final List<Card> pendingEchoCards;

    /**
     * Konstruktor pro vytvoření postavy Barda.
     * Inicializuje prázdné seznamy pro tóny a echo karty.
     * * @param name Název/jméno postavy.
     * @param maxHp Maximální životy.
     * @param maxEnergy Maximální energie na tah.
     */
    public Bard(String name, int maxHp, int maxEnergy) {
        super(name, maxHp, maxEnergy);
        this.currentTones = new ArrayList<>();
        this.pendingEchoCards = new ArrayList<>();
    }

    /**
     * Reakce na úspěšné zahrání karty hráčem.
     * Zpracovává efekt "echo", přidává tón odpovídající kartě a pokud
     * je naplněna kapacita akordu, spustí píseň a tóny vymaže.
     */
    @Override
    public void onCardPlayed(Card card, Entity target) {
        // Pokud má karta efekt echo, uloží se do fronty na konec tahu (může se uložit i vícekrát)
        if (card.hasEffect("echo")) {
            int echoCount = card.getEffectValue("echo");
            for (int i = 0; i < Math.max(1, echoCount); i++) {
                pendingEchoCards.add(card);
            }
        }

        // Určení a přidání tónu podle typu karty
        Tone tone = getToneForCard(card);
        currentTones.add(tone);

        // Uložení typu karty pro mechaniku rytmu
        lastPlayedCardType = card.getType();

        // Pokud máme 3 tóny, zahrajeme píseň a vyčistíme list
        if (currentTones.size() >= CHORD_SIZE) {
            playSong(target);
            currentTones.clear();
        }
    }

    /**
     * Pomocná metoda, která určuje tón na základě vlastností zahrané karty.
     * Útoky = ČERVENÝ, Schopnosti s blokem = MODRÝ, Ostatní (Power/Skill bez bloku) = ZELENÝ.
     */
    private Tone getToneForCard(Card card) {
        if (card.getType() == Card.CardType.ATTACK) return Tone.RED;
        if (card.getType() == Card.CardType.POWER) return Tone.GREEN;
        if (card.getType() == Card.CardType.SKILL) {
            return card.hasEffect("block") ? Tone.BLUE : Tone.GREEN;
        }
        return Tone.GREEN;
    }

    /**
     * Kontroluje, zda byl aktivován "Rhythm" (Rytmus).
     * Rytmus se aktivuje, pokud se typ aktuálně hrané karty liší od typu karty předchozí.
     */
    public boolean isRhythmTriggered(Card.CardType currentType) {
        return lastPlayedCardType != null && lastPlayedCardType != currentType;
    }

    /**
     * Vyhodnotí kombinaci 3 tónů a sehraje odpovídající píseň.
     * - 3x Červený = Destructive Anthem (Poškození nepřítele)
     * - 3x Modrý = Serenity Lullaby (Získání bloku + oslabení nepřítele)
     * - Jakákoliv jiná kombinace = Resonant Echo (Zisk energie + líznutí karty)
     */
    private void playSong(Entity target) {
        if (!(target instanceof Enemy)) {
            return;
        }

        Enemy enemy = (Enemy) target;

        boolean allRed = currentTones.size() == 3 && currentTones.get(0) == Tone.RED && currentTones.get(1) == Tone.RED && currentTones.get(2) == Tone.RED;
        boolean allBlue = currentTones.size() == 3 && currentTones.get(0) == Tone.BLUE && currentTones.get(1) == Tone.BLUE && currentTones.get(2) == Tone.BLUE;

        // PÍSEŇ 1: Útočná (3x Červená)
        if (allRed) {
            int songDamage = 7;
            enemy.takeDamage(songDamage);
            System.out.println("♪ SONG: Destructive Anthem! (" + songDamage + " damage)");
            return;
        }

        // PÍSEŇ 2: Obranná (3x Modrá)
        if (allBlue) {
            int songBlock = 4;
            addBlock(songBlock);
            enemy.addWeak(1); // Přidá nepříteli status Weak (oslabení)
            System.out.println("♪ SONG: Serenity Lullaby! (" + songBlock + " block, Weakness 1)");
            return;
        }

        // PÍSEŇ 3: Podpůrná / Kombinovaná (Směs tónů nebo Zelené)
        int energyGain = 1;
        int drawAmount = 1;
        setEnergy(getEnergy() + energyGain);
        drawCards(drawAmount);
        System.out.println("♪ SONG: Resonant Echo! (+" + energyGain + " energy, +" + drawAmount + " cards)");
    }

    /**
     * Spustí (zopakuje) všechny karty, které byly během tahu uloženy do fronty jako "Echo".
     * Tato metoda se typicky volá na konci tahu nepřítele / začátku nového tahu.
     */
    public void playEchoCards(Entity target) {
        if (pendingEchoCards.isEmpty()) return;
        List<Card> toPlay = new ArrayList<>(pendingEchoCards);
        pendingEchoCards.clear(); // Vyčistíme seznam před samotným spuštěním, aby nedošlo k zacyklení

        for (Card c : toPlay) {
            c.play(this, target);
        }
    }

    /**
     * Vrátí seznam aktuálně nastřádaných tónů pro zobrazení v UI.
     */
    public List<Tone> getCurrentTones() {
        return currentTones;
    }

    /**
     * Kompletně resetuje stav Barda (tóny, echo i rytmus) na výchozí hodnoty.
     * Volá se před začátkem nového souboje.
     */
    public void resetForCombat() {
        currentTones.clear();
        pendingEchoCards.clear();
        lastPlayedCardType = null;
    }
}