package model;

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

    public String getName() { return name; }
    public String getDescription() { return description; }

    public static Relic getRandomRelic(List<Relic> playerRelics) {
        List<Relic> allRelics = getAllAvailableRelics();
        List<Relic> pool = new ArrayList<>();

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

        return pool.get(new Random().nextInt(pool.size()));
    }

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