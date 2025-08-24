package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

public interface RuleBlockEntityModifier {
    public static final Codec<RuleBlockEntityModifier> CODEC = ExtraCodecs.lazyInitialized(() -> Registry.RULE_BLOCK_ENTITY_MODIFIER.byNameCodec().dispatch(RuleBlockEntityModifier::getType, RuleBlockEntityModifierType::codec));

    @Nullable
    public CompoundNBT apply(RandomSource var1, @Nullable CompoundNBT var2);

    public RuleBlockEntityModifierType<?> getType();
}
