package com.tealcube.minecraft.bukkit.highnoon.managers;

import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Preconditions;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ArenaManager {

    private static final Map<UUID, Location> ARENA_MAP = new HashMap<UUID, Location>();

    private ArenaManager() {
        // do nothing
    }

    public static Location getArena(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return ARENA_MAP.containsKey(uuid) ? ARENA_MAP.get(uuid) : null;
    }

    public static void setArena(UUID uuid, Location location) {
        Preconditions.checkNotNull(uuid);
        if (location == null) {
            ARENA_MAP.remove(uuid);
        } else {
            ARENA_MAP.put(uuid, location);
        }
    }

}
