package net.minecraft.block;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class LightningRodBlock extends RodBlock implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int ACTIVATION_TICKS = 8;
    public static final int RANGE = 128;
    private static final int SPARK_CYCLE = 200;

    @Nullable
    private BlockPattern copperGolemPattern;

    public LightningRodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, false).setValue(POWERED, false));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockItemUseContext blockUseContext) {
        FluidState fluidState = blockUseContext.getLevel().getFluidState(blockUseContext.getClickedPos());
        boolean bl = fluidState.getType() == Fluids.WATER;

        return this.defaultBlockState().setValue(FACING, blockUseContext.getClickedFace()).setValue(WATERLOGGED, bl);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, IWorld iWorld, BlockPos blockPos, BlockPos blockPos1) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            iWorld.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, 11);
        }
        return super.updateShape(blockState, direction, blockState1, iWorld, blockPos, blockPos1);
    }

    @Override
    public int getSignal(BlockState blockState, IBlockReader iBlockReader, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, IBlockReader iBlockReader, BlockPos blockPos, Direction direction) {
        if (blockState.getValue(POWERED).booleanValue() && blockState.getValue(FACING) == direction) {
            return 15;
        }
        return 0;
    }

    public void onLightningStrike(BlockState blockState, World world, BlockPos pos) {
        world.setBlock(pos, blockState.setValue(POWERED, true), 3);
        this.updateNeighbours(blockState, world, pos);
        if (!world.getBlockTicks().hasScheduledTick(pos, this)) {
            world.getBlockTicks().scheduleTick(pos, this, ACTIVATION_TICKS);
        }
        this.trySummonCopperGolem(world, pos);
    }


    private void updateNeighbours(BlockState blockState, World level, BlockPos blockPos) {
        Direction direction = ((Direction)blockState.getValue(FACING)).getOpposite();
        level.updateNeighborsAt(blockPos.relative(direction), this);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlock(pos, state.setValue(POWERED, false), 3);
        this.updateNeighbours(state, world, pos);
    }

    @Override
    public void animateTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (!world.isThundering() || world.random.nextInt(SPARK_CYCLE) > world.getGameTime() % (long) SPARK_CYCLE || blockPos.getY() != world.getHeight(Heightmap.Type.WORLD_SURFACE, blockPos.getX(), blockPos.getZ()) - 1) {
            return;
        }
        ParticleTypes.spawnParticlesAlongAxis(blockState.getValue(FACING).getAxis(), world, blockPos, 0.125, ParticleTypes.ELECTRIC_SPARK, new int[]{1, 2});
    }

    @Override
    public void onPlace(BlockState blockState, World world, BlockPos blockPos, BlockState blockState1, boolean b) {
        if (blockState.is(blockState1.getBlock())) return;

        if (blockState.getValue(POWERED).booleanValue() && !world.getBlockTicks().hasScheduledTick(blockPos, this)) {
            world.setBlock(blockPos, blockState.setValue(POWERED, false), 18);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, WATERLOGGED);
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public void destroy(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(POWERED).booleanValue()) {
            this.updateNeighbours(blockState, (World) iWorld, blockPos);
        }
    }


    private BlockPattern getOrCreateCopperGolemPattern() {
        if (copperGolemPattern == null) {
            copperGolemPattern = BlockPatternBuilder.start()
                    .aisle("L", "#")
                    .where('L', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.LIGHTNING_ROD)))
                    .where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.COPPER_BLOCK)))
                    .build();
        }
        return copperGolemPattern;
    }

    public void trySummonCopperGolem(World world, BlockPos pos) {
        BlockPattern.PatternHelper patternHelper = getOrCreateCopperGolemPattern().find(world, pos);
        if (patternHelper != null) {
            for (int i = 0; i < getOrCreateCopperGolemPattern().getHeight(); ++i) {
                CachedBlockInfo blockInfo = patternHelper.getBlock(0, i, 0);
                world.setBlock(blockInfo.getPos(), Blocks.AIR.defaultBlockState(), 2);
                world.levelEvent(2001, blockInfo.getPos(), Block.getId(blockInfo.getState()));
            }

            CopperGolemEntity copperGolem = EntityType.COPPER_GOLEM.create(world);
            if (copperGolem != null) {
                BlockPos summonPos = patternHelper.getBlock(0, 1, 0).getPos();
                copperGolem.moveTo(summonPos.getX() + 0.5D, summonPos.getY(), summonPos.getZ() + 0.5D, 0.0F, 0.0F);
                world.addFreshEntity(copperGolem);
            }
        }
    }
}
