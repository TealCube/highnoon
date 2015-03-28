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
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.data.player.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.managers.ArenaManager;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.highnoon.tasks.RestoreLevelExpTask;
import com.tealcube.minecraft.bukkit.highnoon.utils.DuelUtil;
import com.tealcube.minecraft.bukkit.highnoon.utils.DuelistUtil;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Player) {
            Player target = (Player) entity;

            if (!DuelUtil.canDuel(player, true) || !DuelUtil.canDuel(target, false)) {
                return;
            }

            if (DuelistManager.getDuelist(target).isOccupied()) {
                MessageUtils.sendMessage(player, "<red>That player is occupied at the moment.");
                return;
            }

            event.setCancelled(true);

            DuelUtil.handleDuelInvites(player, target);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Duelist duelPlayer = DuelistManager.getDuelist(player);
        duelPlayer.setOccupied(false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!DuelistUtil.isInDuel(player)) {
            return;
        }

        Location targetLocation = event.getTo();
        Location arenaCenter = ArenaManager.getArena(player);

        if (!Misc.isWithinDistance(targetLocation, arenaCenter, HighNoonPlugin.getInstance().getSettings().getDouble("config.arena.radius"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (DuelistUtil.isInDuel(player)) {
            Player target = DuelistUtil.getDuelPartner(player);

            DuelUtil.endDuelResult(target, player);
        }

        if (DuelistManager.getDuelist(player).isDuelRespawn()) {
            ArenaManager.removeArena(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!HighNoonPlugin.getInstance().getSettings().getBoolean("config.duel.allow-dying")) {
            return;
        }

        Player player = event.getPlayer();

        Location arenaCenter = ArenaManager.getArena(player);

        Duelist duelPlayer = DuelistManager.getDuelist(player);

        if (!duelPlayer.isDuelRespawn() || arenaCenter == null) {
            return;
        }

        event.setRespawnLocation(arenaCenter);
        duelPlayer.setDuelRespawn(false);
        ArenaManager.removeArena(player);

        List<ItemStack> armorList = duelPlayer.getSavedInventoryArmor();
        ItemStack[] armor = armorList.toArray(new ItemStack[armorList.size()]);
        player.getInventory().setArmorContents(armor);

        List<ItemStack> items = duelPlayer.getSavedInventoryItems();
        if (items != null) {
            for (ItemStack item : items) {
                player.getInventory().addItem(item);
            }
        }
        new RestoreLevelExpTask(player).runTaskLater(HighNoonPlugin.getInstance(), 1);

        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (!HighNoonPlugin.getInstance().getSettings().getBoolean("config.duel.allow-dying")) {
            return;
        }

        Player player = event.getEntity();

        if (!DuelistUtil.isInDuel(player)) {
            return;
        }

        Duelist duelPlayer = DuelistManager.getDuelist(player);
        duelPlayer.setDuelRespawn(true);

        List<ItemStack> armorItems = new ArrayList<ItemStack>();
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null) {
                armorItems.add(armor);
            }
        }

        List<ItemStack> items = new ArrayList<ItemStack>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                items.add(item);
            }
        }

        duelPlayer.setSavedInventoryArmor(armorItems);
        duelPlayer.setSavedInventoryItems(items);
        DuelistUtil.storeLevelAndExp(duelPlayer);

        event.getDrops().clear();
        event.setDroppedExp(0);

        DuelUtil.endDuelResult(DuelistUtil.getDuelPartner(player), player);
    }

}
