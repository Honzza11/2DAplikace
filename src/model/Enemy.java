package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Třída reprezentující nepřítele v boji.
 * Rozšiřuje obecnou entitu a přidává chování specifické pro UI a AI –
 * správu záměrů (Intents) a náhodný výběr útoků či obran na základě zadaných vah.
 */
public class Enemy extends Entity {

    /** Výčet typů záměrů, které se zobrazují hráči nad hlavou nepřítele. */
    public enum IntentType {
        ATTACK, BLOCK, DEBUFF
    }
    private String pool;
    private String id;
    private int minHp;

    private String imagePath;
    private IntentType currentIntent;
    private int intentValue;
    private String intentDescription;
    private String debuffType;

    /** Seznam všech dostupných akcí (útoky, debuffy, obrana), které nepřítel umí provést. */
    private List<EnemyMove> moves;


    public Enemy(String name, int hp) {
        super(name, hp);
        this.minHp = hp;
        this.maxHp = hp;
        this.moves = new java.util.ArrayList<>();
    }

    /**
     * Klonovací konstruktor (Template pattern).
     * Používá se pro vytvoření konkrétní instance nepřítele ze šablony načtené např. z JSONu,
     * přičemž mu náhodně vypočítá životy v rozmezí min a max HP.
     */
    public Enemy(Enemy template) {
        super(template.getName(), calculateHp(template.getMinHp(), template.getMaxHp()));
        this.id = template.getId();
        this.pool = template.getPool();
        this.moves = template.getMoves();
        this.imagePath = template.getImagePath();
        decideIntent(); // Hned při zrodu si vybere svůj první záměr
    }

    /**
     * Pomocná metoda pro výpočet náhodného počtu životů v daném rozsahu.
     */
    private static int calculateHp(int min, int max) {
        if (max <= min) return min;
        return min + new Random().nextInt((max - min) + 1);
    }

    /**
     * AI nepřítele: Vybere náhodný tah ze seznamu 'moves' na základě jejich vah (Chance Weight).
     * Následně spočítá náhodnou hodnotu (v rozsahu min a max daného tahu) a připraví
     * textový popis a typ záměru (Intent) pro zobrazení v uživatelském rozhraní.
     */
    public void decideIntent() {
        if (moves == null || moves.isEmpty()) return;

        Random rand = new Random();
        int totalWeight = 0;
        for (EnemyMove move : moves) {
            totalWeight += move.getChanceWeight();
        }

        // Ruletový výběr tahu podle vah
        int roll = rand.nextInt(totalWeight);
        int currentWeight = 0;
        EnemyMove selectedMove = moves.get(0);

        for (EnemyMove move : moves) {
            currentWeight += move.getChanceWeight();
            if (roll < currentWeight) {
                selectedMove = move;
                break;
            }
        }

        // Výpočet výsledné hodnoty akce (např. velikost poškození) v daném rozsahu tahu
        intentValue = selectedMove.getMinVal();
        if (selectedMove.getMaxVal() > selectedMove.getMinVal()) {
            intentValue += rand.nextInt((selectedMove.getMaxVal() - selectedMove.getMinVal()) + 1);
        }

        // Sestavení záměru pro zobrazení hráči
        switch (selectedMove.getType()) {
            case "ATTACK":
                currentIntent = IntentType.ATTACK;
                intentDescription = "Attack for " + getCalculatedDamage(intentValue);
                break;
            case "BLOCK":
                currentIntent = IntentType.BLOCK;
                intentDescription = "Defend for " + intentValue;
                break;
            case "VULNERABLE":
                currentIntent = IntentType.DEBUFF;
                debuffType = "Vulnerable";
                intentDescription = "Apply Vulnerable (" + intentValue + ")";
                break;
            case "WEAK":
                currentIntent = IntentType.DEBUFF;
                debuffType = "Weak";
                intentDescription = "Apply Weak (" + intentValue + ")";
                break;
        }
    }

    /**
     * Vrací upravené poškození nepřítele. Pokud je nepřítel oslaben (Weak),
     * jeho útok dává pouze 75 % základního poškození.
     */
    private int getCalculatedDamage(int baseDamage) {
        if (getWeakTurns() > 0) {
            return (int)(baseDamage * 0.75);
        }
        return baseDamage;
    }

    /**
     * Provede aktuálně naplánovaný záměr vůči hráči (útok, zisk bloku, nebo aplikaci debuffů).
     * Na konci tahu sníží trvání svých negativních statusů a vybere si nový záměr na další kolo.
     */
    public void takeTurn(Player player) {
        this.resetBlock();
        System.out.println(name + " performs: " + intentDescription);

        if (currentIntent == IntentType.ATTACK) {
            player.takeDamage(getCalculatedDamage(intentValue));
        } else if (currentIntent == IntentType.BLOCK) {
            this.addBlock(intentValue);
        } else if (currentIntent == IntentType.DEBUFF) {
            if ("Vulnerable".equals(debuffType)) {
                player.addVulnerable(intentValue + 1);
            } else if ("Weak".equals(debuffType)) {
                player.addWeak(intentValue + 1);
            }
        }

        // Správa statusů a příprava na další kolo
        decrementStatuses();
        decideIntent();
    }

    // Settery a gettery pro správnou serializaci/deserializaci (GSON) a UI...
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    public void setPool(String pool) { this.pool = pool; }
    public String getPool() { return pool; }

    public void setMinHp(int minHp) { this.minHp = minHp; }
    public int getMinHp() { return minHp; }

    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getMaxHp() { return getMaxHealth(); }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getImagePath() { return imagePath; }

    public List<EnemyMove> getMoves() { return moves; }
    public IntentType getCurrentIntent() { return currentIntent; }
    public String getIntentDescription() { return intentDescription; }
    public int getIntentValue() { return intentValue; }
    public boolean isWeakened() { return getWeakTurns() > 0; }
}