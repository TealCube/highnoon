package com.tealcube.minecraft.bukkit.highnoon.managers;

import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DuelistManager {

    private static final Map<UUID, Duelist> DUELIST_MAP = new HashMap<UUID, Duelist>();

    private DuelistManager() {
        // do nothing here
    }

    public static Duelist createDuelist(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        Duelist duelist = new Duelist(uuid);
        DUELIST_MAP.put(uuid, duelist);
        return duelist;
    }

    public static Duelist getDuelist(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return hasDuelist(uuid) ? DUELIST_MAP.get(uuid) : createDuelist(uuid);
    }

    public static boolean hasDuelist(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return DUELIST_MAP.containsKey(uuid);
    }

    public static boolean removeDuelist(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        DUELIST_MAP.remove(uuid);
        return hasDuelist(uuid);
    }

    public static boolean addDuelist(Duelist duelist) {
        Preconditions.checkNotNull(duelist);
        DUELIST_MAP.put(duelist.getUniqueId(), duelist);
        return hasDuelist(duelist.getUniqueId());
    }

}
