package net.minecraft.block;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;


public class CopperButtonBlock
extends AbstractButtonBlock {
    private final WeatheringCopper.WeatherState weatherState;

    public CopperButtonBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
        super(false, properties);
        this.weatherState = weatherState;
    }

    @Override
    protected SoundEvent getSound(boolean p_196369_1_) {
        return p_196369_1_ ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF;
    }

    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }
}

