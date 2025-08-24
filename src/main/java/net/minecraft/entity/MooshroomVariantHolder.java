package net.minecraft.entity;

import net.minecraft.entity.passive.MooshroomEntity;

public interface MooshroomVariantHolder {
    void setMooshroomVariant(MooshroomEntity.Type variant);

    MooshroomEntity.Type getMooshroomVariant();



}