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

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.data.Duel;
import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.events.DuelEndEvent;
import com.tealcube.minecraft.bukkit.highnoon.managers.ArenaManager;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.highnoon.tasks.HealPlayerTask;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
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
        player.setHealth(20);
        player.teleport(ArenaManager.getArena(player.getUniqueId()));

        DuelEndEvent ev = new DuelEndEvent(new Duel(duelist.getUniqueId(), duelist.getTarget()));
        ev.getDuel().setWinner(duelist.getTarget());
        ev.getDuel().setLoser(duelist.getUniqueId());
        Bukkit.getPluginManager().callEvent(ev);

        new HealPlayerTask(player).runTaskLater(HighNoonPlugin.getInstance(), 1);
        new HealPlayerTask(target).runTaskLater(HighNoonPlugin.getInstance(), 1);

        MessageUtils.sendMessage(player, "<red>You lost your duel!");
        MessageUtils.sendMessage(target, "<green>You won your duel!");

        Duelist targetDuelist = DuelistManager.getDuelist(duelist.getTarget());
        targetDuelist.setTarget(null);
        duelist.setTarget(null);
        duelist.getTask().cancel();
        duelist.setTask(null);
        targetDuelist.setTask(null);
        duelist.setLastDuelEnded(Misc.currentTimeSeconds());
        targetDuelist.setLastDuelEnded(Misc.currentTimeSeconds());
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
