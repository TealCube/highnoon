package com.tealcube.minecraft.bukkit.highnoon.data;

import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Objects;

import java.util.UUID;

public class Duelist {

    private final UUID uniqueId;
    private UUID target;

    public Duelist(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Duelist)) {
            return false;
        }
        Duelist duelist = (Duelist) o;
        return Objects.equal(getUniqueId(), duelist.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUniqueId());
    }

}
