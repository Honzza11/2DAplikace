package model;

/**
 * Třída představující hratelnou postavu "AshWalker" (specializace na práci s teplem).
 * AshWalker využívá mechaniku generování tepla (Heat), které mu zvyšuje poškození,
 * ale při překročení limitu riskuje zranění z přehřátí (Overheat).
 */
public class AshWalker extends Player {

    /** Aktuální úroveň tepla, kterou postava má. */
    private int heat;

    /** Hranice tepla, po jejímž překročení dochází k přehřátí. */
    private static final int OVERHEAT_THRESHOLD = 10;

    /** Množství poškození, které hráč utrží, pokud tah skončí s přehřátím. */
    private static final int OVERHEAT_DAMAGE = 5;

    /**
     * Konstruktor pro vytvoření postavy AshWalker.
     * Na začátku hry je úroveň tepla nastavena na 0.
     * * @param name Název/jméno postavy.
     * @param maxHp Maximální životy hráče.
     * @param maxEnergy Maximální energie na tah.
     */
    public AshWalker(String name, int maxHp, int maxEnergy) {
        super(name, maxHp, maxEnergy);
        this.heat = 0;
    }

    @Override
    public void startTurn() {
        super.startTurn();
    }

    /**
     * Logika prováděná na konci tahu hráče.
     * Kontroluje, zda úroveň tepla nepřekročila povolenou mez.
     * Pokud ano, postava obdrží poškození z přehřátí.
     */
    public void endTurn() {
        if (heat > OVERHEAT_THRESHOLD) {
            System.out.println(name + " is overheating! Taking damage.");
            takeDamage(OVERHEAT_DAMAGE);
        }
    }

    @Override
    public void onCardPlayed(Card card, Entity target) {
        // Spouští se při zahrání karty (aktuálně nevyužito)
    }

    /**
     * Zvýší aktuální úroveň tepla o zadanou hodnotu.
     * * @param amount Množství přidaného tepla.
     */
    public void addHeat(int amount) {
        this.heat += amount;
        System.out.println(name + " gains " + amount + " heat. Total: " + heat);
    }

    /**
     * Sníží aktuální úroveň tepla. Hodnota tepla nikdy neklesne pod 0.
     * * @param amount Množství odebraného tepla.
     */
    public void reduceHeat(int amount) {
        this.heat = Math.max(0, this.heat - amount);
        System.out.println(name + " cools down. Heat: " + heat);
    }

    /**
     * Vrátí aktuální množství tepla.
     * * @return Aktuální teplo.
     */
    public int getHeat() {
        return heat;
    }

    /**
     * Vypočítá bonus k poškození na základě aktuálního tepla.
     * Bonus činí +1 poškození za každé 2 body tepla (zaokrouhleno dolů).
     * * @return Bonusové poškození.
     */
    public int getDamageBonus() {
        return heat / 2;
    }

    /**
     * Resetuje úroveň tepla zpět na nulu (např. po skončení souboje).
     */
    public void resetHeat() {
        heat = 0;
    }
}