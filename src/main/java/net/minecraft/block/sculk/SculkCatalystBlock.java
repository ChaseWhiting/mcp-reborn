package net.minecraft.block.sculk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DropExperienceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class SculkCatalystBlock extends DropExperienceBlock {
    public static final BooleanProperty PULSE = BlockStateProperties.BLOOM;

    public SculkCatalystBlock() {
        super(Properties.of(Material.SCULK).strength(3.0f, 3.0f).sound(SoundType.SCULK_CATALYST).lightLevel(blockState -> 6), 5);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PULSE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
       builder.add(PULSE);
    }

    public static void bloom(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, Random randomSource) {
        serverWorld.setBlock(blockPos, (BlockState)blockState.setValue(PULSE, true), 3);
        serverWorld.getBlockTicks().scheduleTick(blockPos, blockState.getBlock(), 8);
        //serverWorld.sendParticles(ParticleTypes.SCULK_SOUL, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.15, (double)blockPos.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.0);
        serverWorld.playSound(null, blockPos, SoundEvents.SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 2.0f, 0.6f + randomSource.nextFloat() * 0.4f);
    }
}
