package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties p_i48471_1_) {
      super(p_i48471_1_);
   }

   public boolean mineBlock(ItemStack itemStack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
      if (!world.isClientSide && !state.getBlock().is(BlockTags.FIRE)) {
         itemStack.hurtAndBreak(1, entity, (p_220036_0_) -> {
            p_220036_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
         });
      }

      return !state.is(BlockTags.LEAVES) && !state.is(Blocks.COBWEB) && !state.is(Blocks.GRASS) && !state.is(Blocks.FERN) && !state.is(Blocks.DEAD_BUSH) && !state.is(Blocks.VINE) && !state.is(Blocks.TRIPWIRE) && !state.is(BlockTags.WOOL) ? super.mineBlock(itemStack, world, state, pos, entity) : true;
   }

   public boolean isCorrectToolForDrops(BlockState p_150897_1_) {
      return p_150897_1_.is(Blocks.COBWEB) || p_150897_1_.is(Blocks.REDSTONE_WIRE) || p_150897_1_.is(Blocks.TRIPWIRE);
   }

   public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
      if (!p_150893_2_.is(Blocks.COBWEB) && !p_150893_2_.is(BlockTags.LEAVES)) {
         return p_150893_2_.is(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(p_150893_1_, p_150893_2_);
      } else {
         return 15.0F;
      }
   }
}