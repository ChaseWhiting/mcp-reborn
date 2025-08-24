
package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;


public class WeatheringCopperStairBlock
extends StairsBlock
implements WeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;


    public WeatheringCopperStairBlock(WeatheringCopper.WeatherState weatherState, BlockState blockState, Properties properties) {
        super(blockState, properties);
        this.weatherState = weatherState;
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld serverLevel, BlockPos blockPos, Random randomSource) {
        this.changeOverTime(blockState, serverLevel, blockPos, randomSource);
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return WeatheringCopper.getNext(blockState.getBlock()).isPresent();
    }

    @Override
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }


}

