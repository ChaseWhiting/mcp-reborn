package net.minecraft.entity;

import javax.annotation.Nullable;

public interface IBee {


    public default void setTarget(@Nullable LivingEntity target, Mob entity) {
        entity.setTarget(target);
    }
}
