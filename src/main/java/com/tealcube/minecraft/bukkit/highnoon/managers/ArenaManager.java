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

import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ArenaManager {

    private ArenaManager() {
        // do nothing
    }

    public static void setArena(Player a, Player b) {
        Location midpoint = Misc.calculateMidpoint(a.getLocation(), b.getLocation());
        midpoint.setY(midpoint.getY() + 0.5);

        DuelistManager.getDuelist(a).setArena(midpoint);
        DuelistManager.getDuelist(b).setArena(midpoint);
    }

    public static Location getArena(Player player) {
        return DuelistManager.getDuelist(player).getArena();
    }

    public static void removeArena(Player player) {
        DuelistManager.getDuelist(player).setArena(null);
    }

}
