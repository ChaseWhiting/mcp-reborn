package net.minecraft.entity.monster;

import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;

public abstract class Monster extends Creature implements IMob {
   protected Monster(EntityType<? extends Monster> p_i48553_1_, World p_i48553_2_) {
      super(p_i48553_1_, p_i48553_2_);
      this.xpReward = 5;
   }

   public SoundCategory getSoundSource() {
      return SoundCategory.HOSTILE;
   }

   public void aiStep() {
      this.updateSwingTime();
      this.updateNoActionTime();
      super.aiStep();
   }

   protected void updateNoActionTime() {
      float f = this.getBrightness();
      if (f > 0.5F) {
         this.noActionTime += 2;
      }

   }

   protected boolean shouldDespawnInPeaceful() {
      return !this.veryHardmode();
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.HOSTILE_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.HOSTILE_SPLASH;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      return this.isInvulnerableTo(p_70097_1_) ? false : super.hurt(p_70097_1_, p_70097_2_);
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.HOSTILE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HOSTILE_DEATH;
   }

   protected SoundEvent getFallDamageSound(int p_184588_1_) {
      return p_184588_1_ > 4 ? SoundEvents.HOSTILE_BIG_FALL : SoundEvents.HOSTILE_SMALL_FALL;
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return 0.5F - p_205022_2_.getBrightness(p_205022_1_);
   }

   public static boolean isDarkEnoughToSpawn(IServerWorld p_223323_0_, BlockPos p_223323_1_, Random p_223323_2_) {
      if (p_223323_0_.getBrightness(LightType.SKY, p_223323_1_) > p_223323_2_.nextInt(32)) {
         return false;
      } else {
         int i = p_223323_0_.getLevel().isThundering() ? p_223323_0_.getMaxLocalRawBrightness(p_223323_1_, 10) : p_223323_0_.getMaxLocalRawBrightness(p_223323_1_);
         return i <= p_223323_2_.nextInt(8);
      }
   }

   public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> p_223325_0_, IServerWorld p_223325_1_, SpawnReason p_223325_2_, BlockPos p_223325_3_, Random p_223325_4_) {
      if (p_223325_0_ != EntityType.CREAKING && checkNearbyBlocks(p_223325_1_, p_223325_3_)) return false;


      return p_223325_1_.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(p_223325_1_, p_223325_3_, p_223325_4_) && checkMobSpawnRules(p_223325_0_, p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_) ;
   }

   public static boolean checkAnyLightMonsterSpawnRules(EntityType<? extends Monster> p_223324_0_, IWorld p_223324_1_, SpawnReason p_223324_2_, BlockPos p_223324_3_, Random p_223324_4_) {
      return p_223324_1_.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(p_223324_0_, p_223324_1_, p_223324_2_, p_223324_3_, p_223324_4_);
   }

   public static AttributeModifierMap.MutableAttribute createMonsterAttributes() {
      return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE).add(Attributes.FOLLOW_RANGE, 20);
   }


   public static boolean checkNearbyBlocks(IServerWorld world, BlockPos pos) {
      int paleHangingMossCount = 0;
      int paleMossBlockCount = 0;
      int paleOakLogCount = 0;
      int paleOakLeavesCount = 0;

      int radius = 10;

      for (int x = -radius; x <= radius; x++) {
         for (int y = -radius; y <= radius; y++) {
            for (int z = -radius; z <= radius; z++) {
               BlockPos currentPos = pos.offset(x, y, z);
               Block block = world.getBlockState(currentPos).getBlock();

               if (block == Blocks.PALE_HANGING_MOSS) {
                  paleHangingMossCount++;
               } else if (block == Blocks.PALE_MOSS_BLOCK) {
                  paleMossBlockCount++;
               } else if (block == Blocks.PALE_OAK_LOG) {
                  paleOakLogCount++;
               } else if (block == Blocks.PALE_OAK_LEAVES) {
                  paleOakLeavesCount++;
               }
            }
         }
      }

      return paleHangingMossCount >= 10
              && paleMossBlockCount >= 8
              && paleOakLogCount >= 15
              && paleOakLeavesCount >= 12;
   }

   protected boolean shouldDropExperience() {
      return true;
   }

   protected boolean shouldDropLoot() {
      return true;
   }

   public boolean isPreventingPlayerRest(PlayerEntity p_230292_1_) {
      return true;
   }

   public ItemStack getProjectile(ItemStack p_213356_1_) {
      if (p_213356_1_.getItem() instanceof ShootableItem) {
         Predicate<ItemStack> predicate = ((ShootableItem)p_213356_1_.getItem()).getSupportedHeldProjectiles();
         ItemStack itemstack = ShootableItem.getHeldProjectile(this, predicate);
         return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

}