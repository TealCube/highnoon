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
import com.tealcube.minecraft.bukkit.highnoon.data.Duel;
import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.events.DuelEndEvent;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class DuelEndTask extends BukkitRunnable {

    private final Duelist a;
    private final Duelist b;

    public DuelEndTask(Duelist a, Duelist b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        DuelEndEvent ev = new DuelEndEvent(new Duel(a.getUniqueId(), b.getTarget()));
        Bukkit.getPluginManager().callEvent(ev);

        a.setTarget(null);
        b.setTarget(null);

        a.setLastDuelEnded(Misc.currentTimeSeconds());
        b.setLastDuelEnded(Misc.currentTimeSeconds());

        Player playerA = Bukkit.getPlayer(a.getUniqueId());
        Player playerB = Bukkit.getPlayer(b.getUniqueId());

        if (playerA != null && playerA.isValid()) {
            MessageUtils.sendMessage(playerA, "<red>Your duel timed out.");
        }
        if (playerB != null && playerB.isValid()) {
            MessageUtils.sendMessage(playerB, "<red>Your duel timed out.");
        }
    }

}
