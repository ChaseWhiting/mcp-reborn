package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;
import javax.annotation.Nullable;


public class MultifaceBlock
extends Block
implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = SixWayBlock.PROPERTY_BY_DIRECTION;
    protected static final Direction[] DIRECTIONS = Direction.values();
    private final Function<BlockState, VoxelShape> shapes;
    private final boolean canRotate;
    private final boolean canMirrorX;
    private final boolean canMirrorZ;

    public MultifaceBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(MultifaceBlock.getDefaultMultifaceState(this.stateDefinition));
        this.shapes = this.makeShapes();
        this.canRotate = Direction.Plane.HORIZONTAL.stream().allMatch(this::isFaceSupported);
        this.canMirrorX = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.X).filter(this::isFaceSupported).count() % 2L == 0L;
        this.canMirrorZ = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(this::isFaceSupported).count() % 2L == 0L;
    }

    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);

    private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), (map) -> {
        map.put(Direction.NORTH, NORTH_AABB);
        map.put(Direction.EAST, EAST_AABB);
        map.put(Direction.SOUTH, SOUTH_AABB);
        map.put(Direction.WEST, WEST_AABB);
        map.put(Direction.UP, UP_AABB);
        map.put(Direction.DOWN, DOWN_AABB);
    });



    private Function<BlockState, VoxelShape> makeShapes() {
        return this.getShapeForEachState(MultifaceBlock::calculateMultifaceShape);
    }

    private static VoxelShape calculateMultifaceShape(BlockState state) {
        VoxelShape shape = VoxelShapes.empty();

        for (Direction direction : DIRECTIONS) {
            if (hasFace(state, direction)) {
                shape = VoxelShapes.or(shape, SHAPE_BY_DIRECTION.get(direction));
            }
        }

        return shape.isEmpty() ? VoxelShapes.block() : shape;
    }


    public static Set<Direction> availableFaces(BlockState blockState) {
        if (!(blockState.getBlock() instanceof MultifaceBlock)) {
            return Set.of();
        }
        EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);
        for (Direction direction : Direction.values()) {
            if (!MultifaceBlock.hasFace(blockState, direction)) continue;
            enumSet.add(direction);
        }
        return enumSet;
    }

    public static Set<Direction> unpack(byte by) {
        EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);
        for (Direction direction : Direction.values()) {
            if ((by & (byte)(1 << direction.ordinal())) <= 0) continue;
            enumSet.add(direction);
        }
        return enumSet;
    }

    public static byte pack(Collection<Direction> collection) {
        byte by = 0;
        for (Direction direction : collection) {
            by = (byte)(by | 1 << direction.ordinal());
        }
        return by;
    }

    protected boolean isFaceSupported(Direction direction) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        for (Direction direction : DIRECTIONS) {
            if (!this.isFaceSupported(direction)) continue;
            builder.add(MultifaceBlock.getFaceProperty(direction));
        }
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, IWorld Iworld, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.getValue(WATERLOGGED)) {
            Iworld.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(Iworld));
        }
        if (!MultifaceBlock.hasAnyFace(blockState)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (!MultifaceBlock.hasFace(blockState, direction) || MultifaceBlock.canAttachTo(Iworld, direction, blockPos2, blockState2)) {
            return blockState;
        }
        return MultifaceBlock.removeFace(blockState, MultifaceBlock.getFaceProperty(direction));
    }

    protected Function<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> function) {
        ImmutableMap<BlockState, VoxelShape> shapeMap = this.stateDefinition.getPossibleStates().stream()
                .collect(ImmutableMap.toImmutableMap(Function.identity(), function));

        return arg_0 -> (VoxelShape) shapeMap.get(arg_0);
    }


    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(blockState);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.shapes.apply(state);
    }

    @Override
    public boolean canSurvive(BlockState blockState, IWorldReader levelReader, BlockPos blockPos) {
        boolean bl = false;
        for (Direction direction : DIRECTIONS) {
            if (!MultifaceBlock.hasFace(blockState, direction)) continue;
            if (!MultifaceBlock.canAttachTo(levelReader, blockPos, direction)) {
                return false;
            }
            bl = true;
        }
        return bl;
    }


    @Override
    public boolean canBeReplaced(BlockState blockState, BlockItemUseContext blockPlaceContext) {
        return !(blockPlaceContext.getItemInHand().getItem()==(this.asItem())) || MultifaceBlock.hasAnyVacantFace(blockState);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext blockPlaceContext) {
        World level = blockPlaceContext.getLevel();
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        return Arrays.stream(blockPlaceContext.getNearestLookingDirections()).map(direction -> this.getStateForPlacement(blockState, level, blockPos, (Direction)direction)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public boolean isValidStateForPlacement(IWorldReader blockGetter, BlockState blockState, BlockPos blockPos, Direction direction) {
        if (!this.isFaceSupported(direction) || blockState.is(this) && MultifaceBlock.hasFace(blockState, direction)) {
            return false;
        }
        BlockPos blockPos2 = blockPos.relative(direction);
        return MultifaceBlock.canAttachTo(blockGetter, direction, blockPos2, blockGetter.getBlockState(blockPos2));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockState blockState, IWorldReader blockGetter, BlockPos blockPos, Direction direction) {
        if (!this.isValidStateForPlacement(blockGetter, blockState, blockPos, direction)) {
            return null;
        }
        BlockState blockState2 = blockState.is(this) ? blockState : (blockState.getFluidState().is(FluidTags.WATER) ? (BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true) : this.defaultBlockState());
        return (BlockState)blockState2.setValue(MultifaceBlock.getFaceProperty(direction), true);
    }



    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        if (!this.canRotate) {
            return blockState;
        }
        return this.mapDirections(blockState, rotation::rotate);
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        if (mirror == Mirror.FRONT_BACK && !this.canMirrorX) {
            return blockState;
        }
        if (mirror == Mirror.LEFT_RIGHT && !this.canMirrorZ) {
            return blockState;
        }
        return this.mapDirections(blockState, mirror::mirror);
    }

    private BlockState mapDirections(BlockState blockState, Function<Direction, Direction> function) {
        BlockState blockState2 = blockState;
        for (Direction direction : DIRECTIONS) {
            if (!this.isFaceSupported(direction)) continue;
            blockState2 = (BlockState)blockState2.setValue(MultifaceBlock.getFaceProperty(function.apply(direction)), blockState.getValue(MultifaceBlock.getFaceProperty(direction)));
        }
        return blockState2;
    }

    public static boolean hasFace(BlockState blockState, Direction direction) {
        BooleanProperty booleanProperty = MultifaceBlock.getFaceProperty(direction);
        return blockState.getValueOrElse(booleanProperty, false);
    }

    public static boolean canAttachTo(IWorldReader blockGetter, BlockPos blockPos, Direction direction) {
        BlockPos blockPos2 = blockPos.relative(direction);
        BlockState blockState = blockGetter.getBlockState(blockPos2);
        return MultifaceBlock.canAttachTo(blockGetter, direction, blockPos2, blockState);
    }

    public static boolean canAttachTo(IWorldReader blockGetter, Direction direction, BlockPos blockPos, BlockState blockState) {
        return Block.isFaceFull(blockState.getBlockSupportShape(blockGetter, blockPos), direction.getOpposite()) || Block.isFaceFull(blockState.getCollisionShape(blockGetter, blockPos), direction.getOpposite());
    }

    private static BlockState removeFace(BlockState blockState, BooleanProperty booleanProperty) {
        BlockState blockState2 = (BlockState)blockState.setValue(booleanProperty, false);
        if (MultifaceBlock.hasAnyFace(blockState2)) {
            return blockState2;
        }
        return Blocks.AIR.defaultBlockState();
    }

    public static BooleanProperty getFaceProperty(Direction direction) {
        return PROPERTY_BY_DIRECTION.get(direction);
    }

    private static BlockState getDefaultMultifaceState(StateContainer<Block, BlockState> stateDefinition) {
        BlockState blockState = (BlockState)stateDefinition.any().setValue(WATERLOGGED, false);
        for (BooleanProperty booleanProperty : PROPERTY_BY_DIRECTION.values()) {
            blockState = (BlockState)blockState.setValue(booleanProperty, false);
        }
        return blockState;
    }

    protected static boolean hasAnyFace(BlockState blockState) {
        for (Direction direction : DIRECTIONS) {
            if (!MultifaceBlock.hasFace(blockState, direction)) continue;
            return true;
        }
        return false;
    }

    private static boolean hasAnyVacantFace(BlockState blockState) {
        for (Direction direction : DIRECTIONS) {
            if (MultifaceBlock.hasFace(blockState, direction)) continue;
            return true;
        }
        return false;
    }
}

