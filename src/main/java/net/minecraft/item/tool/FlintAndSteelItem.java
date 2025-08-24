package net.minecraft.item.tool;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem(Item.Properties p_i48493_1_) {
      super(p_i48493_1_);
   }

   public int getWeight(ItemStack bundle) {
      return 4;
   }

   public ActionResultType useOn(ItemUseContext context) {
      PlayerEntity playerentity = context.getPlayer();
      World world = context.getLevel();
      BlockPos blockpos = context.getClickedPos();
      BlockState blockstate = world.getBlockState(blockpos);
//
      if (SharedConstants.EVERY_BLOCK_IS_TNT) {
         if (!context.getPlayer().isShiftKeyDown()) {
            if (world.isClientSide) return ActionResultType.FAIL;

            TNTEntity blockTNT = new TNTEntity(world, blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5, context.getPlayer());
            blockTNT.setBlockState(blockstate);

            world.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());

            world.playSound(context.getPlayer(), context.getPlayer().blockPosition(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
            context.getPlayer().getMainHandItem().hurt(1, playerentity.getRandom(), (ServerPlayerEntity) playerentity);
            world.addFreshEntity(blockTNT);

            return ActionResultType.SUCCESS;
         }
      }
//
      if (CampfireBlock.canLight(blockstate) || CandleBlock.canLight(blockstate)) {
         world.playSound(playerentity, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
         world.setBlock(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
         world.gameEvent(playerentity != null ? playerentity : null, GameEvent.BLOCK_CHANGE, blockpos);
         if (playerentity != null) {
            context.getItemInHand().hurtAndBreak(1, playerentity, (p_219999_1_) -> {
               p_219999_1_.broadcastBreakEvent(context.getHand());
            });
         }

         return ActionResultType.sidedSuccess(world.isClientSide());
      } else {
         BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
         if (AbstractFireBlock.canBePlacedAt(world, blockpos1, context.getHorizontalDirection())) {
            world.playSound(playerentity, blockpos1, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            BlockState blockstate1 = AbstractFireBlock.getState(world, blockpos1);
            world.setBlock(blockpos1, blockstate1, 11);
            world.gameEvent(playerentity != null ? playerentity : null, GameEvent.BLOCK_PLACE, blockpos);

            ItemStack itemstack = context.getItemInHand();
            if (playerentity instanceof ServerPlayerEntity) {
               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, blockpos1, itemstack);
               itemstack.hurtAndBreak(1, playerentity, (p_219998_1_) -> {
                  p_219998_1_.broadcastBreakEvent(context.getHand());
               });
            }

            return ActionResultType.sidedSuccess(world.isClientSide());
         } else {
            return ActionResultType.FAIL;
         }
      }
   }
}