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
package com.tealcube.minecraft.bukkit.highnoon.storage;

import com.tealcube.minecraft.bukkit.facecore.logging.PluginLogger;
import com.tealcube.minecraft.bukkit.facecore.utilities.IOUtils;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.highnoon.data.DuelResult;
import com.tealcube.minecraft.bukkit.kern.io.CloseableRegistry;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.collect.HashBasedTable;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.collect.Table;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

public class SqliteStorage {

    private static final String HN_DUELISTS_CREATE = "CREATE TABLE IF NOT EXISTS hn_duelists (duelist TEXT PRIMARY KEY, wins INTEGER NOT NULL, " +
            "losses NOT NULL, ties NOT NULL)";
    private final PluginLogger logger;
    private boolean initialized;
    private HighNoonPlugin plugin;
    private File file;

    public SqliteStorage(HighNoonPlugin plugin) {
        this.plugin = plugin;
        this.logger = new PluginLogger(new File(plugin.getDataFolder(), "logs/sqlite.log"));
        this.initialized = false;
        IOUtils.createDirectory(new File(plugin.getDataFolder(), "db"));
        this.file = new File(plugin.getDataFolder(), "db/tribes.db");
    }

    private void createTable() throws SQLException {
        CloseableRegistry registry = new CloseableRegistry();
        Connection connection = registry.register(getConnection());

        if (connection == null) {
            return;
        }

        Statement statement = registry.register(connection.createStatement());
        statement.executeUpdate(HN_DUELISTS_CREATE);

        registry.closeQuietly();
    }

    private boolean tryQuery(Connection c, String query) {
        CloseableRegistry registry = new CloseableRegistry();
        try {
            Statement statement = registry.register(c.createStatement());
            if (statement != null) {
                statement.executeQuery(query);
            }
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            registry.closeQuietly();
        }
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        try {
            createTable();
            initialized = true;
            plugin.getPluginLogger().log(Level.INFO, "sqlite initialized");
        } catch (SQLException ex) {
            plugin.getPluginLogger().log(Level.INFO, "unable to setup sqlite");
        }
    }

    public void shutdown() {
        // don't do anything
    }

    public Table<UUID, DuelResult, Integer> loadDuelResults(UUID... uuids) {
        Table<UUID, DuelResult, Integer> table = HashBasedTable.create();
        String selectStatement = "SELECT * FROM hn_duelists WHERE duelist=? LIMIT 1";
        CloseableRegistry registry = new CloseableRegistry();
        try {
            Connection connection = registry.register(getConnection());
            PreparedStatement statement = registry.register(connection.prepareStatement(selectStatement));
            for (UUID uuid : uuids) {
                statement.setString(1, uuid.toString());
                ResultSet rs = registry.register(statement.executeQuery());
                table.put(uuid, DuelResult.WIN, rs.getInt("wins"));
                table.put(uuid, DuelResult.LOSS, rs.getInt("losses"));
                table.put(uuid, DuelResult.TIE, rs.getInt("ties"));
            }
        } catch (Exception e) {
            logger.log("unable to load duel results: " + e.getMessage());
        } finally {
            registry.closeQuietly();
        }
        return table;
    }

    public Table<UUID, DuelResult, Integer> loadDuelResults() {
        Table<UUID, DuelResult, Integer> table = HashBasedTable.create();
        String selectStatement = "SELECT * FROM hn_duelists";
        CloseableRegistry registry = new CloseableRegistry();
        try {
            Connection connection = registry.register(getConnection());
            PreparedStatement statement = registry.register(connection.prepareStatement(selectStatement));
            ResultSet rs = registry.register(statement.executeQuery());
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("duelist"));
                table.put(uuid, DuelResult.WIN, rs.getInt("wins"));
                table.put(uuid, DuelResult.LOSS, rs.getInt("losses"));
                table.put(uuid, DuelResult.TIE, rs.getInt("ties"));
            }
        } catch (Exception e) {
            logger.log("unable to load duel results: " + e.getMessage());
        } finally {
            registry.closeQuietly();
        }
        return table;
    }

    public void saveDuelResults(Table<UUID, DuelResult, Integer> table) {
        String query = "REPLACE INTO hn_duelists (duelist, wins, losses, ties) VALUES (?,?,?,?)";
        CloseableRegistry registry = new CloseableRegistry();
        try {
            Connection c = registry.register(getConnection());
            PreparedStatement statement = registry.register(c.prepareStatement(query));
            for (UUID uuid : table.rowKeySet()) {
                statement.setString(1, uuid.toString());
                statement.setInt(2, table.get(uuid, DuelResult.WIN));
                statement.setInt(3, table.get(uuid, DuelResult.LOSS));
                statement.setInt(4, table.get(uuid, DuelResult.TIE));
                statement.executeUpdate();
            }
        } catch (Exception e) {
            logger.log("unable to save duel results: " + e.getMessage());
        } finally {
            registry.closeQuietly();
        }
    }

    private String getConnectionURI() {
        return "jdbc:sqlite:" + file.getAbsolutePath();
    }

    private Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            return null;
        }
        try {
            return DriverManager.getConnection(getConnectionURI());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
