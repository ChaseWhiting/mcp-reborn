package net.minecraft.item.tool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoeItem extends ToolItem {
   private static final Set<Block> DIGGABLES = ImmutableSet.of(Blocks.APPLE_LEAVES, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.HAY_BLOCK, Blocks.DRIED_KELP_BLOCK, Blocks.TARGET, Blocks.SHROOMLIGHT, Blocks.SPONGE, Blocks.WET_SPONGE, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.PALE_OAK_LEAVES, Blocks.DEAD_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.SCULK_VEIN, Blocks.SCULK, Blocks.SCULK_SENSOR, Blocks.SCULK_CATALYST);
   protected static final Map<Block, BlockState> TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.defaultBlockState(), Blocks.GRASS_PATH, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT, Blocks.FARMLAND.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT.defaultBlockState()));

   public HoeItem(IItemTier p_i231595_1_, int p_i231595_2_, float p_i231595_3_, Properties p_i231595_4_) {
      super((float)p_i231595_2_, p_i231595_3_, p_i231595_1_, BlockTags.MINEABLE_WITH_HOE, p_i231595_4_);
   }

   public ActionResultType useOn(ItemUseContext context) {
      World world = context.getLevel();
      BlockPos blockpos = context.getClickedPos();
      if (context.getClickedFace() != Direction.DOWN && world.getBlockState(blockpos.above()).isAir()) {
         BlockState blockstate = TILLABLES.get(world.getBlockState(blockpos).getBlock());
         if (blockstate != null) {
            PlayerEntity playerentity = context.getPlayer();
            world.playSound(playerentity, blockpos, SoundEvents.HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClientSide) {
               till(world, blockpos, playerentity, context);

               ItemStack stack = context.getItemInHand();

               int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.TILLING, stack);

               if (level != 0 && playerentity != null && !playerentity.isDiscrete()) {
                  if (level >= 1) {
                     till(world, blockpos.east(), playerentity, context);
                     till(world, blockpos.west(), playerentity, context);
                     till(world, blockpos.north(), playerentity, context);
                     till(world, blockpos.south(), playerentity, context);

                     if (level >= 2) {
                        till(world, blockpos.east().north(), playerentity, context);
                        till(world, blockpos.east().south(), playerentity, context);
                        till(world, blockpos.west().north(), playerentity, context);
                        till(world, blockpos.west().south(), playerentity, context);
                     }
                  }
               }

               if (playerentity != null) {
                  context.getItemInHand().hurtAndBreak(1, playerentity, (p_220043_1_) -> {
                     p_220043_1_.broadcastBreakEvent(context.getHand());
                  });
               }
            }

            return ActionResultType.sidedSuccess(world.isClientSide);
         }
      }

      return ActionResultType.PASS;
   }

   private static void till(World world, BlockPos pos, PlayerEntity player, ItemUseContext context) {
      BlockState state = TILLABLES.get(world.getBlockState(pos).getBlock());
      if (state == null) return;

      BlockState above = world.getBlockState(pos.above());
      if (above.is(BlockTags.FLOWERS) || above.is(List.of(Blocks.GRASS, Blocks.TALL_GRASS, Blocks.SHORT_DRY_GRASS, Blocks.TALL_DRY_GRASS))
      || above.is(List.of(Blocks.PINK_PETALS, Blocks.LEAF_LITTER))) {
         boolean fwd = world.removeBlock(pos.above(), false);
         if (fwd) {
            above.getBlock().destroy(world, pos.above(), above);
         }

         boolean fl = player.hasCorrectToolForDrops(above);
         context.getItemInHand().mineBlock(world, above, pos.above(), player);
         if (fwd && fl) {
            above.getBlock().playerDestroy(world, player, pos.above(), above, world.getBlockEntity(pos.above()), context.getItemInHand());
         }
      }

       if (world.getBlockState(pos.above()).isAir()) {
           world.setBlock(pos, state, 11);
           world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
       }
   }

}