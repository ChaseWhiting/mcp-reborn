package net.minecraft.item.tool.terraria;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;

public class SimpleAccessoryItem extends AccessoryItem {

    public SimpleAccessoryItem(int durability, Rarity rarity) {
        super(new Properties().durability(durability), rarity);
    }

    @Override
    public boolean isAccessoryActive(ItemStack stack, PlayerEntity player) {
        return true;
    }

    @Override
    protected void onAccessoryActivated(ItemStack stack, PlayerEntity player) {

    }

    @Override
    protected void onAccessoryDeactivated(ItemStack stack, PlayerEntity player) {

    }

    @Override
    protected void applyAccessoryEffect(ItemStack stack, World world, PlayerEntity player) {

    }

    @Override
    protected void applyStatBoosts(PlayerEntity player) {

    }
}