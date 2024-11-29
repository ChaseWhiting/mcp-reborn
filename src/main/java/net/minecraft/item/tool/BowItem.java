package net.minecraft.item.tool;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.bundle.QuiverItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class BowItem extends ShootableItem implements IVanishable {
   public BowItem(Properties p_i48522_1_) {
      super(p_i48522_1_);
   }

   public void releaseUsing(ItemStack bowStack, World world, LivingEntity entity, int timeLeft) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity) entity;
         boolean infiniteArrows = player.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bowStack) > 0;
         ItemStack projectileStack = player.getProjectile(bowStack);

         if (!projectileStack.isEmpty() || infiniteArrows) {
            if (projectileStack.isEmpty()) {
               projectileStack = new ItemStack(Items.ARROW);
            }

            int drawDuration = this.getUseDuration(bowStack) - timeLeft;
            float power = getPowerForTime(drawDuration);
            if (!((double) power < 0.1D)) {
               boolean isCreativeOrInfiniteArrow = infiniteArrows && projectileStack.getItem() == Items.ARROW;
               if (!world.isClientSide) {
                  ArrowItem arrowItem = (ArrowItem) (projectileStack.getItem() instanceof ArrowItem ? projectileStack.getItem() : Items.ARROW);
                  AbstractArrowEntity arrowEntity = arrowItem.createArrow(world, projectileStack, player);
                  arrowEntity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, power * 3.0F, 1.0F);

                  if (power == 1.0F) {
                     arrowEntity.setCritArrow(true);
                  }

                  int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bowStack);
                  if (powerLevel > 0) {
                     arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
                  }

                  int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bowStack);
                  if (punchLevel > 0) {
                     arrowEntity.setKnockback(punchLevel);
                  }

                  if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bowStack) > 0) {
                     arrowEntity.setSecondsOnFire(100);
                  }

                  bowStack.hurtAndBreak(1, player, (p_220009_1_) -> {
                     p_220009_1_.broadcastBreakEvent(player.getUsedItemHand());
                  });

                  if (isCreativeOrInfiniteArrow || player.abilities.instabuild && (projectileStack.getItem() == Items.SPECTRAL_ARROW || projectileStack.getItem() == Items.TIPPED_ARROW)) {
                     arrowEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                  }

                  world.addFreshEntity(arrowEntity);
               }

               world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);

               if (!isCreativeOrInfiniteArrow && !player.abilities.instabuild) {
                  // Check if the arrow is from a quiver
                  if (projectileStack.getItem() instanceof QuiverItem) {
                     // Remove one arrow from the quiver
                     removeArrowFromQuiver(player, projectileStack);
                  } else {
                     projectileStack.shrink(1);
                     if (projectileStack.isEmpty()) {
                        player.inventory.removeItem(projectileStack);
                     }
                  }
               }

               player.awardStat(Stats.ITEM_USED.get(this));
            }
         }
      }
   }

   private void removeArrowFromQuiver(PlayerEntity player, ItemStack quiverStack) {
      QuiverItem quiverItem = (QuiverItem) quiverStack.getItem();
      // Retrieve the contents of the quiver
      List<ItemStack> quiverContents = QuiverItem.getContents(quiverStack).collect(Collectors.toList());

      // Find the first arrow in the quiver and remove one from its stack
      for (ItemStack stack : quiverContents) {
         if (stack.getItem() instanceof ArrowItem) {
            stack.shrink(1);
            if (stack.isEmpty()) {
               // If the stack is empty, remove it from the quiver's NBT
               quiverContents.remove(stack);
            }
            break;
         }
      }

      // Update the quiver's NBT with the modified contents
      CompoundNBT nbt = quiverStack.getOrCreateTag();
      ListNBT itemList = new ListNBT();
      for (ItemStack item : quiverContents) {
         CompoundNBT itemTag = new CompoundNBT();
         item.save(itemTag);
         itemList.add(itemTag);
      }
      nbt.put("Items", itemList);
      quiverStack.setTag(nbt);
   }

   public static float getPowerForTime(int p_185059_0_) {
      float f = (float) p_185059_0_ / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 72000;
   }

   public UseAction getUseAnimation(ItemStack p_77661_1_) {
      return UseAction.BOW;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      boolean flag = !player.getProjectile(itemstack).isEmpty();
      if (!player.abilities.instabuild && !flag) {
         return ActionResult.fail(itemstack);
      } else {
         player.startUsingItem(hand);
         return ActionResult.consume(itemstack);
      }
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_OR_BONE_ARROW;
   }

   public int getDefaultProjectileRange() {
      return 15;
   }
}
