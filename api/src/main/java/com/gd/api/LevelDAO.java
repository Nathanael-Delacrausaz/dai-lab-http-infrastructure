package com.gd.api;

import java.util.*;

public class LevelDAO {
    private final Map<Integer, Level> levels = new HashMap<>();
    private int nextId = 1;

    public LevelDAO() {
        // initial data
        createLevel(new Level(0, "Bloodbath", "Riot", "Riot", "Extreme Demon", 
                    10.0, "Long", 22510, 100));
        createLevel(new Level(0, "Sonic Wave", "Cyclic", "Sunix", "Extreme Demon", 
                    9.8, "Long", 35421, 100));
        createLevel(new Level(0, "Tartarus", "Dolphy", "Riot", "Extreme Demon", 
                    10.0, "Long", 45123, 100));
    }

    public List<Level> getAllLevels() {
        return new ArrayList<>(levels.values());
    }

    public Optional<Level> getLevel(int id) {
        return Optional.ofNullable(levels.get(id));
    }

    public Level createLevel(Level level) {
        level.setId(nextId++);
        levels.put(level.getId(), level);
        return level;
    }

    public Optional<Level> updateLevel(int id, Level level) {
        if (levels.containsKey(id)) {
            level.setId(id);
            levels.put(id, level);
            return Optional.of(level);
        }
        return Optional.empty();
    }

    public boolean deleteLevel(int id) {
        return levels.remove(id) != null;
    }
}
