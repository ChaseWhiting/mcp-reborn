package net.minecraft.util.random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.VisibleForTesting;

public interface PositionalRandomFactory {
    default public RandomSource at(BlockPos blockPos) {
        return this.at(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    default public RandomSource fromHashOf(ResourceLocation resourceLocation) {
        return this.fromHashOf(resourceLocation.toString());
    }

    public RandomSource fromHashOf(String var1);

    public RandomSource fromSeed(long var1);

    public RandomSource at(int var1, int var2, int var3);

    @VisibleForTesting
    public void parityConfigString(StringBuilder var1);
}
