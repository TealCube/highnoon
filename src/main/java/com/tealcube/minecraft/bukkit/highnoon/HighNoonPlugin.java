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
package com.tealcube.minecraft.bukkit.highnoon;

import com.tealcube.minecraft.bukkit.facecore.logging.PluginLogger;
import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.facecore.shade.config.MasterConfiguration;
import com.tealcube.minecraft.bukkit.facecore.shade.config.VersionedSmartConfiguration;
import com.tealcube.minecraft.bukkit.facecore.shade.config.VersionedSmartYamlConfiguration;
import com.tealcube.minecraft.bukkit.highnoon.data.DuelResult;
import com.tealcube.minecraft.bukkit.highnoon.storage.SqliteStorage;
import com.tealcube.minecraft.bukkit.highnoon.managers.DuelHistoryManager;
import com.tealcube.minecraft.bukkit.highnoon.tasks.DuelRangeTask;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.collect.Table;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

public class HighNoonPlugin extends FacePlugin {

    private static HighNoonPlugin instance;

    private MasterConfiguration settings;
    private PluginLogger logger;
    private SqliteStorage sqliteStorage;

    public static HighNoonPlugin getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;

        logger = new PluginLogger(this);

        VersionedSmartYamlConfiguration configYAML = new VersionedSmartYamlConfiguration(new File(getDataFolder(), "config.yml"),
                getResource("config.yml"), VersionedSmartConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            debug("Updating config.yml");
        }

        sqliteStorage = new SqliteStorage(this);

        for (Table.Cell<UUID, DuelResult, Integer> cell : sqliteStorage.loadDuelResults().cellSet()) {
            DuelHistoryManager.setDuelResultAmount(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }

        settings = MasterConfiguration.loadFromFiles(configYAML);

        new DuelRangeTask().runTaskTimer(this, 0, 2 * Misc.TICKS_PER_SEC);
    }

    @Override
    public void disable() {
        sqliteStorage.saveDuelResults(DuelHistoryManager.getDuelResults());
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

}
