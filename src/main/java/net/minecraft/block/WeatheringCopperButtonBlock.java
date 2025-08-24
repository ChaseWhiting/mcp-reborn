package net.minecraft.block;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;


public class WeatheringCopperButtonBlock
extends CopperButtonBlock
implements WeatheringCopper {
    private final WeatherState weatherState;

    public WeatheringCopperButtonBlock(WeatherState weatherState, Properties properties) {
        super(weatherState, properties);
        this.weatherState = weatherState;
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld serverLevel, BlockPos blockPos, Random random) {
        this.changeOverTime(blockState, serverLevel, blockPos, random);
    }

    @Override
    protected SoundEvent getSound(boolean p_196369_1_) {
        return p_196369_1_ ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF;
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return WeatheringCopper.getNext(blockState.getBlock()).isPresent();
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }
}

