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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Location> circleBlocks = sphere(loc, r, 1, true, false, 0);
        World world = loc.getWorld();

        for (Location location : circleBlocks) {
            world.playEffect(location, effect, 1);
        }
    }

    public static List<Location> sphere(Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

}
