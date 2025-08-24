package net.minecraft.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;

public interface WarmColdVariantHolder extends VariantHolder<WarmColdVariant> {


    default void setVariantOnSpawn(IServerWorld world, BlockPos pos) {
        if (world.getBiomeName(pos).isEmpty()) {
            this.setVariant(WarmColdVariant.TEMPERATE);
            return;
        }

        WarmColdVariant.setVariant(this, world.getBiomeName(pos).get());
    }

    default void putVariantToTag(CompoundNBT nbt) {
        nbt.putInt("Type", this.getVariant().getId());
    }

    default WarmColdVariant getVariantFromTag(CompoundNBT nbt) {
        return WarmColdVariant.byId(nbt.getInt("Type"));
    }
}
