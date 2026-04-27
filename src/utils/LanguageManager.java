package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages game localization and translations.
 */
public class LanguageManager {
    public enum Language {
        CZECH, ENGLISH
    }

    private static Language currentLanguage = Language.ENGLISH;
    private static final Map<Language, Map<String, String>> translations = new HashMap<>();

    static {
        Map<String, String> en = new HashMap<>();
        en.put("main_menu", "Main Menu");
        en.put("start_game", "Start Game");
        en.put("change_language", "Change Language:Czech");
        en.put("map_view", "Map View");
        en.put("combat_screen", "Combat Screen");
        translations.put(Language.ENGLISH, en);

        Map<String, String> cs = new HashMap<>();
        cs.put("main_menu", "Hlavní Menu");
        cs.put("start_game", "Spustit Hru");
        cs.put("change_language", "Změnit Jazyk:Angličtina");
        cs.put("map_view", "Mapa");
        cs.put("combat_screen", "Souboj");
        translations.put(Language.CZECH, cs);
    }

    public static String getString(String key) {
        return translations.get(currentLanguage).getOrDefault(key, key);
    }

    public static void setLanguage(Language language) {
        currentLanguage = language;
    }

    public static Language getCurrentLanguage() {
        return currentLanguage;
    }

    public static void toggleLanguage() {
        if (currentLanguage == Language.ENGLISH) {
            currentLanguage = Language.CZECH;
        } else {
            currentLanguage = Language.ENGLISH;
        }
    }
}
