package com.tealcube.minecraft.bukkit.highnoon.managers;

import com.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.highnoon.data.DuelResult;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.collect.HashBasedTable;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.collect.Table;

import java.util.UUID;

public final class DuelHistoryManager {

    private static final Table<UUID, DuelResult, Integer> DUEL_HISTORY_TABLE = HashBasedTable.create();

    private DuelHistoryManager() {
        // do nothing
    }

    public static int getResults(UUID uuid, DuelResult type) {
        Preconditions.checkNotNull(uuid);
        Preconditions.checkNotNull(type);
        return DUEL_HISTORY_TABLE.contains(uuid, type) ? DUEL_HISTORY_TABLE.get(uuid, type) : 0;
    }

    public static void setResults(UUID uuid, DuelResult type, int amount) {
        Preconditions.checkNotNull(uuid);
        Preconditions.checkNotNull(type);
        DUEL_HISTORY_TABLE.put(uuid, type, amount);
    }

    public static void incrementResults(UUID uuid, DuelResult type, int amount) {
        setResults(uuid, type, getResults(uuid, type) + amount);
    }

    public static void decrementResults(UUID uuid, DuelResult type, int amount) {
        setResults(uuid, type, getResults(uuid, type) - amount);
    }

    public static Table<UUID, DuelResult, Integer> getDuelHistoryTable() {
        return DUEL_HISTORY_TABLE;
    }

}
