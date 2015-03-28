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
package com.tealcube.minecraft.bukkit.highnoon.data.player;

import com.tealcube.minecraft.bukkit.highnoon.data.DuelInvitationKey;
import com.tealcube.minecraft.bukkit.highnoon.data.LevelExpKey;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public final class Duelist {

    private final UUID uuid;

    private Location arena;
    private UUID duelPartner;
    private boolean occupied;
    private long lastChallengeSent;
    private long lastChallengedReceived;
    private DuelInvitationKey duelInvitationKey;
    private BukkitTask duelEndTask;
    private List<ItemStack> savedInventoryItems;
    private List<ItemStack> savedInventoryArmor;
    private LevelExpKey savedLevel;
    private boolean duelRespawn;

    public Duelist(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getArena() {
        return arena;
    }

    public void setArena(Location arena) {
        this.arena = arena;
    }

    public UUID getDuelPartner() {
        return duelPartner;
    }

    public void setDuelPartner(UUID duelPartner) {
        this.duelPartner = duelPartner;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public long getLastChallengeSent() {
        return lastChallengeSent;
    }

    public void setLastChallengeSent(long lastChallengeSent) {
        this.lastChallengeSent = lastChallengeSent;
    }

    public long getLastChallengedReceived() {
        return lastChallengedReceived;
    }

    public void setLastChallengedReceived(long lastChallengedReceived) {
        this.lastChallengedReceived = lastChallengedReceived;
    }

    public DuelInvitationKey getDuelInvitationKey() {
        return duelInvitationKey;
    }

    public void setDuelInvitationKey(DuelInvitationKey duelInvitationKey) {
        this.duelInvitationKey = duelInvitationKey;
    }

    public BukkitTask getDuelEndTask() {
        return duelEndTask;
    }

    public void setDuelEndTask(BukkitTask duelEndTask) {
        this.duelEndTask = duelEndTask;
    }

    public List<ItemStack> getSavedInventoryItems() {
        return savedInventoryItems;
    }

    public void setSavedInventoryItems(List<ItemStack> savedInventoryItems) {
        this.savedInventoryItems = savedInventoryItems;
    }

    public List<ItemStack> getSavedInventoryArmor() {
        return savedInventoryArmor;
    }

    public void setSavedInventoryArmor(List<ItemStack> savedInventoryArmor) {
        this.savedInventoryArmor = savedInventoryArmor;
    }

    public LevelExpKey getSavedLevel() {
        return savedLevel;
    }

    public void setSavedLevel(LevelExpKey savedLevel) {
        this.savedLevel = savedLevel;
    }

    public boolean isDuelRespawn() {
        return duelRespawn;
    }

    public void setDuelRespawn(boolean duelRespawn) {
        this.duelRespawn = duelRespawn;
    }

    public void actualizeLastChallengeReceived() {
        setLastChallengedReceived(System.currentTimeMillis());
    }

}
