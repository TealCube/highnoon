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
package com.tealcube.minecraft.bukkit.highnoon.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public final class Misc {

    public static final long TICKS_PER_SEC = 20L;
    public static final long MILLIS_PER_SEC = 1000L;

    private Misc() {
        // do nothing
    }

    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / MILLIS_PER_SEC;
    }

    public static boolean isPastDistance(Location a, Location b, double distance) {
        double dist = a.distanceSquared(b);
        return dist > (distance * distance);
    }

    public static boolean isWithinDistance(Location a, Location b, double distance) {
        return !isPastDistance(a, b, distance);
    }

    public static Location calculateMidpoint(Location a, Location b) {
        int midX = (a.getBlockX() + b.getBlockX()) / 2;
        int midY = (a.getBlockY() + b.getBlockY()) / 2;
        int midZ = (a.getBlockZ() + b.getBlockZ()) / 2;

        World world = a.getWorld();

        Block block = world.getBlockAt(midX, midY, midZ);
        while (block.getType() != Material.AIR) {
            block = block.getRelative(BlockFace.UP);
        }

        return block.getLocation();
    }

    public static Vector calculateKnockbackVector(Location first, Location second) {
        double dX = first.getX() - second.getX();
        double dY = first.getY() - second.getY();
        double dZ = first.getZ() - second.getZ();

        double yaw = Math.atan2(dZ, dX);

        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;

        double X = Math.sin(pitch) * Math.cos(yaw);
        double Y = Math.sin(pitch) * Math.sin(yaw);
        double Z = Math.cos(pitch);

        return new Vector(X, Z, Y);
    }

    public static boolean hasTimePassed(long point, long seconds) {
        return currentTimeSeconds() - point >= seconds;
    }

    public static Set<Location> sphere(Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        Set<Location> circleblocks = new HashSet<Location>();
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
