package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.random.RandomSource;

import javax.annotation.Nullable;

public class AppendStatic
implements RuleBlockEntityModifier {
    public static final Codec<AppendStatic> CODEC = RecordCodecBuilder.create(instance -> instance.group(CompoundNBT.CODEC.fieldOf("data").forGetter(appendStatic -> appendStatic.tag)).apply(instance, AppendStatic::new));
    private final CompoundNBT tag;

    public AppendStatic(CompoundNBT compoundTag) {
        this.tag = compoundTag;
    }

    @Override
    public CompoundNBT apply(RandomSource randomSource, @Nullable CompoundNBT compoundTag) {
        return compoundTag == null ? this.tag.copy() : compoundTag.merge(this.tag);
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return RuleBlockEntityModifierType.APPEND_STATIC;
    }
}

