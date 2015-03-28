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
import com.tealcube.minecraft.bukkit.highnoon.data.LevelExpKey;
import com.tealcube.minecraft.bukkit.highnoon.data.player.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DuelistUtil {

    private DuelistUtil() {
        // do nothing here
    }

    public static void setDueling(Player a, Player b) {
        DuelistManager.getDuelist(a).setDuelPartner(b.getUniqueId());
        DuelistManager.getDuelist(b).setDuelPartner(a.getUniqueId());
    }

    public static Player getDuelPartner(Player a) {
        UUID duelPartner = DuelistManager.getDuelist(a).getDuelPartner();
        if (duelPartner != null) {
            return Bukkit.getPlayer(duelPartner);
        }
        return null;
    }

    public static boolean removeDuelPartner(Player a) {
        Duelist duelist = DuelistManager.getDuelist(a);
        if (duelist.getDuelPartner() != null) {
            duelist.setDuelPartner(null);
            return true;
        }
        return false;
    }

    public static boolean isInDuel(Player a) {
        return DuelistManager.getDuelist(a).getDuelPartner() != null;
    }

    public static boolean areDueling(Player a, Player b) {
        Duelist duelistA = DuelistManager.getDuelist(a);
        Duelist duelistB = DuelistManager.getDuelist(b);

        UUID targetA = duelistA.getDuelPartner();
        UUID targetB = duelistB.getDuelPartner();

        return !(targetA == null || targetB == null) && targetA.equals(duelistB.getUuid()) && targetB.equals(duelistA.getUuid());
    }

    public static UUID getLastInvitationFrom(Duelist duelist) {
        return duelist.getDuelInvitationKey() != null ? duelist.getDuelInvitationKey().getPlayer() : null;
    }

    public static boolean isLastInvitationTimedOut(Duelist duelist) {
        DuelInvitationKey key = duelist.getDuelInvitationKey();
        return key == null ||
                key.getTimestamp() + HighNoonPlugin.getInstance().getSettings().getInt("config.challenge.timeout") < System.currentTimeMillis();
    }

    public static void removeDuelInvite(Duelist duelist) {
        duelist.setDuelPartner(null);
    }

    public static void challenge(Duelist a, Duelist b) {
        Player aPlayer = Bukkit.getPlayer(a.getUuid());
        a.actualizeLastChallengeReceived();

        if (System.currentTimeMillis() < (b.getLastChallengedReceived() + 3 * 1000)) {
            return;
        }

        Player bPlayer = Bukkit.getPlayer(b.getUuid());
        if (hasBeenChallenged(b, a) && !isLastInvitationTimedOut(b)) {
            MessageUtils.sendMessage(aPlayer, "<red>You already sent that player a challenge.");
            return;
        }

        b.actualizeLastChallengeReceived();
        MessageUtils
                .sendMessage(aPlayer, "<green>You sent <white>%target%<green> a challenge.", new String[][]{{"%target%", bPlayer.getDisplayName()}});
        b.setDuelInvitationKey(new DuelInvitationKey(a.getDuelPartner()));

        MessageUtils.sendMessage(bPlayer, "<green>You received a duel challenge from <white>%challenger%<green>.",
                new String[][]{{"%challenger%", aPlayer.getDisplayName()}});
        if (HighNoonPlugin.getInstance().getSettings().getBoolean("config.challenge.command")) {
            MessageUtils.sendMessage(bPlayer, "<green>Use ");
        }
    }

    private static boolean hasBeenChallenged(Duelist a, Duelist b) {
        return getLastInvitationFrom(a) != null && getLastInvitationFrom(a).equals(b.getUuid());
    }

    public static List<UUID> getDuelingPlayers() {
        Iterable<Duelist> duelists = DuelistManager.getDuelists();
        List<UUID> uuids = new ArrayList<UUID>();
        for (Duelist duelist : duelists) {
            if (duelist.getDuelPartner() != null) {
                uuids.add(duelist.getUuid());
            }
        }
        return uuids;
    }

    public static void storeLevelAndExp(Duelist duelist) {
        Player player = Bukkit.getPlayer(duelist.getUuid());
        duelist.setSavedLevel(new LevelExpKey(player.getLevel(), player.getExp()));
    }

    public static boolean retreiveLevelsAndExp(Duelist duelist) {
        Player player = Bukkit.getPlayer(duelist.getUuid());

        LevelExpKey key = duelist.getSavedLevel();

        if (key == null) {
            return false;
        }

        player.setLevel(key.getLevel());
        player.setExp(key.getExp());
        return true;
    }

}
