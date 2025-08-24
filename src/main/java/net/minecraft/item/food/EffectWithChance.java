package net.minecraft.item.food;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.potion.EffectInstance;

public record EffectWithChance(EffectInstance effect, float probability) {

    public static final Codec<EffectWithChance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EffectInstance.CODEC.fieldOf("effect").forGetter(EffectWithChance::effect),
            Codec.FLOAT.fieldOf("probability").orElse(1.0F).forGetter(EffectWithChance::probability)
    ).apply(instance, EffectWithChance::new));


}
