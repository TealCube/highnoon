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
package com.tealcube.minecraft.bukkit.highnoon.tasks;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.managers.ArenaManager;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class CountdownTask extends BukkitRunnable {

    private final Player a;
    private final Player b;
    private int number;

    public CountdownTask(Player a, Player b, int number) {
        this.a = a;
        this.b = b;
        this.number = number;
    }

    @Override
    public void run() {
        if (number > 0) {
            if (!areValid(a, b)) {
                return;
            }

            MessageUtils.sendMessage(a, "<white>Time remaining: %time%", new String[][]{{"%time%", number + ""}});

            number--;
        }
        else {
            Duelist playerDuelist = DuelistManager.getDuelist(a.getUniqueId());
            Duelist targetDuelist = DuelistManager.getDuelist(b.getUniqueId());

            playerDuelist.setTarget(targetDuelist.getUniqueId());
            targetDuelist.setTarget(playerDuelist.getUniqueId());

            Location midpoint = Misc.calculateMidpoint(a.getLocation(), b.getLocation());

            ArenaManager.setArena(playerDuelist.getUniqueId(), midpoint);
            ArenaManager.setArena(targetDuelist.getUniqueId(), midpoint);
            this.cancel();
        }
    }

    private boolean areValid(Player player, Player target) {
        return isValid(player, target) && isValid(target, player);
    }

    private boolean isValid(Player player, Player target) {
        if (target == null || !target.isValid()) {
            MessageUtils.sendMessage(player, "<red>Your duel was cancelled.");
            ArenaManager.setArena(player.getUniqueId(), null);
            this.cancel();
            return false;
        }

        return true;
    }

}
