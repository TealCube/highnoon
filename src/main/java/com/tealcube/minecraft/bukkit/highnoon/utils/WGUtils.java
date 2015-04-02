package com.tealcube.minecraft.bukkit.highnoon.utils;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import org.bukkit.Location;

public final class WGUtils {

    private WGUtils() {
        // do nothing
    }

    public static boolean inRegion(Location location) {
        Vector vector = BukkitUtil.toVector(location);
        WorldGuardPlugin plugin = HighNoonPlugin.getInstance().getWorldGuardPlugin();
        RegionContainer regionContainer = plugin.getRegionContainer();
        RegionManager manager = regionContainer.get(location.getWorld());
        if (manager == null) {
            return false;
        }
        ApplicableRegionSet regionSet = manager.getApplicableRegions(vector);
        return regionSet.size() != 0;
    }

}
