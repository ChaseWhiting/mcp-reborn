package net.minecraft.block;

import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class SnifferEggBlock extends Block {
    public static final int MAX_HATCH_LEVEL = 2;
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
    private static final int REGULAR_HATCH_TIME_TICKS = 24000;
    private static final int BOOSTED_HATCH_TIME_TICKS = 12000;
    private static final int RANDOM_HATCH_OFFSET_TICKS = 300;
    private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 2.0, 15.0, 16.0, 14.0);

    public SnifferEggBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(HATCH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HATCH);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public int getHatchLevel(BlockState state) {
        return state.getValue(HATCH);
    }

    private boolean isReadyToHatch(BlockState blockState) {
        return this.getHatchLevel(blockState) == 2;
    }



    //TODO fully implement sniffer
    @Override
    public void tick(BlockState blockState, ServerWorld serverLevel, BlockPos blockPos, Random randomSource) {
        if (!this.isReadyToHatch(blockState)) {
            serverLevel.playSound(null, blockPos, SoundEvents.SNIFFER_EGG_CRACK, SoundCategory.BLOCKS, 0.7f, 0.9f + randomSource.nextFloat() * 0.2f);
            serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(HATCH, this.getHatchLevel(blockState) + 1), 2);
            return;
        }
        serverLevel.playSound(null, blockPos, SoundEvents.SNIFFER_EGG_HATCH, SoundCategory.BLOCKS, 0.7f, 0.9f + randomSource.nextFloat() * 0.2f);
        serverLevel.destroyBlock(blockPos, false);
//        Sniffer sniffer = EntityType.SNIFFER.create(serverLevel);
//        if (sniffer != null) {
//            Vector3d vec3 = blockPos.getCenter();
//            sniffer.setBaby(true);
//            sniffer.moveTo(vec3.x(), vec3.y(), vec3.z(), MathHelper.wrapDegrees(serverLevel.random.nextFloat() * 360.0f), 0.0f);
//            serverLevel.addFreshEntity(sniffer);
//        }
    }

    @Override
    public void onPlace(BlockState blockState, World level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        boolean bl2 = SnifferEggBlock.hatchBoost(level, blockPos);
        if (!level.isClientSide() && bl2) {
            level.levelEvent(3011, blockPos, 0);
        }
        int n = bl2 ? BOOSTED_HATCH_TIME_TICKS : REGULAR_HATCH_TIME_TICKS;
        int n2 = n / 3;
        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(blockState));
        level.getBlockTicks().scheduleTick(blockPos, this, n2 + level.random.nextInt(RANDOM_HATCH_OFFSET_TICKS));
    }

    @Override
    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }

    // TODO add the moss block here
    public static boolean hatchBoost(World blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos.below()).is(List.of());
    }


}
