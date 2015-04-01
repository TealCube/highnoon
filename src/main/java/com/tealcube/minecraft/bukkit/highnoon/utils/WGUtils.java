package com.tealcube.minecraft.bukkit.highnoon.utils;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import org.bukkit.Location;

public final class WGUtils {

    private WGUtils() {
        // do nothing
    }

    public static boolean inRegion(Location location) {
        ApplicableRegionSet regionSet = HighNoonPlugin.getInstance().getWorldGuardPlugin().getRegionManager(location.getWorld())
                .getApplicableRegions(location);
        return regionSet.size() != 0;
    }

}
