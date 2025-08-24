package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.frog.TadpoleEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

public class FrogspawnBlock extends Block {

    private static final int MIN_TADPOLES_SPAWN = 2;
    private static final int MAX_TADPOLES_SPAWN = 5;
    private static final int DEFAULT_MIN_HATCH_TICK_DELAY = 3600;
    private static final int DEFAULT_MAX_HATCH_TICK_DELAY = 12000;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.5, 16.0);
    private static int minHatchTickDelay = 3600;
    private static int maxHatchTickDelay = 12000;

    public FrogspawnBlock() {
        super(AbstractBlock.Properties.of(Material.FROGSPAWN).instabreak().noOcclusion().noCollission().sound(SoundType.FROGSPAWN));
    }

    private static int getFrogspawnHatchDelay(Random randomSource) {
        return RandomUtils.nextInt(minHatchTickDelay, maxHatchTickDelay);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        return mayPlaceOn(p_196260_2_, p_196260_3_.below());
    }

    @Override
    public void tick(BlockState blockState, ServerWorld serverLevel, BlockPos blockPos, Random randomSource) {
        if (!this.canSurvive(blockState, serverLevel, blockPos)) {
            this.destroyBlock(serverLevel, blockPos);
            return;
        }
        this.hatchFrogspawn(serverLevel, blockPos, randomSource);
    }

    @Override
    public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
        p_220082_2_.getBlockTicks().scheduleTick(p_220082_3_, this, getFrogspawnHatchDelay(p_220082_2_.random));
    }

    private static boolean mayPlaceOn(IWorldReader blockGetter, BlockPos blockPos) {
        FluidState fluidState = blockGetter.getFluidState(blockPos);
        FluidState fluidState2 = blockGetter.getFluidState(blockPos.above());
        return fluidState.getType() == Fluids.WATER && fluidState2.getType() == Fluids.EMPTY;
    }

    @Override
    public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
        if (p_196262_4_.getType() == EntityType.FALLING_BLOCK) {
            this.destroyBlock(p_196262_2_, p_196262_3_);
        }
    }

    private void hatchFrogspawn(ServerWorld world, BlockPos pos, Random random) {
        this.destroyBlock(world, pos);
        world.playSound(null, pos, SoundEvents.FROGSPAWN_HATCH, SoundCategory.BLOCKS, 1f, 1f);
        spawnTadpoles(world, pos, random);
    }

    private void destroyBlock(World level, BlockPos blockPos) {
        level.destroyBlock(blockPos, false);
    }

    private void spawnTadpoles(ServerWorld world, BlockPos pos, Random random) {
        int n = RandomUtils.nextInt(2, 6);
        for (int i = 1; i <= n; i++) {
            TadpoleEntity tadpoleEntity = EntityType.TADPOLE.create(world);
            if (tadpoleEntity == null) continue;
            double d = pos.getX() + this.getRandomTadpolePositionOffset(random);
            double d2 = pos.getZ() + this.getRandomTadpolePositionOffset(random);
            int n2 = RandomUtils.nextInt(1, 361);
            tadpoleEntity.moveTo(d, pos.getY() - 0.5, d2, n2, 0f);
            tadpoleEntity.setPersistenceRequired();
            world.addFreshEntity(tadpoleEntity);
        }
    }

    private double getRandomTadpolePositionOffset(Random randomSource) {
        double d = TadpoleEntity.HITBOX_WIDTH / 2.0f;
        return MathHelper.clamp(randomSource.nextDouble(), d, 1.0 - d);
    }
}
