package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlowerPotBlock extends Block {
   private static final Map<Block, Block> POTTED_BY_CONTENT = Maps.newHashMap();
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
   private final Block content;

   public FlowerPotBlock(Block p_i48395_1_, AbstractBlock.Properties p_i48395_2_) {
      super(p_i48395_2_);
      this.content = p_i48395_1_;
      POTTED_BY_CONTENT.put(p_i48395_1_, this);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack itemstack = p_225533_4_.getItemInHand(p_225533_5_);
      Item item = itemstack.getItem();
      Block block = item instanceof BlockItem ? POTTED_BY_CONTENT.getOrDefault(((BlockItem)item).getBlock(), Blocks.AIR) : Blocks.AIR;
      boolean flag = block == Blocks.AIR;
      boolean flag1 = this.content == Blocks.AIR;
      if (flag != flag1) {
         if (flag1) {
            p_225533_2_.setBlock(p_225533_3_, block.defaultBlockState(), 3);
            p_225533_4_.awardStat(Stats.POT_FLOWER);
            if (!p_225533_4_.abilities.instabuild) {
               itemstack.shrink(1);
            }
         } else {
            ItemStack itemstack1 = new ItemStack(this.content);
            if (itemstack.isEmpty()) {
               p_225533_4_.setItemInHand(p_225533_5_, itemstack1);
            } else if (!p_225533_4_.addItem(itemstack1)) {
               p_225533_4_.drop(itemstack1, false);
            }

            p_225533_2_.setBlock(p_225533_3_, Blocks.FLOWER_POT.defaultBlockState(), 3);
         }

         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else {
         return ActionResultType.CONSUME;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return this.content == Blocks.AIR ? super.getCloneItemStack(p_185473_1_, p_185473_2_, p_185473_3_) : new ItemStack(this.content);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == Direction.DOWN && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public Block getContent() {
      return this.content;
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}