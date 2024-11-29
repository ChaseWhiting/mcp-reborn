package net.minecraft.item.tool.terraria;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AnkhShieldItem extends AccessoryItem {

    public AnkhShieldItem() {
        super(new Properties().durability(1650), Rarity.GREEN);
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
        List<EffectInstance> effectsToRemove = new ArrayList<>();

        for (EffectInstance effectInstance : player.getActiveEffects()) {
            if (!effectInstance.getEffect().isBeneficial()) {
                effectsToRemove.add(effectInstance);
            }
        }

        for (EffectInstance effectInstance : effectsToRemove) {
            player.removeEffect(effectInstance.getEffect());
            stack.hurt(1, player);
        }
    }

    @Override
    protected void applyStatBoosts(PlayerEntity player) {
    }
}
