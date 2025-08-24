package net.minecraft.entity.item;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class InfernalFireballEntity extends ProjectileItemEntity {

   public InfernalFireballEntity(EntityType<? extends InfernalFireballEntity> p_i50160_1_, World p_i50160_2_) {
      super(p_i50160_1_, p_i50160_2_);
   }

   public InfernalFireballEntity(World p_i1786_1_, LivingEntity p_i1786_2_) {
      super(EntityType.INFERNAL_FIREBALL, p_i1786_2_, p_i1786_1_);
   }

   public InfernalFireballEntity(World p_i1787_1_, double p_i1787_2_, double p_i1787_4_, double p_i1787_6_) {
      super(EntityType.INFERNAL_FIREBALL, p_i1787_2_, p_i1787_4_, p_i1787_6_, p_i1787_1_);
   }

   protected Item getDefaultItem() {
      return Items.FIRE_CHARGE;
   }

   protected float getGravity() {
      return 0.07F;
   }

   protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
      super.onHitBlock(p_230299_1_);
      if (!this.level.isClientSide) {
            BlockPos blockpos = p_230299_1_.getBlockPos().relative(p_230299_1_.getDirection());
            if (this.level.isEmptyBlock(blockpos)) {
               this.level.setBlockAndUpdate(blockpos, Blocks.HELLFIRE.defaultBlockState());
            }


      }
   }

   protected void onHit(RayTraceResult p_70227_1_) {
      super.onHit(p_70227_1_);
      if (!this.level.isClientSide) {
         if (p_70227_1_ instanceof EntityRayTraceResult && ((EntityRayTraceResult) p_70227_1_).getEntity() instanceof InfernalFireballEntity) return;

         this.remove();
      }

   }

   protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
      super.onHitEntity(p_213868_1_);
      if (!this.level.isClientSide) {
         Entity entity = p_213868_1_.getEntity();
         if (!entity.fireImmune()) {
            Entity entity1 = this.getOwner();
            int i = entity.getRemainingFireTicks();
            entity.setSecondsOnFire(10);
            boolean flag = entity.hurt(DamageSource.hellfire(this, entity1), 8.0F);
            if (!flag) {
               entity.setRemainingFireTicks(i);
            } else if (entity1 instanceof LivingEntity) {
               this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }
         }

      }
   }

}