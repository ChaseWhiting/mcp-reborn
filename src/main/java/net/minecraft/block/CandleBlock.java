package net.minecraft.block;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.dyeable.IDyeableBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.ToIntFunction;

public class CandleBlock extends AbstractCandleBlock implements IWaterLoggable, IDyeableBlock {
    public static final int MIN_CANDLES = 1;
    public static final int MAX_CANDLES = 4;
    public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;
    public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = blockState -> blockState.getValue(LIT) != false ? LIGHT_PER_CANDLE * blockState.getValue(CANDLES) : 0;
    private static final Int2ObjectMap<List<Vector3d>> PARTICLE_OFFSETS = Util.make(() -> {
        Int2ObjectOpenHashMap<List<Vector3d>> int2ObjectOpenHashMap = new Int2ObjectOpenHashMap<>();
        int2ObjectOpenHashMap.defaultReturnValue(ImmutableList.of());
        int2ObjectOpenHashMap.put(1, ImmutableList.of(
                new Vector3d(0.5, 0.5, 0.5)));
        int2ObjectOpenHashMap.put(2, ImmutableList.of(
                new Vector3d(0.375, 0.44, 0.5),
                new Vector3d(0.625, 0.5, 0.44)));
        int2ObjectOpenHashMap.put(3, ImmutableList.of(
                new Vector3d(0.5, 0.313, 0.625),
                new Vector3d(0.375, 0.44, 0.5),
                new Vector3d(0.56, 0.5, 0.44)));
        int2ObjectOpenHashMap.put(4, ImmutableList.of(
                new Vector3d(0.44, 0.313, 0.56),
                new Vector3d(0.625, 0.44, 0.56),
                new Vector3d(0.375, 0.44, 0.375),
                new Vector3d(0.56, 0.5, 0.375)));
        return Int2ObjectMaps.unmodifiable(int2ObjectOpenHashMap);
    });
    private static final VoxelShape ONE_AABB = Block.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0);
    private static final VoxelShape TWO_AABB = Block.box(5.0, 0.0, 6.0, 11.0, 6.0, 9.0);
    private static final VoxelShape THREE_AABB = Block.box(5.0, 0.0, 6.0, 10.0, 6.0, 11.0);
    private static final VoxelShape FOUR_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 10.0);


    public CandleBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState) ((BlockState) ((BlockState) ((BlockState) this.stateDefinition.any()).setValue(CANDLES, 1)).setValue(LIT, false)).setValue(WATERLOGGED, false));
    }

    @Override
    public void stepOn(World world, BlockPos pos, Entity entity) {
        if (!isLit(world.getBlockState(pos)) && !world.getBlockState(pos).getValue(WATERLOGGED) && !(entity instanceof ProjectileEntity)) {
            if (entity.isOnFire() || entity instanceof BlazeEntity blazeEntity && blazeEntity.isCharged()) {
                world.setBlock(pos, world.getBlockState(pos).setValue(LIT, true), 11);
            }
        }

        super.stepOn(world, pos, entity);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (player.abilities.mayBuild && player.getItemInHand(hand).isEmpty() && isLit(state)) {
            extinguish(player, state, world, pos);
            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext context) {
        if (!context.isSecondaryUseActive() && context.getItemInHand().getItem() == this.asItem() && state.getValue(CANDLES) < MAX_CANDLES) {
            return true;
        }

        return super.canBeReplaced(state, context);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.is(this)) {
            return state.cycle(CANDLES);
        }
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

        return super.getStateForPlacement(context).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState state2, IWorld world, BlockPos pos, BlockPos pos2) {
        if (blockState.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(blockState, direction, state2, world, pos, pos2);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }

        return super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(CANDLES)) {
            default: {
                return ONE_AABB;
            }
            case 2: {
                return TWO_AABB;
            }
            case 3: {
                return THREE_AABB;
            }
            case 4:
        }
        return FOUR_AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CANDLES, LIT, WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(WATERLOGGED) || fluidState.getType() != Fluids.WATER) {
            return false;
        }
        BlockState state2 = state.setValue(WATERLOGGED, true);
        if (isLit(state)) {
            extinguish(null, state2, world, pos);
        } else {
            world.setBlock(pos, state2, 3);
        }

        world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
        return true;
    }

    public static boolean canLight(BlockState blockState) {
        List<Block> candles = Registry.BLOCK.stream().filter(block -> block instanceof CandleBlock).toList();
        return blockState.is(candles, blockStateBase -> blockStateBase.hasProperty(LIT) && blockStateBase.hasProperty(WATERLOGGED) && blockState.getValue(LIT) == false && blockState.getValue(WATERLOGGED) == false);
    }

    @Override
    protected Iterable<Vector3d> getParticleOffsets(BlockState blockState) {
        return PARTICLE_OFFSETS.get(blockState.getValue(CANDLES).intValue());
    }

    @Override
    protected boolean canBeLit(BlockState blockState) {
        return !blockState.getValue(WATERLOGGED) && super.canBeLit(blockState);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldReader, BlockPos pos) {
        return Block.canSupportCenter(worldReader, pos.below(), Direction.UP);
    }

    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public String getBlockPrefix() {
        return "candle";
    }

    @Override
    public BlockState colouredState(World level, BlockPos pos, DyeColor color) {
        BlockState ns = IDyeableBlock.super.colouredState(level, pos, color);

        BlockState cs = level.getBlockState(pos);

        return ns.setValue(CANDLES, cs.getValue(CANDLES)).setValue(LIT, cs.getValue(LIT)).setValue(WATERLOGGED, cs.getValue(WATERLOGGED));
    }
}
