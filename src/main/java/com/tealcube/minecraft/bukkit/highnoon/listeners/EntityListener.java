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

import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.managers.ArenaManager;
import com.tealcube.minecraft.bukkit.highnoon.tasks.HealPlayerTask;
import com.tealcube.minecraft.bukkit.highnoon.utils.DuelUtil;
import com.tealcube.minecraft.bukkit.highnoon.utils.DuelistUtil;
import org.bukkit.entity.AnimalTamer;
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
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (HighNoonPlugin.getInstance().getSettings().getBoolean("config.duel.allow-dying")) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        if (!DuelistUtil.isInDuel(player)) {
            return;
        }

        double playerHealth = player.getHealth();
        double damageAmount = event.getFinalDamage();

        if ((playerHealth - damageAmount) > 0) {
            return;
        }

        event.setCancelled(true);
        player.setHealth(1);
        player.teleport(ArenaManager.getArena(player));
        DuelUtil.endDuelResult(DuelistUtil.getDuelPartner(player), player);
        new HealPlayerTask(player).runTaskLater(HighNoonPlugin.getInstance(), 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamage() <= 0) {
            return;
        }

        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();

        if (attacker instanceof Projectile) {
            ProjectileSource projectileSource = ((Projectile) attacker).getShooter();

            if (projectileSource instanceof LivingEntity) {
                attacker = (LivingEntity) projectileSource;
            }
        }
        else if (attacker instanceof Tameable) {
            AnimalTamer animalTamer = ((Tameable) attacker).getOwner();

            if (animalTamer instanceof Entity) {
                attacker = (Entity) animalTamer;
            }
        }

        if (defender instanceof Player) {
            Player defendingPlayer = (Player) defender;

            if (!defendingPlayer.isOnline()) {
                return;
            }

            if (attacker instanceof Player) {
                Player attackingPlayer = (Player) attacker;
                if (!DuelistUtil.areDueling(attackingPlayer, defendingPlayer)) {
                    event.setCancelled(false);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        ProjectileSource projectileSource = event.getPotion().getShooter();

        if (!(projectileSource instanceof Player)) {
            return;
        }

        Player player = (Player) projectileSource;

        for (LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player)) {
                continue;
            }

            Player target = (Player) entity;

            if (player == target) {
                continue;
            }

            if (DuelistUtil.isInDuel(target)) {
                if (!DuelistUtil.areDueling(player, target)) {
                    event.setIntensity(target, 0);
                }
            }
        }
    }

}
