package net.minecraft.item.tool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoeItem extends ToolItem {
   private static final Set<Block> DIGGABLES = ImmutableSet.of(Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.HAY_BLOCK, Blocks.DRIED_KELP_BLOCK, Blocks.TARGET, Blocks.SHROOMLIGHT, Blocks.SPONGE, Blocks.WET_SPONGE, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES);
   protected static final Map<Block, BlockState> TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.defaultBlockState(), Blocks.GRASS_PATH, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT, Blocks.FARMLAND.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT.defaultBlockState()));

   public HoeItem(IItemTier p_i231595_1_, int p_i231595_2_, float p_i231595_3_, Properties p_i231595_4_) {
      super((float)p_i231595_2_, p_i231595_3_, p_i231595_1_, DIGGABLES, p_i231595_4_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      if (p_195939_1_.getClickedFace() != Direction.DOWN && world.getBlockState(blockpos.above()).isAir()) {
         BlockState blockstate = TILLABLES.get(world.getBlockState(blockpos).getBlock());
         if (blockstate != null) {
            PlayerEntity playerentity = p_195939_1_.getPlayer();
            world.playSound(playerentity, blockpos, SoundEvents.HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClientSide) {
               world.setBlock(blockpos, blockstate, 11);
               if (playerentity != null) {
                  p_195939_1_.getItemInHand().hurtAndBreak(1, playerentity, (p_220043_1_) -> {
                     p_220043_1_.broadcastBreakEvent(p_195939_1_.getHand());
                  });
               }
            }

            return ActionResultType.sidedSuccess(world.isClientSide);
         }
      }

      return ActionResultType.PASS;
   }
}