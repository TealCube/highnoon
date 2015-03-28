/*
 * This file is part of Tribes, licensed under the ISC License.
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

import com.tealcube.minecraft.bukkit.highnoon.data.player.Duelist;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class DuelistManager {

    private static final Map<UUID, Duelist> DUELISTS = new HashMap<UUID, Duelist>();

    private DuelistManager() {
        // do nothing here
    }

    public static Duelist addDuelist(UUID uuid) {
        Duelist duelist = DUELISTS.get(uuid);

        if (duelist != null) {
            return duelist;
        }

        duelist = new Duelist(uuid);
        DUELISTS.put(uuid, duelist);
        return duelist;
    }

    public static Duelist addDuelist(Player player) {
        return addDuelist(player.getUniqueId());
    }

    public static void removeDuelist(Player player) {
        DUELISTS.remove(player.getUniqueId());
    }

    public static void clear() {
        DUELISTS.clear();
    }

    public static Set<UUID> getUniqueIds() {
        return DUELISTS.keySet();
    }

    public static Collection<Duelist> getDuelists() {
        return DUELISTS.values();
    }

    public static Duelist getDuelist(UUID uuid) {
        return DUELISTS.containsKey(uuid) ? DUELISTS.get(uuid) : addDuelist(uuid);
    }

    public static Duelist getDuelist(Player player) {
        return getDuelist(player.getUniqueId());
    }

}
