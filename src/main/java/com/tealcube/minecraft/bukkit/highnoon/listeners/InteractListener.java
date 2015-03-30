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
package com.tealcube.minecraft.bukkit.highnoon.listeners;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.data.Duel;
import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.events.DuelEndEvent;
import com.tealcube.minecraft.bukkit.highnoon.managers.ArenaManager;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.highnoon.tasks.CountdownTask;
import com.tealcube.minecraft.bukkit.highnoon.tasks.HealPlayerTask;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import info.faceland.q.actions.options.Option;
import info.faceland.q.actions.questions.Question;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class InteractListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (!player.isSneaking() && HighNoonPlugin.getInstance().getSettings().getBoolean("config.challenge.hold-shift-required")) {
            return;
        }

        if (!HighNoonPlugin.getInstance().getSettings().getStringList("config.challenge.duel-materials").contains(player.getItemInHand().getType()
                .name())) {
            return;
        }

        if (entity instanceof Player) {
            event.setCancelled(true);

            final Player target = (Player) entity;

            final Duelist playerDuelist = DuelistManager.getDuelist(player.getUniqueId());
            final Duelist targetDuelist = DuelistManager.getDuelist(target.getUniqueId());

            if (playerDuelist.getTarget() != null) {
                MessageUtils.sendMessage(player, "<red>You're in a duel, you cannot invite other players to duel.");
                return;
            }
            if (targetDuelist.getTarget() != null) {
                MessageUtils.sendMessage(target, "<red>They're in a duel, you cannot invite them to duel.");
                return;
            }

            List<Option> options = new ArrayList<Option>();
            options.add(new Option("accept", new Runnable() {
                @Override
                public void run() {
                    if (playerDuelist.getTarget() != null) {
                        MessageUtils.sendMessage(target, "<red>You cannot accept their challenge, they are in a duel.");
                        return;
                    }
                    if (targetDuelist.getTarget() != null) {
                        MessageUtils.sendMessage(player, "<red>You cannot accept their challenge, you are in a duel.");
                        return;
                    }

                    new CountdownTask(player, target, 3).runTaskTimer(HighNoonPlugin.getInstance(), 0, Misc.TICKS_PER_SEC);

                    MessageUtils.sendMessage(player, "<white>%player%<green> accepted your duel request.");
                }
            }, "Accept the duel invitation"));
            options.add(new Option("decline", new Runnable() {
                @Override
                public void run() {
                    MessageUtils.sendMessage(player, "<white>%player%<red> declined your duel request.");
                }
            }, "Decline the duel invitation"));
            Question question = new Question(target.getUniqueId(), TextUtils.color(TextUtils.args(
                    "<white>%player%<green> has invited you to duel.", new String[][]{{"%player%", player.getDisplayName()}})), options);
            HighNoonPlugin.getInstance().getQPlugin().getQuestionManager().appendQuestion(question);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Duelist duelist = DuelistManager.getDuelist(player.getUniqueId());
        if (duelist.getTarget() == null) {
            return;
        }

        DuelEndEvent ev = new DuelEndEvent(new Duel(duelist.getUniqueId(), duelist.getTarget()));
        ev.getDuel().setWinner(duelist.getTarget());
        ev.getDuel().setLoser(duelist.getUniqueId());
        Bukkit.getPluginManager().callEvent(ev);

        new HealPlayerTask(player).runTaskLater(HighNoonPlugin.getInstance(), 1);
        new HealPlayerTask(Bukkit.getPlayer(duelist.getTarget())).runTaskLater(HighNoonPlugin.getInstance(), 1);

        DuelistManager.getDuelist(duelist.getTarget()).setTarget(null);
        duelist.setTarget(null);

        MessageUtils.sendMessage(player, "<red>You lost your duel!");
        MessageUtils.sendMessage(Bukkit.getPlayer(duelist.getTarget()), "<green>You won your duel!");
    }

}
