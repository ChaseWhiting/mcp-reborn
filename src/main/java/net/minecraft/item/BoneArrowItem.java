package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.BoneArrowEntity;
import net.minecraft.world.World;

public class BoneArrowItem extends ArrowItem {
    public BoneArrowItem(Item.Properties properties) {
        super(properties);
    }

    public AbstractArrowEntity createArrow(World world, ItemStack stack, LivingEntity entity) {
        BoneArrowEntity arrowentity = new BoneArrowEntity(world, entity);
        return arrowentity;
    }
}