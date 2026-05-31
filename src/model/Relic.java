package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Třída reprezentující herní relikvii.
 * Relikvie jsou pasivní předměty, které hráči poskytují trvalé nebo jednorázové
 * bonusy v souboji (např. extra lízání karet, block na začátku boje, více energie).
 */
public class Relic {
    private String name;
    private String description;

    public Relic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    /**
     * Vybere náhodnou relikvii z celkového fondu dostupných relikvií.
     * Algoritmus aktivně filtruje relikvie, které už hráč vlastní,
     * aby nedošlo k duplikaci stejného předmětu.
     * * @param playerRelics Seznam relikvií, které už hráč aktuálně má v inventáři.
     * @return Nová náhodná relikvie, nebo null, pokud už hráč vlastní všechny dostupné relikvie.
     */
    public static Relic getRandomRelic(List<Relic> playerRelics) {
        List<Relic> allRelics = getAllAvailableRelics();
        List<Relic> pool = new ArrayList<>();

        // Kontrola duplicit: Přidá do výběru pouze ty relikvie, které hráč ještě nemá
        for (Relic r : allRelics) {
            boolean alreadyOwned = false;
            for (Relic pr : playerRelics) {
                if (pr.getName().equals(r.getName())) {
                    alreadyOwned = true;
                    break;
                }
            }
            if (!alreadyOwned) {
                pool.add(r);
            }
        }

        if (pool.isEmpty()) {
            return null;
        }

        // Výběr náhodného prvku z profiltrovaného fondu
        return pool.get(new Random().nextInt(pool.size()));
    }

    /**
     * Statická tovární metoda, která vrací kompletní seznam (databázi) všech
     * existujících relikvií implementovaných ve hře.
     */
    private static List<Relic> getAllAvailableRelics() {
        List<Relic> list = new ArrayList<>();
        list.add(new Relic("Strawberry", "Max HP increased by 10. Heal 10 HP."));
        list.add(new Relic("Anchor", "Start each combat with 10 Block."));
        list.add(new Relic("Lantern", "Start each combat with +1 Energy."));
        list.add(new Relic("Bag of Preparation", "Draw 2 additional cards on your first turn."));
        list.add(new Relic("Akabeko", "Your first attack in each combat deals 8 additional damage."));
        return list;
    }
}