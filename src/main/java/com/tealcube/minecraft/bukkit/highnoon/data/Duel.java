package com.tealcube.minecraft.bukkit.highnoon.data;

import com.google.common.base.Objects;

import java.util.UUID;

public class Duel {

    private final UUID uniqueId;
    private final UUID challenger;
    private final UUID target;
    private UUID winner;
    private UUID loser;

    public Duel(UUID challenger, UUID target) {
        this.uniqueId = UUID.randomUUID();
        this.challenger = challenger;
        this.target = target;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getChallenger() {
        return challenger;
    }

    public UUID getTarget() {
        return target;
    }

    public UUID getWinner() {
        return winner;
    }

    public void setWinner(UUID winner) {
        this.winner = winner;
    }

    public UUID getLoser() {
        return loser;
    }

    public void setLoser(UUID loser) {
        this.loser = loser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Duel)) {
            return false;
        }
        Duel duel = (Duel) o;
        return Objects.equal(getUniqueId(), duel.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUniqueId());
    }
}
