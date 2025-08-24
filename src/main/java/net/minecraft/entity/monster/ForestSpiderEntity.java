package net.minecraft.entity.monster;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ForestSpiderEntity extends SpiderEntity {
   public ForestSpiderEntity(EntityType<? extends ForestSpiderEntity> p_i50214_1_, World p_i50214_2_) {
      super(p_i50214_1_, p_i50214_2_);
   }

   public static AttributeModifierMap.MutableAttribute createCaveSpider() {
      return SpiderEntity.createAttributes().add(Attributes.MAX_HEALTH, 12.0D);
   }

   public boolean doHurtTarget(Entity target) {
      if (super.doHurtTarget(target)) {
         if (target instanceof LivingEntity) {
            int i = 0;
            if (this.level.getDifficulty() == Difficulty.NORMAL) {
               i = 7;
            } else if (this.level.getDifficulty() == Difficulty.HARD) {
               i = 15;
            }

            if (i > 0) {
               ((LivingEntity) target).addEffect(new EffectInstance(Effects.POISON, i * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      return p_213386_4_;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.45F;
   }
}