package net.minecraft.item.food;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.sound.SoundCategoryWrapper;
import net.minecraft.util.sound.SoundWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public record FoodData(int nutrition,
                       float saturation,
                       boolean isMeat,
                       boolean canAlwaysEat,
                       boolean isFast,
                       int timeToEat,
                       SoundWrapper doneEating,
                       SoundWrapper eating,
                       List<EffectWithChance> effects) {

    public static final Logger LOGGER = LogManager.getLogger();

    public static FoodData generic() {
        return new FoodData(1, 0.1F, false, false, false, 32,
                SoundWrapper.of(SoundCategoryWrapper.of(SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS)),
                SoundWrapper.of(SoundCategoryWrapper.of(SoundEvents.GENERIC_EAT, SoundCategory.PLAYERS)),
                List.of());
    }

    public static final Codec<FoodData> CODEC = ExtraCodecs.lazyInitialized(() -> {
       return RecordCodecBuilder.create(instance ->
               instance.group(
                       Codec.INT.optionalFieldOf("nutrition", 1)
                               .forGetter(FoodData::nutrition),
                       Codec.FLOAT.optionalFieldOf("saturation", 0.1F).forGetter(FoodData::saturation),
                       Codec.BOOL.optionalFieldOf("meat", false).forGetter(FoodData::isMeat),
                       Codec.BOOL.optionalFieldOf("alwaysEat", false).forGetter(FoodData::canAlwaysEat),
                       Codec.BOOL.optionalFieldOf("fast", false).forGetter(FoodData::isFast),
                       Codec.INT.optionalFieldOf("timeToConsume", 32).forGetter(FoodData::timeToEat),
                       SoundWrapper.CODEC.optionalFieldOf("doneEating",
                               SoundWrapper.of(SoundCategoryWrapper.of(SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS)))
                               .forGetter(FoodData::doneEating),
                       SoundWrapper.CODEC.optionalFieldOf("eating",
                                       SoundWrapper.of(SoundCategoryWrapper.of(SoundEvents.GENERIC_EAT, SoundCategory.PLAYERS)))
                               .forGetter(FoodData::eating),
                       EffectWithChance.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(FoodData::effects)).apply(instance, FoodData::new));
    });

    @Nullable
    public static CompoundNBT save(FoodData data) {
        CompoundNBT nbt = new CompoundNBT();

        FoodData.CODEC.encodeStart(NBTDynamicOps.INSTANCE, data).resultOrPartial(LOGGER::error).ifPresentOrElse(foodDt -> nbt.put("FoodData", foodDt), () -> {

        });

        if (nbt.contains("FoodData")) {
            return nbt;
        }

        return null;
    }

    @Nullable
    public static FoodData load(CompoundNBT nbt) {
        if (nbt.contains("FoodData")) {
            DataResult<FoodData> data = FoodData.CODEC.parse(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt.get("FoodData")));
            AtomicReference<FoodData> fd = new AtomicReference<>();
            fd.set(null);
            data.resultOrPartial(LOGGER::error).ifPresent(fd::set);

            if (fd.get() != null) {
                return fd.get();
            }

        }

        return null;
    }
}
