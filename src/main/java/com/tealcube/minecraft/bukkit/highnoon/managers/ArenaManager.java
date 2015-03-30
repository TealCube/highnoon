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
