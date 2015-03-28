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
package com.tealcube.minecraft.bukkit.highnoon.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public final class Misc {

    public static final long TICKS_PER_SEC = 20L;
    public static final long MILLIS_PER_SEC = 1000L;

    private Misc() {
        // do nothing
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

}
