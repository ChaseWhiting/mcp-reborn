package net.minecraft.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class EntityBuilder<T extends LivingEntity, W extends World> {
    private final T entity;
    private final W world;

    public EntityBuilder(EntityType<T> entityType, W world) {
        this.world = world;
        this.entity = entityType.create(world);
    }

    public EntityBuilder<T, W> setAttribute(Attribute attribute, double value) {
        if (entity.getAttribute(attribute) != null) {
            entity.getAttribute(attribute).setBaseValue(value);
        }
        return this;
    }

    public EntityBuilder<T, W> setHealth(Float health) {
        entity.setHealth(health);
        return this;
    }

    public EntityBuilder<T, W> addEffect(EffectInstance effect) {
        entity.addEffect(effect);
        return this;
    }

    public EntityBuilder<T, W> addGoal(int priority, Goal goal) {
        if (entity instanceof Mob) {
            ((Mob) entity).getGoalSelector().addGoal(priority, goal);
        }
        return this;
    }

    public EntityBuilder<T, W> setCustomName(String name) {
        entity.setCustomName(new StringTextComponent(name));
        entity.setCustomNameVisible(true);
        return this;
    }

    public EntityBuilder<T, W> setItem(EquipmentSlotType slot, Item item) {
        ItemStack stack = new ItemStack(item, 1);
        entity.setItemSlot(slot, stack);
        return this;
    }

    public EntityBuilder<T, W> setPos(BlockPos pos) {
        entity.setPos(pos.getX(), pos.getY(), pos.getZ());
        return this;
    }

    public EntityBuilder<T, W> setPos(double x, double y, double z) {
        entity.setPos(x, y, z);
        return this;
    }

    public T build() {
        return entity;
    }
}
