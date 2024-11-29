package net.minecraft.item.tool.terraria;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;

public class RegenerationBandItem extends AccessoryItem {

    public RegenerationBandItem() {
        super(new Properties().durability(2340), Rarity.BLUE);
    }

    @Override
    public boolean isAccessoryActive(ItemStack stack, PlayerEntity player) {
        return player.tick(40);
    }

    @Override
    protected void onAccessoryActivated(ItemStack stack, PlayerEntity player) {
    }

    @Override
    protected void onAccessoryDeactivated(ItemStack stack, PlayerEntity player) {
    }

    @Override
    protected void applyAccessoryEffect(ItemStack stack, World world, PlayerEntity player) {
        if (player.getHealth() < player.getMaxHealth()) {
            player.heal(0.5F);
            stack.hurt(1, player);
        }

    }

    @Override
    protected void applyStatBoosts (PlayerEntity player){
    }
}
