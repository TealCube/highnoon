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
package com.tealcube.minecraft.bukkit.highnoon.listeners;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.data.Duel;
import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.events.DuelEndEvent;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.highnoon.tasks.CountdownTask;
import com.tealcube.minecraft.bukkit.highnoon.tasks.HealPlayerTask;
import com.tealcube.minecraft.bukkit.highnoon.utils.Formatter;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import com.tealcube.minecraft.bukkit.highnoon.utils.WGUtils;
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
import java.util.Set;

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

            Location location = Misc.calculateMidpoint(player.getLocation(), target.getLocation());
            int size = (int) HighNoonPlugin.getInstance().getSettings().getDouble("config.arena.radius");
            Set<Location> arenaLocs = Misc.sphere(location, size, size, false, true, 0);

            for (Location loc : arenaLocs) {
                if (WGUtils.inRegion(loc)) {
                    MessageUtils.sendMessage(player, "<red>You cannot duel here.");
                    return;
                }
            }

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

            final long cooldown = HighNoonPlugin.getInstance().getSettings().getLong("config.challenge.cooldown");

            if (!Misc.hasTimePassed(playerDuelist.getLastDuelEnded(), cooldown)) {
                MessageUtils.sendMessage(player, "<red>You have ended a duel too recently to duel again. Try again in <white>%amount%<red> seconds.",
                        new String[][]{{"%amount%", (cooldown - (Misc.currentTimeSeconds() - playerDuelist.getLastDuelEnded())) + ""}});
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

                    if (!Misc.hasTimePassed(targetDuelist.getLastDuelEnded(), cooldown)) {
                        MessageUtils.sendMessage(target, "<red>You have ended a duel too recently to duel again. You can accept duels in " +
                                "<white>%amount%<red> seconds. Have them challenge you again.",
                                new String[][]{{"%amount%", (cooldown - (Misc.currentTimeSeconds() - targetDuelist.getLastDuelEnded())) + ""}});
                        return;
                    }

                    new CountdownTask(player, target, 3).runTaskTimer(HighNoonPlugin.getInstance(), 0, Misc.TICKS_PER_SEC);

                    MessageUtils.sendMessage(target, "<green>You accepted <white>%player%<green>'s duel request.", new String[][]{{"%player%", target
                            .getDisplayName()}});
                    MessageUtils.sendMessage(player, "<white>%player%<green> accepted your duel request.", new String[][]{{"%player%", target.getDisplayName()}});
                }
            }, "Accept the duel invitation"));
            options.add(new Option("decline", new Runnable() {
                @Override
                public void run() {
                    MessageUtils.sendMessage(target, "<red>You declined <white>%player%<red>'s duel request.", new String[][]{{"%player%", player
                            .getDisplayName()}});
                    MessageUtils.sendMessage(player, "<white>%player%<red> declined your duel request.", new String[][]{{"%player%", target
                            .getDisplayName()}});
                }
            }, "Decline the duel invitation"));
            Question question = new Question(target.getUniqueId(), TextUtils.color(TextUtils.args(
                    "<white>%player%<green> has invited you to duel.", new String[][]{{"%player%", player.getDisplayName()}})), options);
            HighNoonPlugin.getInstance().getQPlugin().getQuestionManager().appendQuestion(question);
            List<String> messages = Formatter.format(question);
            for (String m : messages) {
                target.sendMessage(m);
            }
            MessageUtils.sendMessage(player, "<green>You sent a duel invite to <white>%name%<green>.",
                    new String[][]{{"%name%", target.getDisplayName()}});
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

        MessageUtils.sendMessage(player, "<red>You lost your duel!");
        MessageUtils.sendMessage(Bukkit.getPlayer(duelist.getTarget()), "<green>You won your duel!");

        Duelist target = DuelistManager.getDuelist(duelist.getTarget());
        target.setTarget(null);
        duelist.setTarget(null);
        duelist.getTask().cancel();
        duelist.setTask(null);
        target.setTask(null);
        duelist.setLastDuelEnded(Misc.currentTimeSeconds());
        target.setLastDuelEnded(Misc.currentTimeSeconds());
    }

}
