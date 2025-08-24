package net.minecraft.item.tool.bow;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BowReleaseInfo {
    private final float power;
    private final boolean hasInfiniteArrows;
    private final PlayerEntity player;
    private final World level;

    private final ItemStack bowStack;
    private final ItemStack arrowStack;

    public BowReleaseInfo(float power, boolean infArrows, PlayerEntity player, World level, ItemStack bs, ItemStack as) {
        this.player = player;
        this.power = power;
        this.hasInfiniteArrows = infArrows;
        this.bowStack = bs;
        this.arrowStack = as;
        this.level = level;
    }

    public float getPower() {
        return this.power;
    }

    public boolean infiniteArrows() {
        return hasInfiniteArrows;
    }

    public PlayerEntity player() {
        return player;
    }


    public ItemStack getBowStack() {
        return bowStack;
    }

    public ItemStack getArrowStack() {
        return arrowStack;
    }

    public World getLevel() {
        return level;
    }
}
