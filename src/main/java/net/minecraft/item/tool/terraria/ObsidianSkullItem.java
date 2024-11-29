package net.minecraft.item.tool.terraria;

import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class ObsidianSkullItem extends AccessoryItem {

    public ObsidianSkullItem() {
        super(new Properties().durability(1895), Rarity.GREEN);
    }

    @Override
    public boolean isAccessoryActive(ItemStack stack, PlayerEntity player) {
        return player.getFeetBlockState().is(Blocks.MAGMA_BLOCK) || player.getFeetBlockState().getBlock() instanceof CampfireBlock;
    }

    @Override
    protected void onAccessoryActivated(ItemStack stack, PlayerEntity player) {
    }

    @Override
    protected void onAccessoryDeactivated(ItemStack stack, PlayerEntity player) {
    }

    @Override
    protected void applyAccessoryEffect(ItemStack stack, World world, PlayerEntity player) {
        if (!player.hasEffect(Effects.FIRE_RESISTANCE) && !player.isInLava()) {
            player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 40, 0));
            stack.hurt(1, player);
        }
    }

    @Override
    protected void applyStatBoosts (PlayerEntity player){
    }
}
