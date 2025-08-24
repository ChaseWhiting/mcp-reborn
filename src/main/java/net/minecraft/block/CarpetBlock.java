package net.minecraft.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.item.DyeColor;
import net.minecraft.item.dyeable.IDyeableBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import java.util.function.Supplier;

public class CarpetBlock extends Block implements IDyeableBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
   private final DyeColor color;

   protected CarpetBlock(DyeColor p_i48290_1_, AbstractBlock.Properties p_i48290_2_) {
      super(p_i48290_2_);
      this.color = p_i48290_1_;
   }

   public DyeColor getColor() {
      return this.color;
   }

   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return !p_196260_2_.isEmptyBlock(p_196260_3_.below());
   }

   @Override
   public Block getBlock() {
      return this;
   }

   @Override
   public Supplier<BiMap<DyeColor, Block>> getDyeConversion() {
      return Suppliers.memoize(() -> ImmutableBiMap.<DyeColor, Block>builder()
              .put(DyeColor.WHITE, Blocks.WHITE_CARPET)
              .put(DyeColor.ORANGE, Blocks.ORANGE_CARPET)
              .put(DyeColor.MAGENTA, Blocks.MAGENTA_CARPET)
              .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET)
              .put(DyeColor.YELLOW, Blocks.YELLOW_CARPET)
              .put(DyeColor.LIME, Blocks.LIME_CARPET)
              .put(DyeColor.PINK, Blocks.PINK_CARPET)
              .put(DyeColor.GRAY, Blocks.GRAY_CARPET)
              .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET)
              .put(DyeColor.CYAN, Blocks.CYAN_CARPET)
              .put(DyeColor.PURPLE, Blocks.PURPLE_CARPET)
              .put(DyeColor.BLUE, Blocks.BLUE_CARPET)
              .put(DyeColor.BROWN, Blocks.BROWN_CARPET)
              .put(DyeColor.GREEN, Blocks.GREEN_CARPET)
              .put(DyeColor.RED, Blocks.RED_CARPET)
              .put(DyeColor.BLACK, Blocks.BLACK_CARPET)
              .build());
   }


   public static Supplier<BiMap<DyeColor, Block>> getDyeConversionMap() {
      return Suppliers.memoize(() -> ImmutableBiMap.<DyeColor, Block>builder()
              .put(DyeColor.WHITE, Blocks.WHITE_CARPET)
              .put(DyeColor.ORANGE, Blocks.ORANGE_CARPET)
              .put(DyeColor.MAGENTA, Blocks.MAGENTA_CARPET)
              .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET)
              .put(DyeColor.YELLOW, Blocks.YELLOW_CARPET)
              .put(DyeColor.LIME, Blocks.LIME_CARPET)
              .put(DyeColor.PINK, Blocks.PINK_CARPET)
              .put(DyeColor.GRAY, Blocks.GRAY_CARPET)
              .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET)
              .put(DyeColor.CYAN, Blocks.CYAN_CARPET)
              .put(DyeColor.PURPLE, Blocks.PURPLE_CARPET)
              .put(DyeColor.BLUE, Blocks.BLUE_CARPET)
              .put(DyeColor.BROWN, Blocks.BROWN_CARPET)
              .put(DyeColor.GREEN, Blocks.GREEN_CARPET)
              .put(DyeColor.RED, Blocks.RED_CARPET)
              .put(DyeColor.BLACK, Blocks.BLACK_CARPET)
              .build());
   }

   public String getBlockPrefix() {
      return "carpet";
   }
}