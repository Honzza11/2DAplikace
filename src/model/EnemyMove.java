package model;

/**
 * Reprezentuje konkrétní útok, obranu nebo debuff, který může nepřítel v boji provést.
 * Třída slouží jako datový model pro načítání vlastností tahů (např. z JSONu),
 * které pak AI nepřítele vyhodnocuje v metodě decideIntent().
 */
public class EnemyMove {

    /** Typ akce – např. "ATTACK", "BLOCK", "WEAK" nebo "VULNERABLE". */
    private String type;

    /** Váha šance na výběr tohoto tahu. Vyšší číslo znamená vyšší pravděpodobnost zahraní. */
    private int chanceWeight;

    /** Minimální základní hodnota účinku (např. minimální poškození nebo blok). */
    private int minVal;

    /** Maximální základní hodnota účinku (např. maximální poškození nebo blok). */
    private int maxVal;

    /**
     * Bezparametrický konstruktor nezbytný pro správnou deserializaci knihovnou GSON.
     */
    public EnemyMove() {}

    public String getType() { return type; }
    public int getChanceWeight() { return chanceWeight; }
    public int getMinVal() { return minVal; }
    public int getMaxVal() { return maxVal; }
}