package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.random.RandomSource;

import javax.annotation.Nullable;

public class Clear
implements RuleBlockEntityModifier {
    private static final Clear INSTANCE = new Clear();
    public static final Codec<Clear> CODEC = Codec.unit(INSTANCE);

    @Override
    public CompoundNBT apply(RandomSource randomSource, @Nullable CompoundNBT compoundTag) {
        return new CompoundNBT();
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return RuleBlockEntityModifierType.CLEAR;
    }
}

