package net.minecraft.entity.ai.goal;

import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.Holder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class RaidHornGoal<T extends AbstractRaiderEntity> extends Goal {
    private final T raider;
    private boolean started = false;
    private boolean startedRaid = false;

    public RaidHornGoal(T raider) {
        this.raider = raider;
    }

    public boolean canUse() {
        World level = raider.level;

        return level.isServerSide &&
                ((ServerWorld)level).isCloseToVillage(raider.blockPosition(), 1) &&
                this.raider.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty() &&
                ((ServerWorld)level).getRaidAt(raider.blockPosition()) == null;
    }

    public boolean canContinueToUse() {
        return this.raider.isUsingItem() && this.raider.getTicksUsingItem() <= 130;
    }

    public void start() {
        this.raider.setItemSlot(EquipmentSlotType.OFFHAND, InstrumentItem.create(Items.GOAT_HORN, Holder.of(Instruments.SEEK_GOAT_HORN)));
        this.raider.startUsingItem(Hand.OFF_HAND);
    }

    public void stop() {
        this.raider.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
    }

    public void tick() {
        if (this.raider.getTicksUsingItem() > 10 && !started) {
            started = true;
            ItemStack horn = raider.getOffhandItem();
            Optional<? extends Holder<Instrument>> optional = this.getInstrument(horn);
            if (optional.isPresent()) {
                Instrument instrument = optional.get().value();
                play(raider.level, raider, instrument);
            }
        }
        if (this.raider.getTicksUsingItem() > 100 && !startedRaid) {
            startedRaid = true;
            if (raider.level instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) raider.level;
                if (serverWorld.getDifficulty() == Difficulty.PEACEFUL) {
                    return;
                }

                serverWorld.getRaids().createOrExtendRaidForPatroller(raider);
            }
        }
    }

    public void play(World level, T raider, Instrument instrument) {
        SoundEvent soundEvent = instrument.soundEvent.value();
        float f = instrument.range / 16.0f;
        level.playSound(null, raider, soundEvent, SoundCategory.RECORDS, f, 1.0f);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, raider.position(), GameEvent.Context.of(raider));
    }

    private Optional<Holder<Instrument>> getInstrument(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getOrCreateTag();

        if (nbt.contains("Instrument", 8)) {
            String string = nbt.getString("Instrument");
            Instrument instrument = Instruments.getFromName(string);
            return Optional.of(Holder.of(instrument));
        }
        return Optional.empty();
    }
}
