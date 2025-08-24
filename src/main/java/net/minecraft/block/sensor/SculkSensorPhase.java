package net.minecraft.block.sensor;

import net.minecraft.util.IStringSerializable;

public enum SculkSensorPhase implements IStringSerializable
{
    INACTIVE("inactive"),
    ACTIVE("active"),
    COOLDOWN("cooldown");

    private final String name;

    private SculkSensorPhase(String string2) {
        this.name = string2;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
