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
package com.tealcube.minecraft.bukkit.highnoon.tasks;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.highnoon.utils.DuelUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTask extends BukkitRunnable {

    private final Player playerOne;
    private final Player playerTwo;
    private int number;

    public CountdownTask(Player playerOne, Player playerTwo, int number) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.number = number;
    }

    @Override
    public void run() {
        countdown();
    }

    private void countdown() {
        if (number > 0) {
            if (!areValid(playerOne, playerTwo)) {
                return;
            }

            MessageUtils.sendMessage(playerOne, "<green>Seconds until duel: <white>%time%", new String[][]{{"%time%", "" + number}});
            MessageUtils.sendMessage(playerTwo, "<green>Seconds until duel: <white>%time%", new String[][]{{"%time%", "" + number}});

            number--;
        } else {
            DuelUtil.startDuel(playerOne, playerTwo);
            this.cancel();
        }
    }

    private boolean areValid(Player a, Player b) {
        return isValid(a, b) && isValid(b, a);
    }

    private boolean isValid(Player a, Player b) {
        if (b == null || !b.isValid()) {
            MessageUtils.sendMessage(a, "<red>Your duel was cancelled.");
            this.cancel();
            return false;
        }
        return true;
    }

}
