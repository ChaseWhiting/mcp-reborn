package net.minecraft.entity.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.GreatHungerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ExperienceBottleEntity extends ProjectileItemEntity {

   public int[] experienceToAdd = new int[]{0, 0, 0};
   public ExperienceBottleEntity(EntityType<? extends ExperienceBottleEntity> p_i50152_1_, World p_i50152_2_) {
      super(p_i50152_1_, p_i50152_2_);
   }

   public ExperienceBottleEntity(World p_i1786_1_, LivingEntity p_i1786_2_) {
      super(EntityType.EXPERIENCE_BOTTLE, p_i1786_2_, p_i1786_1_);
   }

   public ExperienceBottleEntity(World p_i1787_1_, double p_i1787_2_, double p_i1787_4_, double p_i1787_6_) {
      super(EntityType.EXPERIENCE_BOTTLE, p_i1787_2_, p_i1787_4_, p_i1787_6_, p_i1787_1_);
   }

   protected Item getDefaultItem() {
      return Items.EXPERIENCE_BOTTLE;
   }

   protected float getGravity() {
      return 0.07F;
   }

   protected void onHit(RayTraceResult result) {
      super.onHit(result);
      if (!this.level.isClientSide) {
         this.level.levelEvent(2002, this.blockPosition(), PotionUtils.getColor(Potions.WATER));
         if (result.getType() == RayTraceResult.Type.ENTITY && result instanceof EntityRayTraceResult) {
            EntityRayTraceResult entityResult = (EntityRayTraceResult) result;

            if (entityResult.getEntity() instanceof GreatHungerEntity) {
               addXp((GreatHungerEntity) entityResult.getEntity());
               this.remove();
               return;
            }
         }
         int i = generateRandomValue(3, 5, 5);
         if (experienceToAdd.length >= 2 && experienceToAdd[0] != 0 && experienceToAdd[1] != 0 && experienceToAdd[2] != 0) {
            i = generateRandomValue(experienceToAdd[0], experienceToAdd[1], experienceToAdd[2]);
         }
         while(i > 0) {
            int j = ExperienceOrbEntity.getExperienceValue(i);
            i -= j;
            this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.getX(), this.getY(), this.getZ(), j));
         }

         this.remove();
      }
   }

   public void readAdditionalSaveData(CompoundNBT nbt) {
      super.readAdditionalSaveData(nbt);
      if (nbt.contains("EXP")) {
         this.experienceToAdd = nbt.getIntArray("EXP");
      }
   }

   public void addAdditionalSaveData(CompoundNBT nbt) {
      super.addAdditionalSaveData(nbt);
      nbt.putIntArray("EXP", this.experienceToAdd);
   }

   public int generateRandomValue(int val, int val2, int val3) {
      return val + this.level.random.nextInt(val2) + this.level.random.nextInt(val3);
   }

   private void addXp(GreatHungerEntity entity) {
      entity.setStoredExperiencePoints(entity.getStoredExperiencePoints() + generateRandomValue(2, 3, 3));
   }
}