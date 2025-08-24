package net.minecraft.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Holder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;

public class Instruments {
    public static final Instrument PONDER_GOAT_HORN = registerNewGoatHorn(SoundEvents.GOAT_HORN_PONDER, "ponder_goat_horn");
    public static final Instrument SING_GOAT_HORN = registerNewGoatHorn(SoundEvents.GOAT_HORN_SING, "sing_goat_horn");
    public static final Instrument SEEK_GOAT_HORN = registerNewGoatHorn(SoundEvents.GOAT_HORN_SEEK, "seek_goat_horn");
    public static final Instrument FEEL_GOAT_HORN = registerNewGoatHorn(SoundEvents.GOAT_HORN_FEEL, "feel_goat_horn");
    public static final Instrument ADMIRE_GOAT_HORN = registerNewGoatHorn(SoundEvents.GOAT_HORN_ADMIRE, "admire_goat_horn");
    public static final Instrument CALL_GOAT_HORN = registerNewGoatHorn(SoundEvents.GOAT_HORN_CALL, "call_goat_horn");
    public static final Instrument YEARN_GOAT_HORN = registerNewGoatHorn(SoundEvents.GOAT_HORN_YEARN, "yearn_goat_horn");
    public static final Instrument DREAM_GOAT_HORN = registerNewGoatHorn(SoundEvents.GOAT_HORN_DREAM, "dream_goat_horn");



    public static Instrument registerNewGoatHorn(SoundEvent soundEvent, String id) {
        Instrument horn = new Instrument(Holder.of(soundEvent), 140, 256.0F);
        return register(horn, id);
    }

    public static Instrument register(Instrument instrument, String id) {
        return Registry.register(Registry.INSTRUMENT, new ResourceLocation(id), instrument);
    }

    public static Instrument getFromName(String name) {
        return Registry.INSTRUMENT.getOptional(new ResourceLocation(name)).orElse(PONDER_GOAT_HORN);
    }

    public static void saveInstrumentToTag(ItemStack item, Instrument instrument) {
        String name = Registry.INSTRUMENT.getKey(instrument).toString();


        CompoundNBT nbt = item.getOrCreateTag();
        nbt.putString("Instrument", name);
    }
}
