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
package com.tealcube.minecraft.bukkit.highnoon;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.tealcube.minecraft.bukkit.config.MasterConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedSmartYamlConfiguration;
import com.tealcube.minecraft.bukkit.facecore.logging.PluginLogger;
import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.highnoon.listeners.CombatListener;
import com.tealcube.minecraft.bukkit.highnoon.listeners.InteractListener;
import com.tealcube.minecraft.bukkit.highnoon.tasks.DuelRangeTask;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import info.faceland.q.QPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

public class HighNoonPlugin extends FacePlugin {

    private static HighNoonPlugin instance;

    private MasterConfiguration settings;
    private PluginLogger logger;
    private QPlugin qPlugin;
    private WorldGuardPlugin worldGuardPlugin;

    public static HighNoonPlugin getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;

        qPlugin = (QPlugin) getServer().getPluginManager().getPlugin("Q");
        worldGuardPlugin = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");

        logger = new PluginLogger(this);

        VersionedSmartYamlConfiguration configYAML = new VersionedSmartYamlConfiguration(new File(getDataFolder(), "config.yml"),
                getResource("config.yml"), VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            debug("Updating config.yml");
        }

        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(), this);

        settings = MasterConfiguration.loadFromFiles(configYAML);

        new DuelRangeTask().runTaskTimer(this, 0, 2 * Misc.TICKS_PER_SEC);
    }

    @Override
    public void disable() {
    }

    public MasterConfiguration getSettings() {
        return settings;
    }

    public void debug(String... messages) {
        debug(Level.INFO, messages);
    }

    public void debug(Level level, String... messages) {
        logger.log(level, Arrays.asList(messages));
    }

    public QPlugin getQPlugin() {
        return qPlugin;
    }

    public WorldGuardPlugin getWorldGuardPlugin() {
        return worldGuardPlugin;
    }

}
