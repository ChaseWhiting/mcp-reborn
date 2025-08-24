package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.WarmColdVariant;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractEggEntity extends ProjectileItemEntity {
   public AbstractEggEntity(EntityType<? extends AbstractEggEntity> egg, World world) {
      super(egg, world);
   }

   public AbstractEggEntity(World world, LivingEntity owner) {
      super(EntityType.EGG, owner, world);
   }

   public AbstractEggEntity(World world, double x, double y, double z) {
      super(EntityType.EGG, x, y, z, world);
   }

   public AbstractEggEntity(EntityType<? extends AbstractEggEntity> egg, World world, LivingEntity owner) {
      super(egg, owner, world);
   }

   public AbstractEggEntity(EntityType<? extends AbstractEggEntity> egg, World world, double x, double y, double z) {
      super(egg, x, y, z, world);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte b) {
      if (b == 3) {
         double d0 = 0.08D;

         for(int i = 0; i < 8; ++i) {
            this.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
         }
      }

   }

   protected void onHitEntity(EntityRayTraceResult result) {
      super.onHitEntity(result);
      result.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
   }

   protected void onHit(RayTraceResult result) {
      super.onHit(result);
      if (!this.level.isClientSide) {
         if (this.random.nextInt(8) == 0) {
            int i = 1;
            if (this.random.nextInt(32) == 0) {
               i = 4;
            }

            for(int j = 0; j < i; ++j) {
               ChickenEntity chickenentity = EntityType.CHICKEN.create(this.level);
               chickenentity.setAge(-24000);
               chickenentity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
               chickenentity.setVariant(this.getChickenType());
               this.level.addFreshEntity(chickenentity);
            }
         }

         this.level.broadcastEntityEvent(this, (byte)3);
         this.remove();
      }

   }

   abstract public WarmColdVariant getChickenType();

   protected Item getDefaultItem() {
      return Items.EGG;
   }
}