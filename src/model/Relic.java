package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Relic {
    private String name;
    private String description;

    public Relic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<Relic> loadRelics(String filePath) {
        List<Relic> relics = new ArrayList<>();
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
                parseRelic(data, relics);
                index = end + 1;
            }
        } catch (Exception e) {
            System.err.println("Error loading relics from " + filePath + ": " + e.getMessage());
        }
        return relics;
    }

    private static void parseRelic(String data, List<Relic> relics) {
        String name = getField(data, "name");
        String desc = getField(data, "description");
        if (name != null && desc != null) {
            relics.add(new Relic(name, desc));
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

    public static Relic getRandomRelic() {
        List<Relic> pool = loadRelics("Res/relics.json");
        if (pool == null || pool.isEmpty()) {
            return new Relic("Strawberry", "Max HP increased by 10. Heal 10 HP.");
        }
        return pool.get(new Random().nextInt(pool.size()));
    }
}
