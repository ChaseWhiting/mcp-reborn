package net.minecraft.util.sound;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;

public record SoundWrapper(SoundCategoryWrapper soundWrapper, float volume, float pitch) {

    public ResourceLocation getSoundLocation() {
        return Registry.SOUND_EVENT.getKey(this.soundWrapper().event());
    }

    public static SoundWrapper of(SoundEvent event, float volume, float pitch) {
        return new SoundWrapper(SoundCategoryWrapper.of(event), volume, pitch);
    }

    public static SoundWrapper of(SoundEvent event, SoundCategory category, float volume, float pitch) {
        return new SoundWrapper(SoundCategoryWrapper.of(event, category), volume, pitch);
    }

    public static SoundWrapper of(SoundCategoryWrapper wrapper, float volume, float pitch) {
        return new SoundWrapper(wrapper, volume, pitch);
    }

    public static SoundWrapper of(SoundCategoryWrapper wrapper) {
        return new SoundWrapper(wrapper, 1.0F, 1.0F);
    }

    public static final Codec<SoundWrapper> CODEC = ExtraCodecs.lazyInitialized(() ->
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            SoundCategoryWrapper.CODEC.fieldOf("soundWrapper").forGetter(SoundWrapper::soundWrapper),
                            Codec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(SoundWrapper::volume),
                            Codec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(SoundWrapper::pitch))
                            .apply(instance, SoundWrapper::of)));

}
