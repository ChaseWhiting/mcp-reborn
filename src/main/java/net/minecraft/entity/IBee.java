package net.minecraft.entity;

import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public interface IBee {


    public default void setTarget(@Nullable LivingEntity target, MobEntity entity) {
        entity.setTarget(target);
    }
}
