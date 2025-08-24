
package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class FireflyBushBlock
extends BushBlock
implements IGrowable {
    private static final double FIREFLY_CHANCE_PER_TICK = 0.7;
    private static final double FIREFLY_HORIZONTAL_RANGE = 10.0;
    private static final double FIREFLY_VERTICAL_RANGE = 5.0;

    public FireflyBushBlock(Properties properties) {
        super(properties);
    }



    @Override
    public void animateTick(BlockState blockState, World level, BlockPos blockPos, Random randomSource) {



            if (randomSource.nextDouble() <= FIREFLY_CHANCE_PER_TICK) {
                double d = (double)blockPos.getX() + randomSource.nextDouble() * FIREFLY_HORIZONTAL_RANGE - FIREFLY_VERTICAL_RANGE;
                double d2 = (double)blockPos.getY() + randomSource.nextDouble() * FIREFLY_VERTICAL_RANGE;
                double d3 = (double)blockPos.getZ() + randomSource.nextDouble() * FIREFLY_HORIZONTAL_RANGE - FIREFLY_VERTICAL_RANGE;
                level.addParticle(ParticleTypes.FIREFLY, d, d2, d3, 0.0, 0.0, 0.0);
            }

    }


    @Override
    public boolean isValidBonemealTarget(IBlockReader levelReader, BlockPos blockPos, BlockState b, boolean a) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(World level, Random randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerWorld serverLevel, Random randomSource, BlockPos blockPos, BlockState blockState) {
        FireflyBushBlock.popResource((World)serverLevel, blockPos, new ItemStack(this));
    }
}

