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
package com.tealcube.minecraft.bukkit.highnoon.tasks;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.managers.ArenaManager;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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

            MessageUtils.sendMessage(a, "<green>Duel starts in: <white>%time%", new String[][]{{"%time%", number + ""}});
            MessageUtils.sendMessage(b, "<green>Duel starts in: <white>%time%", new String[][]{{"%time%", number + ""}});

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

            DuelEndTask duelEndTask = new DuelEndTask(playerDuelist, targetDuelist);
            BukkitTask task = duelEndTask.runTaskLater(HighNoonPlugin.getInstance(), HighNoonPlugin.getInstance().getSettings().getInt("config.duel" +
                    ".length") * Misc.TICKS_PER_SEC);
            playerDuelist.setTask(task);
            targetDuelist.setTask(task);
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
