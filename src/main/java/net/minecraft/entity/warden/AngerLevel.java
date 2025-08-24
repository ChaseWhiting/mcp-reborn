/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.warden;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;

import java.util.Arrays;

public enum AngerLevel {
    CALM(0, SoundEvents.WARDEN_AMBIENT, SoundEvents.WARDEN_LISTENING),
    AGITATED(40, SoundEvents.WARDEN_AGITATED, SoundEvents.WARDEN_LISTENING_ANGRY),
    ANGRY(80, SoundEvents.WARDEN_ANGRY, SoundEvents.WARDEN_LISTENING_ANGRY);

    private static final AngerLevel[] SORTED_LEVELS;
    private final int minimumAnger;
    private final SoundEvent ambientSound;
    private final SoundEvent listeningSound;

    private AngerLevel(int n2, SoundEvent soundEvent, SoundEvent soundEvent2) {
        this.minimumAnger = n2;
        this.ambientSound = soundEvent;
        this.listeningSound = soundEvent2;
    }

    public int getMinimumAnger() {
        return this.minimumAnger;
    }

    public SoundEvent getAmbientSound() {
        return this.ambientSound;
    }

    public SoundEvent getListeningSound() {
        return this.listeningSound;
    }

    public static AngerLevel byAnger(int n) {
        for (AngerLevel angerLevel : SORTED_LEVELS) {
            if (n < angerLevel.minimumAnger) continue;
            return angerLevel;
        }
        return CALM;
    }

    public boolean isAngry() {
        return this == ANGRY;
    }

    static {
        SORTED_LEVELS = Util.make(AngerLevel.values(), angerLevelArray -> Arrays.sort(angerLevelArray, (angerLevel, angerLevel2) -> Integer.compare(angerLevel2.minimumAnger, angerLevel.minimumAnger)));
    }
}

