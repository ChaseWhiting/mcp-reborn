package net.minecraft.item.dagger;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tool.SwordItem;

public class DesolateDaggerItem extends SwordItem {
    public DesolateDaggerItem() {
        super(ItemTier.DIAMOND, 0, -2F, (new Item.Properties()).durability(360).rarity(Rarity.DEMONIC).tab(ItemGroup.TAB_COMBAT ));
    }

    public int getMaxDamage(ItemStack stack) {
        return 360;
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity hurt, LivingEntity player) {
        if (super.hurtEnemy(stack, hurt, player)) {
            int delayedLevel = stack.getEnchantmentLevel(Enchantments.IMPENDING_STAB);
            int doubleStab = stack.getEnchantmentLevel(Enchantments.DOUBLE_STAB);

            for(int i = 0; i < 1 + doubleStab; i++){
                DesolateDaggerEntity daggerEntity = EntityType.DESOLATE_DAGGER.create(player.level());
                daggerEntity.setTargetId(hurt.getId());
                daggerEntity.copyPosition(player);
                daggerEntity.setItemStack(stack);
                daggerEntity.player = ((PlayerEntity) player);
                daggerEntity.orbitFor = (delayedLevel > 0 ? 40 : 20) + player.getRandom().nextInt(10) + (doubleStab != 0  && i != 0 ? 8 + 8 * (i == 1 ? 0 : i) : 0);
                player.level().addFreshEntity(daggerEntity);



            }
            return true;
        } else {
            return false;
        }
    }

    public static int getOrbit(int doubleStab, int delayedLevel, LivingEntity player, int i) {
        int defaultOrbit = delayedLevel > 0 ? 40 : 20;

        return defaultOrbit;
    }
}