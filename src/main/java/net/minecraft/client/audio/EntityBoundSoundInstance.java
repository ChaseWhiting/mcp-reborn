package net.minecraft.client.audio;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class EntityBoundSoundInstance
extends TickableSound {
    private final Entity entity;

    public EntityBoundSoundInstance(SoundEvent soundEvent, SoundCategory soundSource, float f, float f2, Entity entity, long l) {
        super(soundEvent, soundSource);
        this.volume = f;
        this.pitch = f2;
        this.entity = entity;
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }

    public EntityBoundSoundInstance(SoundEvent soundEvent, SoundCategory soundSource, float f, float f2, Entity entity) {
        super(soundEvent, soundSource);
        this.volume = f;
        this.pitch = f2;
        this.entity = entity;
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }

    @Override
    public boolean canPlaySound() {
        return !this.entity.isSilent();
    }

    @Override
    public void tick() {
        if (this.entity.removed) {
            this.stop();
            return;
        }
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }

    public void stopSound() {
        this.stop();
    }

    public void setVolume(float f) {
        this.volume = f;
    }
}

