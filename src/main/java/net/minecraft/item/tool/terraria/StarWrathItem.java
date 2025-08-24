package net.minecraft.item.tool.terraria;

import net.minecraft.item.*;

public class StarWrathItem extends StarfuryItem {

    public StarWrathItem() {
        super(ItemTier.STARFURY, 11, -1.6F, ItemTier.STARFURY.getUses() * 2, Rarity.REDD);
    }

    @Override
    public double getMaxRayTraceDistance() {
        return 45d;
    }

    public int getProjectileCount() {
        return 3;
    }

    @Override
    public float getKnockbackPower() {
        return 0.2f;
    }

    @Override
    public float getVerticalKnockbackPower() {
        return 0.4f;
    }

    public Item getItem() {
        return Items.STAR_WRATH;
    }

    public Item getVisibleItem() {
        return Items.PURPLE_STAR;
    }

    public int getCooldownTime() {
        return 6;
    }

    public float getProjectileSpeed() {
        return 2.3f;
    }
}