package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyLoader {
    private static List<Enemy> enemyTemplates = new ArrayList<>();

    public static void loadEnemies(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            enemyTemplates = gson.fromJson(reader, new TypeToken<List<Enemy>>(){}.getType());
            System.out.println("Successfully loaded " + enemyTemplates.size() + " enemy templates from JSON.");
        } catch (Exception e) {
            System.err.println("Error loading enemies from JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Enemy getRandomEnemyByPool(String pool) {
        List<Enemy> filtered = new ArrayList<>();
        for (Enemy e : enemyTemplates) {
            if (e.getPool() != null && e.getPool().equalsIgnoreCase(pool)) {
                filtered.add(e);
            }
        }
        if (filtered.isEmpty()) return null;

        return filtered.get(new Random().nextInt(filtered.size()));
    }
}