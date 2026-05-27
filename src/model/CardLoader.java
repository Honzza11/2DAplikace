package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CardLoader {
    

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

    private static void parseCard(String data, List<Card> cards) {
        String name = getField(data, "name");
        int cost = getIntField(data, "cost");
        String desc = getField(data, "description");
        String typeStr = getField(data, "type");
        String heroClass = getField(data, "heroClass");
        
        if (name == null || typeStr == null) return;

        Card card = new Card(name, cost, desc, Card.CardType.valueOf(typeStr.toUpperCase()), heroClass);
        

        addEffectIfPresent(data, "damage", card);
        addEffectIfPresent(data, "block", card);
        addEffectIfPresent(data, "heat_gain", card);
        addEffectIfPresent(data, "apply_vulnerable", card);
        addEffectIfPresent(data, "apply_weak", card);
        addEffectIfPresent(data, "heat_loss", card);
        addEffectIfPresent(data, "rhythm_bonus_damage", card);
        addEffectIfPresent(data, "rhythm_bonus_block", card);
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

        addUpgradeEffectIfPresent(data, "damage", card);
        addUpgradeEffectIfPresent(data, "block", card);
        addUpgradeEffectIfPresent(data, "heat_gain", card);
        addUpgradeEffectIfPresent(data, "heat_loss", card);
        addUpgradeEffectIfPresent(data, "rhythm_bonus_damage", card);
        addUpgradeEffectIfPresent(data, "rhythm_bonus_block", card);
        addUpgradeEffectIfPresent(data, "echo", card);
        addUpgradeEffectIfPresent(data, "energy_gain", card);
        addUpgradeEffectIfPresent(data, "draw", card);
        
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

    private static void addEffectIfPresent(String data, String field, Card card) {
        String val = getField(data, field);
        if (val != null) {
            try {
                card.addEffect(field, Integer.parseInt(val));
            } catch (NumberFormatException ignored) {}
        }
    }

    private static void addUpgradeEffectIfPresent(String data, String field, Card card) {
        String val = getField(data, "upgrade_" + field);
        if (val != null) {
            try {
                card.addUpgradeEffect(field, Integer.parseInt(val));
            } catch (NumberFormatException ignored) {}
        }
    }

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
