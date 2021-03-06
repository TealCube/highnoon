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
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.managers.ArenaManager;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelistManager;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class DuelRangeTask extends BukkitRunnable {

    private Map<String, Integer> outOfArenaStrikes = new HashMap<String, Integer>();

    @Override
    public void run() {
        for (Player player : DuelistManager.getDuelingPlayers()) {
            Location playerLocation = player.getLocation();
            Location arenaCenter = ArenaManager.getArena(player.getUniqueId());
            double arenaSize = HighNoonPlugin.getInstance().getSettings().getDouble("config.arena.radius");

            drawArenaCircle(arenaCenter, (int) arenaSize, Effect.MOBSPAWNER_FLAMES);

            if (!Misc.isWithinDistance(playerLocation, arenaCenter, arenaSize)) {
                if (strikePlayer(player)) {
                    playerLocation.setY(playerLocation.getY() - 8);
                    Vector knockback = Misc.calculateKnockbackVector(playerLocation, arenaCenter);

                    player.getWorld().playSound(playerLocation, Sound.FIZZ, 1F, 1F);
                    player.setVelocity(knockback.multiply(2));
                    MessageUtils.sendMessage(player, "<red>Stay inside the arena.");
                } else {
                    Player target = Bukkit.getPlayer(DuelistManager.getDuelist(player.getUniqueId()).getTarget());
                    MessageUtils.sendMessage(player, "<red>You have been teleported back to the arena.");
                    MessageUtils.sendMessage(target, "<red>Your opponent has been teleported back to the arena.");

                    player.teleport(arenaCenter);
                    player.getWorld().playSound(playerLocation, Sound.ENDERMAN_TELEPORT, 1F, 0.5F);
                }
            }
        }
    }

    private boolean strikePlayer(Player player) {
        String playerName = player.getName();
        int strikes = 0;

        if (outOfArenaStrikes.containsKey(playerName)) {
            strikes = outOfArenaStrikes.get(playerName);
            strikes++;
        }

        if (strikes >= 3) {
            outOfArenaStrikes.remove(playerName);
            return false;
        } else {
            outOfArenaStrikes.put(playerName, strikes);
            return true;
        }
    }

    private void drawArenaCircle(Location loc, int r, Effect effect) {
        Set<Location> circleBlocks = Misc.sphere(loc, r, 2, true, false, 0);
        World world = loc.getWorld();

        int i = 0;
        for (Location location : circleBlocks) {
            if (++i % 2 == 0) {
                world.playEffect(location, effect, 1);
            }
        }
    }

}
