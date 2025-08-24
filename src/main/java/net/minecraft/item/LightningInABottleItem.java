package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LightningInABottleItem extends Item {
   public LightningInABottleItem(Properties p_i225737_1_) {
      super(p_i225737_1_);
   }

   public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
      super.finishUsingItem(itemStack, world, entity);
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStack);
         serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
      }

      if (world.isServerSide) {
         ServerWorld serverWorld = (ServerWorld) world;
         serverWorld.setWeatherParameters(0, 2400 * 20, true, true);
      }

      for (int i = 1; i < 5; i++ ){
         double offsetY = 0;



         switch (i) {
            case 1 -> offsetY = 0.2;
            case 2 -> offsetY = 0.6;
            case 3 -> offsetY = 1.0;
            case 4 -> offsetY = entity.getBbHeight() + 0.2;
            default -> offsetY = 0;
         }


         double centerX = entity.getX();
         double centerY = entity.getY() + offsetY;
         double centerZ = entity.getZ();

         AxisAlignedBB boundingBox = entity.getBoundingBox();
         double bbWidth = boundingBox.getXsize() / 2.0;

         for (int x = 0; x < ParticleTypes.randomBetweenInclusive(random, 12, 22); x++) {
            double particleX = centerX + (random.nextDouble() - 0.5) * bbWidth * 2;
            double particleY = centerY + (random.nextDouble() - 0.5) * 0.5;
            double particleZ = centerZ + (random.nextDouble() - 0.5) * bbWidth * 2;

            world.addParticle(ParticleTypes.ELECTRIC_SPARK, particleX, particleY, particleZ, 0, 0, 0);
         }
      }




      if (itemStack.isEmpty()) {
         return new ItemStack(Items.GLASS_BOTTLE);
      } else {
         if (entity instanceof PlayerEntity && !((PlayerEntity) entity).abilities.instabuild) {
            ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
            PlayerEntity playerentity = (PlayerEntity) entity;
            if (!playerentity.inventory.add(itemstack)) {
               playerentity.drop(itemstack, false);
            }
         }

         return itemStack;
      }
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 40;
   }

   public UseAction getUseAnimation(ItemStack p_77661_1_) {
      return UseAction.DRINK;
   }


   public SoundEvent getEatingSound() {
      return SoundEvents.GENERIC_DRINK;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      return DrinkHelper.useDrink(world, player, hand);
   }
}