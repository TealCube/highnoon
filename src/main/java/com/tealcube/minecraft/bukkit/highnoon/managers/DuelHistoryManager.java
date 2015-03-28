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
package com.tealcube.minecraft.bukkit.highnoon.managers;

import com.tealcube.minecraft.bukkit.highnoon.data.DuelResult;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.collect.HashBasedTable;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.collect.Table;

import java.util.UUID;

public final class DuelHistoryManager {

    private static final Table<UUID, DuelResult, Integer> DUEL_RESULTS = HashBasedTable.create();

    private DuelHistoryManager() {
        // do nothing
    }

    public static int getDuelResultAmount(UUID uuid, DuelResult result) {
        return DUEL_RESULTS.contains(uuid, result) ? DUEL_RESULTS.get(uuid, result) : 0;
    }

    public static void setDuelResultAmount(UUID uuid, DuelResult result, int amount) {
        DUEL_RESULTS.put(uuid, result, amount);
    }

    public static Table<UUID, DuelResult, Integer> getDuelResults() {
        return DUEL_RESULTS;
    }

}
