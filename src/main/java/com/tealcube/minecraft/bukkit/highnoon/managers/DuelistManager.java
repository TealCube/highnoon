/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.highnoon.managers;

import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    public static Collection<UUID> getDuelingUuids() {
        Set<UUID> uuidSet = new HashSet<UUID>();
        for (Duelist d : DUELIST_MAP.values()) {
            if (d.getTarget() != null) {
                uuidSet.add(d.getUniqueId());
            }
        }
        return uuidSet;
    }

    public static Collection<Player> getDuelingPlayers() {
        Set<Player> players = new HashSet<Player>();
        for (UUID uuid : getDuelingUuids()) {
            players.add(Bukkit.getPlayer(uuid));
        }
        return players;
    }

}
