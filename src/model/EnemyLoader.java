package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pomocná třída zajišťující načítání šablon nepřátel z externího JSON souboru.
 * Slouží jako továrna, která dokáže ze stažených šablon vyfiltrovat a náhodně
 * vybrat nepřítele pro konkrétní herní zónu (Pool).
 */
public class EnemyLoader {

    /** Statický seznam, ve kterém jsou uloženy všechny načtené šablony nepřátel. */
    private static List<Enemy> enemyTemplates = new ArrayList<>();

    /**
     * Načte soubor JSON a pomocí knihovny GSON ho deserializuje
     * přímo do seznamu objektů typu Enemy.
     * * @param filePath Cesta k JSON souboru s daty nepřátel.
     */
    public static void loadEnemies(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            // TypeToken říká GSONu, že chceme data mapovat na List instancí třídy Enemy
            enemyTemplates = gson.fromJson(reader, new TypeToken<List<Enemy>>(){}.getType());
            System.out.println("Successfully loaded " + enemyTemplates.size() + " enemy templates from JSON.");
        } catch (Exception e) {
            System.err.println("Error loading enemies from JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Profiltruje dostupné šablony nepřátel a vybere z nich jednoho náhodného,
     * který odpovídá zadanému fondu/zóně (např. "Zone1", "BossPool").
     * * @param pool Identifikátor zóny, pro kterou nepřítele hledáme.
     * @return Nová instance vybraného nepřítele, nebo null pokud žádný neodpovídá.
     */
    public static Enemy getRandomEnemyByPool(String pool) {
        List<Enemy> filtered = new ArrayList<>();

        // Vyfiltrování nepřátel patřících do požadovaného poolu
        for (Enemy e : enemyTemplates) {
            if (e.getPool() != null && e.getPool().equalsIgnoreCase(pool)) {
                filtered.add(e);
            }
        }
        if (filtered.isEmpty()) return null;

        // Výběr náhodného prvku z vyfiltrovaného seznamu
        return filtered.get(new Random().nextInt(filtered.size()));
    }
}