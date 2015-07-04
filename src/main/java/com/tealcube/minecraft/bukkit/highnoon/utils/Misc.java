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
