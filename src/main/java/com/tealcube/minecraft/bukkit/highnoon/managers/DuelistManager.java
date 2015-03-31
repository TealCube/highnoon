/*
 * This file is part of HighNoon, licensed under the ISC License.
 *
 * Copyright (c) 2015 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.highnoon.managers;

import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Preconditions;
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
