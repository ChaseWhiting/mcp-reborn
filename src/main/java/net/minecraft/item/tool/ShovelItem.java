package net.minecraft.item.tool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShovelItem extends ToolItem {
   private static final Set<Block> DIGGABLES = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.SOUL_SOIL, Blocks.SUSPICIOUS_GRAVEL, Blocks.SUSPICIOUS_SAND, Blocks.SUSPICIOUS_CLAY, Blocks.SUSPICIOUS_SOUL_SOIL);
   protected static final Map<Block, BlockState> FLATTENABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.defaultBlockState()));

   public ShovelItem(IItemTier p_i48469_1_, float p_i48469_2_, float p_i48469_3_, Properties p_i48469_4_) {
      super(p_i48469_2_, p_i48469_3_, p_i48469_1_, BlockTags.MINEABLE_WITH_SHOVEL, p_i48469_4_);
   }

   public boolean isCorrectToolForDrops(BlockState p_150897_1_) {
      return p_150897_1_.is(Blocks.SNOW) || p_150897_1_.is(Blocks.SNOW_BLOCK) || super.isCorrectToolForDrops(p_150897_1_);
   }


   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (p_195939_1_.getClickedFace() == Direction.DOWN) {
         return ActionResultType.PASS;
      } else {
         PlayerEntity playerentity = p_195939_1_.getPlayer();
         BlockState blockstate1 = FLATTENABLES.get(blockstate.getBlock());
         BlockState blockstate2 = null;
         if (blockstate1 != null && world.getBlockState(blockpos.above()).isAir()) {
            world.playSound(playerentity, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            blockstate2 = blockstate1;
         } else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.getValue(CampfireBlock.LIT)) {
            if (!world.isClientSide()) {
               world.levelEvent((PlayerEntity)null, 1009, blockpos, 0);
            }

            CampfireBlock.dowse(playerentity, world, blockpos, blockstate);
            blockstate2 = blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false));
         }

         if (blockstate2 != null) {
            if (!world.isClientSide) {
               world.setBlock(blockpos, blockstate2, 11);
               world.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(playerentity, blockstate2));
               if (playerentity != null) {
                  p_195939_1_.getItemInHand().hurtAndBreak(1, playerentity, (p_220041_1_) -> {
                     p_220041_1_.broadcastBreakEvent(p_195939_1_.getHand());
                  });
               }
            }

            return ActionResultType.sidedSuccess(world.isClientSide);
         } else {
            return ActionResultType.PASS;
         }
      }
   }
}