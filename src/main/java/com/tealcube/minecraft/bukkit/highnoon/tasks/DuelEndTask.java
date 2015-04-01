package com.tealcube.minecraft.bukkit.highnoon.tasks;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.highnoon.data.Duel;
import com.tealcube.minecraft.bukkit.highnoon.data.Duelist;
import com.tealcube.minecraft.bukkit.highnoon.events.DuelEndEvent;
import com.tealcube.minecraft.bukkit.highnoon.utils.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class DuelEndTask extends BukkitRunnable {

    private final Duelist a;
    private final Duelist b;

    public DuelEndTask(Duelist a, Duelist b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        DuelEndEvent ev = new DuelEndEvent(new Duel(a.getUniqueId(), b.getTarget()));
        Bukkit.getPluginManager().callEvent(ev);

        a.setTarget(null);
        b.setTarget(null);

        a.setLastDuelEnded(Misc.currentTimeSeconds());
        b.setLastDuelEnded(Misc.currentTimeSeconds());

        Player playerA = Bukkit.getPlayer(a.getUniqueId());
        Player playerB = Bukkit.getPlayer(b.getUniqueId());

        if (playerA != null && playerA.isValid()) {
            MessageUtils.sendMessage(playerA, "<red>Your duel timed out.");
        }
        if (playerB != null && playerB.isValid()) {
            MessageUtils.sendMessage(playerB, "<red>Your duel timed out.");
        }
    }

}
