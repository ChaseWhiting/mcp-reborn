package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;


public class WeatheringCopperTrapDoorBlock
extends TrapDoorBlock
implements WeatheringCopper {
    private final WeatherState weatherState;

    public WeatheringCopperTrapDoorBlock(WeatherState weatherState, Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld serverLevel, BlockPos blockPos, Random random) {
        this.changeOverTime(blockState, serverLevel, blockPos, random);
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

