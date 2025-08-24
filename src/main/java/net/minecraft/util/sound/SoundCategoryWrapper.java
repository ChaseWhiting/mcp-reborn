package net.minecraft.util.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public record SoundCategoryWrapper(SoundEvent event, SoundCategory category) {

    public static final Codec<SoundCategoryWrapper> CODEC = ExtraCodecs.lazyInitialized(() -> RecordCodecBuilder.create(instance ->
            instance.group(SoundEvent.CODEC.fieldOf("sound").forGetter(SoundCategoryWrapper::event),
                    SoundCategory.CODEC.optionalFieldOf("category", SoundCategory.MASTER).forGetter(SoundCategoryWrapper::category)).apply(instance, SoundCategoryWrapper::of)));

    public static SoundCategoryWrapper of(SoundEvent event, SoundCategory category) {
        return new SoundCategoryWrapper(event, category);
    }

    public static SoundCategoryWrapper of(SoundEvent event) {
        return new SoundCategoryWrapper(event, SoundCategory.MASTER);
    }
}
