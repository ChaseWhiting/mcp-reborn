package net.minecraft.item.tool.terraria;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;

public class ObsidianRoseItem extends AccessoryItem {

    public ObsidianRoseItem() {
        super(new Properties().durability(1895), Rarity.LEGENDARY);
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
    protected void applyStatBoosts (PlayerEntity player){
    }
}
