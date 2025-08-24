package net.minecraft.block.sensor;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.entity.warden.event.GameEventListener;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SculkSensorBlock extends ContainerBlock {
    public static final EnumProperty<SculkSensorPhase> PHASE = BlockStateProperties.SCULK_SENSOR_PHASE;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final int listenerRange;

    public SculkSensorBlock(AbstractBlock.Properties properties, int range) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PHASE, SculkSensorPhase.INACTIVE).setValue(POWER, 0).setValue(WATERLOGGED, false));
        this.listenerRange = range;
    }

    public int getListenerRange() {
        return this.listenerRange;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos position, Random random) {
        if (getPhase(state) != SculkSensorPhase.ACTIVE) {
            if (getPhase(state) == SculkSensorPhase.COOLDOWN) {
                world.setBlock(position, state.setValue(PHASE, SculkSensorPhase.INACTIVE), 3);
            }
            return;
        }
        deactivate(world, position, state);
    }

    @Override
    public void stepOn(World world, BlockPos pos, Entity entity) {
        TileEntity tileEntity;
        BlockState state = world.getBlockState(pos);
        if (!world.isClientSide && canActivate(state) && entity.getType() != EntityType.WARDEN && (tileEntity = world.getBlockEntity(pos)) instanceof SculkSensorBlockEntity) {
            SculkSensorBlockEntity sculkSensorBlockEntity = (SculkSensorBlockEntity)tileEntity;
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) world;
                sculkSensorBlockEntity.getListener().forceGameEvent(serverWorld, GameEvent.STEP, GameEvent.Context.of(entity), entity.position());
            }
        }
        super.stepOn(world, pos, entity);
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState state2, boolean b) {
        if (world.isClientSide || state.is(state2.getBlock())) {
            return;
        }
        if (state.getValue(POWER) > 0 && !world.getBlockTicks().hasScheduledTick(pos, this)) {
            world.setBlock(pos, state.setValue(POWER, 0), 18);
        }
        world.getBlockTicks().scheduleTick(new BlockPos(pos), state.getBlock(), 1);
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState state2, boolean bl) {
        if (state.is(state2.getBlock())) return;

        if (getPhase(state) == SculkSensorPhase.ACTIVE) {
            updateNeighbours(world, pos);
        }
        super.onRemove(state, world, pos, state2, bl);
    }



    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, IWorld levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            levelAccessor.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    private static void updateNeighbours(World level, BlockPos blockPos) {
        level.updateNeighborsAt(blockPos, Blocks.SCULK_SENSOR);
        level.updateNeighborsAt(blockPos.relative(Direction.UP.getOpposite()), Blocks.SCULK_SENSOR);
    }

    @Override
    public @Nullable TileEntity newBlockEntity(IBlockReader reader) {
        return new SculkSensorBlockEntity();
    }

    @Override
    @Nullable
    public <T extends TileEntity> GameEventListener getListener(ServerWorld serverWorld, T t) {
        if (t instanceof SculkSensorBlockEntity) {
            return ((SculkSensorBlockEntity)t).getListener();
        }
        return null;
    }

    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    public static SculkSensorPhase getPhase(BlockState blockState) {
        return blockState.getValue(PHASE);
    }

    public static boolean canActivate(BlockState blockState) {
        return SculkSensorBlock.getPhase(blockState) == SculkSensorPhase.INACTIVE;
    }

    public static void deactivate(World level, BlockPos blockPos, BlockState blockState) {
        level.setBlock(blockPos, (BlockState)((BlockState)blockState.setValue(PHASE, SculkSensorPhase.COOLDOWN)).setValue(POWER, 0), 3);
        level.getBlockTicks().scheduleTick(blockPos, blockState.getBlock(), 1);
        if (!blockState.getValue(WATERLOGGED).booleanValue()) {
            level.playSound(null, blockPos, SoundEvents.SCULK_CLICKING_STOP, SoundCategory.BLOCKS, 1.0f, level.random.nextFloat() * 0.2f + 0.8f);
        }
        SculkSensorBlock.updateNeighbours(level, blockPos);
    }

    public static void activate(@Nullable Entity entity, World level, BlockPos blockPos, BlockState blockState, int n) {
        level.setBlock(blockPos, (BlockState)((BlockState)blockState.setValue(PHASE, SculkSensorPhase.ACTIVE)).setValue(POWER, n), 3);
        level.getBlockTicks().scheduleTick(blockPos, blockState.getBlock(), 40);
        SculkSensorBlock.updateNeighbours(level, blockPos);
        level.gameEvent(entity, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, blockPos);
        if (!blockState.getValue(WATERLOGGED).booleanValue()) {
            level.playSound(null, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, SoundEvents.SCULK_CLICKING, SoundCategory.BLOCKS, 1.0f, level.random.nextFloat() * 0.2f + 0.8f);
        }
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos blockPos, Random randomSource) {
        if (getPhase(state) != SculkSensorPhase.ACTIVE) return;

        Direction direction = Direction.getRandom(randomSource);
        if (direction == Direction.UP || direction == Direction.DOWN) return;

        double d = (double)blockPos.getX() + 0.5 + (direction.getStepX() == 0 ? 0.5 - randomSource.nextDouble() : (double)direction.getStepX() * 0.6);
        double d2 = (double)blockPos.getY() + 0.25;
        double d3 = (double)blockPos.getZ() + 0.5 + (direction.getStepZ() == 0 ? 0.5 - randomSource.nextDouble() : (double)direction.getStepZ() * 0.6);
        double d4 = (double)randomSource.nextFloat() * 0.04;
        world.addParticle(ParticleTypes.SOUL, d, d2, d3, 0.0, d4, 0.0);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PHASE, POWER, WATERLOGGED);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World level, BlockPos blockPos) {
        TileEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof SculkSensorBlockEntity) {
            SculkSensorBlockEntity sculkSensorBlockEntity = (SculkSensorBlockEntity)blockEntity;
            return SculkSensorBlock.getPhase(blockState) == SculkSensorPhase.ACTIVE ? sculkSensorBlockEntity.getLastVibrationFrequency() : 0;
        }
        return 0;
    }

    @Override
    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
        return true;
    }

    @Override
    public void spawnAfterBreak(BlockState p_220062_1_, ServerWorld p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
        super.spawnAfterBreak(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);
        popExperience(p_220062_2_, p_220062_3_, 5);
    }
}
