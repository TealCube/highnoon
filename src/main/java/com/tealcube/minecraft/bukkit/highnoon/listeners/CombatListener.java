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
package com.tealcube.minecraft.bukkit.highnoon.listeners;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.data.Duel;
import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.events.DuelEndEvent;
import com.tealcube.minecraft.bukkit.highnoon.managers.ArenaManager;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.highnoon.tasks.HealPlayerTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        Duelist duelist = DuelistManager.getDuelist(player.getUniqueId());
        if (duelist.getTarget() == null) {
            return;
        }

        double playerHealth = player.getHealth();
        double damageAmount = event.getFinalDamage();

        if ((playerHealth - damageAmount) > 0) {
            return;
        }

        Player target = Bukkit.getPlayer(duelist.getTarget());

        event.setCancelled(true);
        player.setHealth(1);
        player.teleport(ArenaManager.getArena(player.getUniqueId()));

        DuelEndEvent ev = new DuelEndEvent(new Duel(duelist.getUniqueId(), duelist.getTarget()));
        ev.getDuel().setWinner(duelist.getTarget());
        ev.getDuel().setLoser(duelist.getUniqueId());
        Bukkit.getPluginManager().callEvent(ev);

        new HealPlayerTask(player).runTaskLater(HighNoonPlugin.getInstance(), 1);
        new HealPlayerTask(Bukkit.getPlayer(duelist.getTarget())).runTaskLater(HighNoonPlugin.getInstance(), 1);

        MessageUtils.sendMessage(player, "<red>You lost your duel!");
        MessageUtils.sendMessage(Bukkit.getPlayer(duelist.getTarget()), "<green>You won your duel!");

        Duelist targetDuelist = DuelistManager.getDuelist(duelist.getTarget());
        targetDuelist.setTarget(null);
        duelist.setTarget(null);
        duelist.getTask().cancel();
        duelist.setTask(null);
        targetDuelist.setTask(null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamage() <= 0) {
            return;
        }
        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();

        if (attacker instanceof Projectile) {
            if (((Projectile) attacker).getShooter() instanceof LivingEntity) {
                attacker = (LivingEntity) ((Projectile) attacker).getShooter();
            }
        } else if (attacker instanceof Tameable) {
            if (((Tameable) attacker).getOwner() instanceof LivingEntity) {
                attacker = (LivingEntity) ((Tameable) attacker).getOwner();
            }
        }

        if (defender instanceof Player) {
            if (!((Player) defender).isOnline() || !defender.isValid()) {
                return;
            }
            if (attacker instanceof Player) {
                Duelist attackerDuelist = DuelistManager.getDuelist(attacker.getUniqueId());
                Duelist defenderDuelist = DuelistManager.getDuelist(defender.getUniqueId());
                if (attackerDuelist.getTarget() == null && defenderDuelist.getTarget() == null) {
                    return;
                }
                if (attackerDuelist.getTarget() == null) {
                    MessageUtils.sendMessage(attacker, "<red>You cannot attack them, they are in a duel.");
                    event.setCancelled(true);
                    return;
                }
                if (defenderDuelist.getTarget() == null) {
                    MessageUtils.sendMessage(attacker, "<red>You cannot attack them, you are in a duel.");
                    event.setCancelled(true);
                    return;
                }
                if (attackerDuelist.getTarget().equals(defenderDuelist.getUniqueId()) && defenderDuelist.getTarget().equals(attackerDuelist
                        .getUniqueId())) {
                    return;
                }
                MessageUtils.sendMessage(attacker, "<red>You cannot attack them, you're not dueling them.");
                event.setCancelled(true);
            }
        }
    }

}
