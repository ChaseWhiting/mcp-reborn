package net.minecraft.block;

import com.clearspring.analytics.util.Lists;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class DryVegetationBlock extends BushBlock {
    private static List<Block> VALID_BLOCKS;
    private static List<Block> TERRACOTTA;
    public static List<Block> PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS;


    protected DryVegetationBlock(Properties properties) {
        super(properties);
    }

    public static void bootstrap() {
        if (VALID_BLOCKS == null) {
            VALID_BLOCKS = Lists.newArrayList();
            TERRACOTTA = Lists.newArrayList();
            PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS = Lists.newArrayList();

        }

        VALID_BLOCKS.addAll(List.of(Blocks.SAND, Blocks.RED_SAND));
        VALID_BLOCKS.addAll(List.of(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.MUD, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM, Blocks.PALE_MOSS_BLOCK));
        VALID_BLOCKS.addAll(List.of(Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA,
                Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA,
                Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA,
                Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA,
                Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA,
                Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA,
                Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA,
                Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA,
                Blocks.BLACK_TERRACOTTA));
        TERRACOTTA.addAll(List.of(Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA,
                Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA,
                Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA,
                Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA,
                Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA,
                Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA,
                Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA,
                Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA,
                Blocks.BLACK_TERRACOTTA));
        PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS.addAll(TERRACOTTA);
        PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS.addAll(List.of(Blocks.SAND, Blocks.RED_SAND));
    }

    private static final VoxelShape SHAPE = column(12, 0, 13);
    private static final int IDLE_SOUND_CHANCE = 150;
    private static final int IDLE_SOUND_BADLANDS_DECREASED_CHANCE = 5;

    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        Vector3d vector3d = state.getOffset(world, pos);
        return this.getShape().move(vector3d.x, vector3d.y, vector3d.z);
    }

    public AbstractBlock.OffsetType getOffsetType() {
        return AbstractBlock.OffsetType.XZ;
    }

    public VoxelShape getShape() {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
        return VALID_BLOCKS != null && VALID_BLOCKS.contains(p_200014_1_.getBlock());
    }

    @Override
    public void animateTick(BlockState blockState, World level, BlockPos blockPos, Random randomSource) {
        if (randomSource.nextInt(IDLE_SOUND_CHANCE) == 0) {
            BlockState blockState2 = level.getBlockState(blockPos.below());
            if ((blockState2.is(Blocks.RED_SAND) || TERRACOTTA.contains(blockState2.getBlock())) && randomSource.nextInt(IDLE_SOUND_BADLANDS_DECREASED_CHANCE) != 0) {
                return;
            }
            BlockState blockState3 = level.getBlockState(blockPos.below(2));
            if (PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS.contains(blockState2.getBlock()) && PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS.contains(blockState3.getBlock())) {
                level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.DEAD_BUSH_IDLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
        }
    }

    public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
        return true;
    }
}
