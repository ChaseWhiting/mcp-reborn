package net.minecraft.item;


import net.minecraft.util.Holder;
import net.minecraft.util.SoundEvent;

public class Instrument {
    public Holder<SoundEvent> soundEvent;
    int useDuration;
    public float range;

    public Instrument(Holder<SoundEvent> soundEvent, int useDuration, float range) {
        this.soundEvent = soundEvent;
        this.useDuration = useDuration;
        this.range = range;
    }

    public Instrument(SoundEvent soundEvent, int useDuration, float range) {
        this(Holder.of(soundEvent), useDuration, range);
    }



}

