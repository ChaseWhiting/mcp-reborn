package net.minecraft.block;

import net.minecraft.item.DyeColor;
import net.minecraft.item.dyeable.IDyeableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StainedGlassPaneBlock extends PaneBlock implements IBeaconBeamColorProvider, IDyeableBlock {
   private final DyeColor color;

   public StainedGlassPaneBlock(DyeColor p_i48322_1_, AbstractBlock.Properties p_i48322_2_) {
      super(p_i48322_2_);
      this.color = p_i48322_1_;
      this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public DyeColor getColor() {
      return this.color;
   }

   @Override
   public Block getBlock() {
      return this;
   }

   public BlockState colouredState(World level, BlockPos pos, DyeColor color) {
      BlockState ns = IDyeableBlock.super.colouredState(level, pos, color);

      BlockState cs = level.getBlockState(pos);

      return ns.setValue(NORTH, cs.getValue(NORTH)).setValue(SOUTH, cs.getValue(SOUTH)).setValue(EAST, cs.getValue(EAST)).setValue(WEST, cs.getValue(WEST)).setValue(WATERLOGGED, cs.getValue(WATERLOGGED));
   }

   @Override
   public String getBlockPrefix() {
      return "stained_glass_pane";
   }
}