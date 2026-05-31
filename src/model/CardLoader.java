package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Pomocná třída zajišťující načítání a parsování karet ze souboru (zjednodušený JSON parser).
 * Přečte textový soubor, najde objekty vymezené složenými závorkami {} a vytvoří z nich instance třídy Card.
 */
public class CardLoader {

    /**
     * Načte textový soubor, odstraní přebytečné bílé znaky, vyhledá jednotlivé
     * bloky karet vymezené závorkami {} a předá je k vyhodnocení.
     * * @param filePath Cesta k souboru s daty karet.
     * @return Seznam úspěšně načtených karet.
     */
    public static List<Card> loadCards(String filePath) {
        List<Card> cards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line.trim());
            }

            String json = content.toString();
            int index = 0;
            // Cyklus vyhledává dvojice znaků '{' a '}' reprezentující jednotlivé karty
            while ((index = json.indexOf("{", index)) != -1) {
                int end = json.indexOf("}", index);
                if (end == -1) break;

                String data = json.substring(index + 1, end);
                parseCard(data, cards);
                index = end + 1;
            }
        } catch (Exception e) {
            System.err.println("Error loading cards from " + filePath + ": " + e.getMessage());
        }
        return cards;
    }

    /**
     * Rozebere textový řetězec jedné karty, vytáhne základní parametry a
     * naplní její mapy základních a vylepšených (upgrade) efektů.
     */
    private static void parseCard(String data, List<Card> cards) {
        String name = getField(data, "name");
        int cost = getIntField(data, "cost");
        String desc = getField(data, "description");
        String typeStr = getField(data, "type");
        String heroClass = getField(data, "heroClass");

        if (name == null || typeStr == null) return;

        Card card = new Card(name, cost, desc, Card.CardType.valueOf(typeStr.toUpperCase()), heroClass);

        // Mapování standardních efektů karty
        addEffectIfPresent(data, "damage", card);
        addEffectIfPresent(data, "block", card);
        addEffectIfPresent(data, "heat_gain", card);
        addEffectIfPresent(data, "apply_vulnerable", card);
        addEffectIfPresent(data, "apply_weak", card);
        addEffectIfPresent(data, "heat_loss", card);
        addEffectIfPresent(data, "energy_gain", card);
        addEffectIfPresent(data, "draw", card);
        addEffectIfPresent(data, "echo", card);
        addEffectIfPresent(data, "heal", card);
        addEffectIfPresent(data, "consume_heat_for_block", card);
        addEffectIfPresent(data, "heat_bonus_damage", card);
        addEffectIfPresent(data, "condition_heat_max", card);
        addEffectIfPresent(data, "condition_heat_min", card);
        addEffectIfPresent(data, "condition_heat", card);
        addEffectIfPresent(data, "condition_bonus_damage", card);
        addEffectIfPresent(data, "multi_attack", card);

        // Mapování bonusových hodnot pro případný upgrade karty
        addUpgradeEffectIfPresent(data, "damage", card);
        addUpgradeEffectIfPresent(data, "block", card);
        addUpgradeEffectIfPresent(data, "heat_gain", card);
        addUpgradeEffectIfPresent(data, "heat_loss", card);
        addUpgradeEffectIfPresent(data, "echo", card);
        addUpgradeEffectIfPresent(data, "energy_gain", card);
        addUpgradeEffectIfPresent(data, "draw", card);

        // Načtení specifických textů a změn cen po upgradu
        String upDesc = getField(data, "upgrade_description");
        if (upDesc != null) card.setUpgradeDescription(upDesc);

        String upCost = getField(data, "upgrade_cost");
        if (upCost != null) {
            try {
                card.setUpgradeCostOverride(Integer.parseInt(upCost));
            } catch (NumberFormatException ignored) {}
        }

        cards.add(card);
    }

    /**
     * Pokusí se najít zadaný klíč efektu a pokud v datech existuje,
     * převede jeho hodnotu na číslo a přidá ji kartě do standardních efektů.
     */
    private static void addEffectIfPresent(String data, String field, Card card) {
        String val = getField(data, field);
        if (val != null) {
            try {
                card.addEffect(field, Integer.parseInt(val));
            } catch (NumberFormatException ignored) {}
        }
    }

    /**
     * Pokusí se najít hodnotu pro upgrade daného efektu (hledá klíč s prefixem "upgrade_")
     * a uloží ji do seznamu vylepšení karty.
     */
    private static void addUpgradeEffectIfPresent(String data, String field, Card card) {
        String val = getField(data, "upgrade_" + field);
        if (val != null) {
            try {
                card.addUpgradeEffect(field, Integer.parseInt(val));
            } catch (NumberFormatException ignored) {}
        }
    }

    /**
     * Hrubý textový parser, který v JSON řetězci najde klíč pole,
     * odřízne dvojtečku, vyseparuje hodnotu po nejbližší čárku a očistí ji od uvozovek.
     */
    private static String getField(String data, String fieldName) {
        String key = "\"" + fieldName + "\"";
        int keyIndex = data.indexOf(key);
        if (keyIndex == -1) return null;

        int colonIndex = data.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;

        int start = colonIndex + 1;
        int end = data.indexOf(",", start);
        if (end == -1) end = data.length();

        String val = data.substring(start, end).trim();
        if (val.startsWith("\"") && val.endsWith("\"")) {
            val = val.substring(1, val.length() - 1);
        }
        return val;
    }

    private static int getIntField(String data, String fieldName) {
        String val = getField(data, fieldName);
        return (val != null) ? Integer.parseInt(val) : 0;
    }
}