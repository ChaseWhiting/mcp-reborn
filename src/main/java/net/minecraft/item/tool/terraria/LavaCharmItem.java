package net.minecraft.item.tool.terraria;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.StarfuryStarEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.item.tool.terraria.StarfuryItem.*;

public class LavaCharmItem extends AccessoryItem {

    public LavaCharmItem() {
        super(new Properties().durability(1000), Rarity.ORANGEE);
    }

    @Override
    public boolean isAccessoryActive(ItemStack stack, PlayerEntity player) {
        boolean flag = player.getCooldowns().isOnCooldown(Items.LAVA_CHARM);

        return !flag;
    }

    @Override
    protected void onAccessoryActivated(ItemStack stack, PlayerEntity player) {
    }

    @Override
    protected void onAccessoryDeactivated(ItemStack stack, PlayerEntity player) {
    }

    @Override
    protected void applyAccessoryEffect(ItemStack stack, World world, PlayerEntity player) {
        if (player.isInLava()) {
            player.getCooldowns().addCooldown(Items.LAVA_CHARM, 27 * 20);
            player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 7 * 20, 0));
            stack.hurt(5, player);
        }
    }

    @Override
    protected void applyStatBoosts (PlayerEntity player){
    }
}
