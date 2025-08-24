package net.minecraft.item.tool;

import net.minecraft.enchantment.IVanishable;

public abstract class TerrariaBowItem extends SimpleShootableBowItem implements IVanishable {
    protected final float velocity;

    public TerrariaBowItem(float velocity, Properties properties) {
        super(properties);
        this.velocity = velocity;
    }

    public float getVelocity() {
        return this.velocity;
    }

    public float getVelocityInMCScale(float power) {
        return power * ((velocity / 6.6F) * 3.0F);
    }

    public static float getVelocityInMCScale(float power, float velocity) {
        return power * ((velocity / 6.6F) * 3.0F);
    }

}
