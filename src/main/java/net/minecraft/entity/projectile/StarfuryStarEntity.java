package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StarfuryStarEntity extends ProjectileItemEntity {
   private boolean solid = false; // Tracks if the projectile has become solid

   public StarfuryStarEntity(EntityType<? extends StarfuryStarEntity> p_i50154_1_, World p_i50154_2_) {
      super(p_i50154_1_, p_i50154_2_);
   }

   public StarfuryStarEntity(World p_i1780_1_, LivingEntity p_i1780_2_) {
      super(EntityType.STARFURY_STAR, p_i1780_2_, p_i1780_1_);
   }

   public StarfuryStarEntity(World p_i1781_1_, double p_i1781_2_, double p_i1781_4_, double p_i1781_6_) {
      super(EntityType.STARFURY_STAR, p_i1781_2_, p_i1781_4_, p_i1781_6_, p_i1781_1_);
   }

   @Override
   protected void onHitEntity(EntityRayTraceResult entityRayTraceResult) {
      super.onHitEntity(entityRayTraceResult);
      if (this.getOwner() != null && this.getOwner() instanceof LivingEntity && this.getOwner() != entityRayTraceResult.getEntity()) {
         entityRayTraceResult.getEntity().hurt(DamageSource.indirectMobAttack(this, this.getOwner().as(LivingEntity.class)).setProjectile(), regular() ? 13 : 22);
      }
   }

   @Override
   protected void onHit(RayTraceResult result) {
      super.onHit(result);

      // Determine the distance within which the projectile becomes solid
      int solidDistance = regular() ? 3 : 6;
      if (this.getOwner() != null && Math.abs(this.getY() - this.getOwner().getY()) <= solidDistance || this.getOwner() != null && this.getY() <= this.getOwner().getY()) {
         solid = true; // Set the projectile as solid
      }

      // Not solid yet; skip damage and explosion particles
      if (this.getItem().getItem() == Items.STAR) {
         makeYellowParticle(4);
         makePinkParticle(6);
      } else {
         makePurpleParticle(12);
      }


      for (int i = 0; i < 1; i++) {
         this.level.addParticle(ParticleTypes.EXPLOSION, this.getX() + random.nextGaussian() * 0.2,
                 this.getY() + random.nextGaussian() * 0.2,
                 this.getZ() + random.nextGaussian() * 0.2,
                 0, 0, 0);
      }
      if (solid) {
         // Solid projectile behavior

         if (!this.level.isClientSide) {
            List<Entity> entityList = level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(4, 3, 4), entity -> entity != this && entity.isAlive());
            if (this.getOwner() != null) {
               entityList = entityList.stream().filter(entity -> entity != this.getOwner()).collect(Collectors.toList());
            }
            this.playSound(SoundEvents.STARFURY_STAR_IMPACT, 2.5f, 1.0f);
            if (!entityList.isEmpty()) {
               for (Entity entity : entityList) {
                  if (entity instanceof LivingEntity) {
                     entity.hurt(DamageSource.indirectMobAttack(this, entity.as(LivingEntity.class)).setProjectile(), regular() ? 8 : 16);
                  }
               }
            }
            this.remove();
         }
      }
   }

   public boolean regular() {
      return this.getItem().getItem() == Items.STAR;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getItem().getItem() == Items.STAR) {
         makePinkParticle(6);
         makeYellowParticle(2);
      } else {
         makePurpleParticle(9);
      }
   }

   private void makeYellowParticle(int count) {
      int color = 0xFFFF00;
      double red = (double) (color >> 16 & 255) / 255.0D;
      double green = (double) (color >> 8 & 255) / 255.0D;
      double blue = (double) (color & 255) / 255.0D;

      Random random = new Random();
      for (int i = 0; i < count; ++i) {
         this.level.addAlwaysVisibleParticle(
                 ParticleTypes.ENTITY_EFFECT,
                 this.getX() + random.nextGaussian() * 0.1,
                 this.getY() + random.nextGaussian() * 0.1,
                 this.getZ() + random.nextGaussian() * 0.1,
                 red, green, blue
         );
      }
   }

   private void makePinkParticle(int count) {
      int color = 0xFF69B4;
      double red = (double) (color >> 16 & 255) / 255.0D;
      double green = (double) (color >> 8 & 255) / 255.0D;
      double blue = (double) (color & 255) / 255.0D;

      Random random = new Random();
      for (int i = 0; i < count; ++i) {
         this.level.addAlwaysVisibleParticle(
                 ParticleTypes.ENTITY_EFFECT,
                 this.getX() + random.nextGaussian() * 0.1,
                 this.getY() + random.nextGaussian() * 0.1,
                 this.getZ() + random.nextGaussian() * 0.1,
                 red, green, blue
         );
      }
   }

   private void makePurpleParticle(int count) {
      int color = 0x992e69;
      double red = (double) (color >> 16 & 255) / 255.0D;
      double green = (double) (color >> 8 & 255) / 255.0D;
      double blue = (double) (color & 255) / 255.0D;

      Random random = new Random();
      for (int i = 0; i < count; ++i) {
         this.level.addAlwaysVisibleParticle(
                 ParticleTypes.ENTITY_EFFECT,
                 this.getX() + random.nextGaussian() * 0.1,
                 this.getY() + random.nextGaussian() * 0.1,
                 this.getZ() + random.nextGaussian() * 0.1,
                 red, green, blue
         );
      }
   }

   @Override
   protected Item getDefaultItem() {
      return Items.STAR;
   }

   // Saving the "solid" state
   @Override
   public void addAdditionalSaveData(CompoundNBT tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("Solid", this.solid);
   }

   // Loading the "solid" state
   @Override
   public void readAdditionalSaveData(CompoundNBT tag) {
      super.readAdditionalSaveData(tag);
      this.solid = tag.getBoolean("Solid");
   }
}
