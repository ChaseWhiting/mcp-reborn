package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.random.RandomSource;

import javax.annotation.Nullable;

public class Passthrough
implements RuleBlockEntityModifier {
    public static final Passthrough INSTANCE = new Passthrough();
    public static final Codec<Passthrough> CODEC = Codec.unit(INSTANCE);

    @Override
    @Nullable
    public CompoundNBT apply(RandomSource randomSource, @Nullable CompoundNBT compoundTag) {
        return compoundTag;
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return RuleBlockEntityModifierType.PASSTHROUGH;
    }
}

