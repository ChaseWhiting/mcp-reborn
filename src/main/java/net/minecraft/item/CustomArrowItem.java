package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.BoneArrowEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CustomArrowItem extends ArrowItem {
    CustomArrowType type;
    public CustomArrowItem(Item.Properties properties, CustomArrowType type) {
        super(properties);
        this.type = type;
    }



    public AbstractArrowEntity createArrow(World world, ItemStack stack, LivingEntity entity) {
        CustomArrowEntity arrowentity = new CustomArrowEntity(world, entity);
        arrowentity.setArrowType(this.type);
        return arrowentity;
    }
}