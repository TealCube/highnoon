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
package com.tealcube.minecraft.bukkit.highnoon.utils;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.data.DuelInvitationKey;
import com.tealcube.minecraft.bukkit.highnoon.data.DuelResult;
import com.tealcube.minecraft.bukkit.highnoon.data.player.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.events.DuelEndEvent;
import com.tealcube.minecraft.bukkit.highnoon.managers.ArenaManager;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelHistoryManager;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.highnoon.tasks.CountdownTask;
import com.tealcube.minecraft.bukkit.highnoon.tasks.DuelEndTask;
import com.tealcube.minecraft.bukkit.highnoon.tasks.HealPlayerTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class DuelUtil {

    private DuelUtil() {
        // do nothing
    }

    public static boolean canDuel(Player player, boolean interact) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return false;
        }
        Duelist duelist = DuelistManager.getDuelist(player);
        if (interact) {
            if (HighNoonPlugin.getInstance().getSettings().getBoolean("config.challenge.hold-shift-required") && !player.isSneaking()) {
                return false;
            }
            if (!ItemUtil.isDuelWeapon(player.getItemInHand())) {
                return false;
            }
            if (System.currentTimeMillis() < duelist.getLastChallengeSent() + 3 * Misc.MILLIS_PER_SEC) {
                return false;
            }
        }
        return !DuelistUtil.isInDuel(player);
    }

    public static void handleDuelInvites(Player a, Player b) {
        Duelist duelistA = DuelistManager.getDuelist(a);
        DuelInvitationKey key = duelistA.getDuelInvitationKey();

        if (!acceptChallenge(b, key)) {
            Duelist duelistB = DuelistManager.getDuelist(b);
            DuelistUtil.challenge(duelistA, duelistB);
            return;
        }

        if (DuelistUtil.isLastInvitationTimedOut(duelistA)) {
            MessageUtils.sendMessage(a, "<red>Your duel invitation has expired.");
            DuelistUtil.removeDuelInvite(duelistA);
            return;
        }

        prepareDuel(a, b);

        new CountdownTask(a, b, 3).runTaskTimer(HighNoonPlugin.getInstance(), 0, Misc.TICKS_PER_SEC);
    }

    private static boolean acceptChallenge(Player target, DuelInvitationKey key) {
        return key != null && target.getUniqueId().equals(key.getPlayer());
    }

    public static void prepareDuel(Player player) {
        Duelist duelist = DuelistManager.getDuelist(player);
        DuelistUtil.removeDuelInvite(duelist);
        duelist.setOccupied(true);
    }

    public static void prepareDuel(Player a, Player b) {
        prepareDuel(a);
        prepareDuel(b);

        ArenaManager.setArena(a, b);
    }

    public static void startDuel(Player a, Player b) {
        if (!(a.isOnline() && b.isOnline())) {
            return;
        }

        Duelist duelistA = DuelistManager.getDuelist(a);
        Duelist duelistB = DuelistManager.getDuelist(b);

        DuelistUtil.removeDuelInvite(duelistA);
        DuelistUtil.removeDuelInvite(duelistB);
        DuelistUtil.setDueling(a, b);

        MessageUtils.sendMessage(a, "<green>Your duel has started.");
        MessageUtils.sendMessage(b, "<green>Your duel has started.");

        int duelLength = HighNoonPlugin.getInstance().getSettings().getInt("config.duel.length");

        duelistA.setDuelEndTask(new DuelEndTask(a).runTaskLater(HighNoonPlugin.getInstance(), duelLength * Misc.TICKS_PER_SEC));
        duelistB.setDuelEndTask(new DuelEndTask(b).runTaskLater(HighNoonPlugin.getInstance(), duelLength * Misc.TICKS_PER_SEC));
    }

    public static void endDuelResult(Player winner, Player loser) {
        endDuel(winner, true);
        endDuel(loser, false);

        DuelEndEvent winnerEvent = new DuelEndEvent(DuelistManager.getDuelist(winner), DuelResult.WIN);
        Bukkit.getPluginManager().callEvent(winnerEvent);

        DuelEndEvent loserEvent = new DuelEndEvent(DuelistManager.getDuelist(winner), DuelResult.WIN);
        Bukkit.getPluginManager().callEvent(loserEvent);

        DuelHistoryManager.setDuelResultAmount(winner.getUniqueId(), DuelResult.WIN, DuelHistoryManager.getDuelResultAmount(winner.getUniqueId(),
                DuelResult.WIN) + 1);
        DuelHistoryManager.setDuelResultAmount(loser.getUniqueId(), DuelResult.LOSS, DuelHistoryManager.getDuelResultAmount(loser.getUniqueId(),
                DuelResult.LOSS) + 1);

        if (HighNoonPlugin.getInstance().getSettings().getBoolean("config.duel.heal-after")) {
            new HealPlayerTask(winner).runTask(HighNoonPlugin.getInstance());
        }
    }

    public static void endDuelInTie(Player a) {
        Player target = DuelistUtil.getDuelPartner(a);

        if (target == null) {
            return;
        }

        DuelEndEvent tieEvent = new DuelEndEvent(DuelistManager.getDuelist(a), DuelResult.TIE);
        Bukkit.getPluginManager().callEvent(tieEvent);

        endDuel(a, true);
        endDuel(target, true);

        DuelHistoryManager.setDuelResultAmount(a.getUniqueId(), DuelResult.TIE, DuelHistoryManager.getDuelResultAmount(a.getUniqueId(),
                DuelResult.TIE) + 1);
        DuelHistoryManager.setDuelResultAmount(target.getUniqueId(), DuelResult.TIE, DuelHistoryManager.getDuelResultAmount(target.getUniqueId(),
                DuelResult.TIE) + 1);

        if (HighNoonPlugin.getInstance().getSettings().getBoolean("config.duel.heal-after")) {
            new HealPlayerTask(a).runTask(HighNoonPlugin.getInstance());
            new HealPlayerTask(target).runTask(HighNoonPlugin.getInstance());
        }
    }

    public static void endDuel(Player player, boolean deleteArena) {
        Duelist duelist = DuelistManager.getDuelist(player);

        if (duelist.getDuelEndTask() != null) {
            duelist.getDuelEndTask().cancel();
            duelist.setDuelEndTask(null);
        }

        DuelistUtil.removeDuelPartner(player);
        duelist.setOccupied(false);

        if (deleteArena) {
            ArenaManager.removeArena(player);
        }
    }

}
