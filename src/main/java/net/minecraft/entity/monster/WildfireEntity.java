package net.minecraft.entity.monster;

import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class WildfireEntity extends Monster {
   private float allowedHeightOffset = 0.5F;
   private int nextHeightOffsetChangeTick;
   private static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(WildfireEntity.class, DataSerializers.BYTE);
   private static final DataParameter<Boolean> IS_DYING = EntityDataManager.defineId(WildfireEntity.class, DataSerializers.BOOLEAN);
   public final AnimationState deathAnimationState = new AnimationState();
   public final AnimationState chargeState = new AnimationState();
   public final AnimationState shieldsSpin = new AnimationState();

   public WildfireEntity(EntityType<? extends WildfireEntity> p_i50215_1_, World p_i50215_2_) {
      super(p_i50215_1_, p_i50215_2_);


      this.setPathfindingMalus(PathNodeType.WATER, -1.0F);
      this.setPathfindingMalus(PathNodeType.LAVA, 8.0F);
      this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0.0F);
      this.xpReward = 40;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new WildfireEntity.FireballAttackGoal(this));
      this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 0.0F));
      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));

      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, VillagerEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));

   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 85).add(Attributes.ARMOR, 30).add(Attributes.ATTACK_DAMAGE, 12.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F).add(Attributes.FOLLOW_RANGE, 64.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
      this.entityData.define(IS_DYING, false);
   }

   public boolean isDying() {
      return this.entityData.get(IS_DYING);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.BLAZE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.BLAZE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BLAZE_DEATH;
   }

   public float getBrightness() {
      return 1.0F;
   }

   public void tick() {
      super.tick();

      if (level.isClientSide) {
         this.chargeState.animateWhen(this.isCharged(), this.tickCount);
         this.shieldsSpin.animateWhen(true, this.tickCount);
      }
      this.entityData.set(IS_DYING, this.deathTime > 0);

   }


   protected void tickDeath() {
      ++this.deathTime;
      this.deathAnimationState.animateWhen(this.isDying(), this.tickCount);
      if (this.deathTime == 35) {
         this.remove();

         for (int i = 0; i < 20; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.POOF, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), d0, d1, d2);
         }
      }

   }

   public void aiStep() {
      if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
      }

      if (this.level.isClientSide) {
         if (this.random.nextInt(24) == 0 && !this.isSilent()) {
            this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
         }

         for(int i = 0; i < 2; ++i) {
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
         }
      }

      super.aiStep();
   }

   public boolean isSensitiveToWater() {
      return true;
   }

   protected void customServerAiStep() {
      --this.nextHeightOffsetChangeTick;
      if (this.nextHeightOffsetChangeTick <= 0) {
         this.nextHeightOffsetChangeTick = 100;
         if (veryHardmode()) {
            this.allowedHeightOffset = 1F + (float)this.random.nextGaussian() * 6.0F;
         } else {
            this.allowedHeightOffset = 0.5F + (float)this.random.nextGaussian() * 3.0F;

         }
      }

      LivingEntity livingentity = this.getTarget();
      if (livingentity != null && livingentity.getEyeY() > this.getEyeY() + (double)this.allowedHeightOffset && this.canAttack(livingentity)) {
         Vector3d vector3d = this.getDeltaMovement();
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, ((double)0.3F - vector3d.y) * (double)0.3F, 0.0D));
         this.hasImpulse = true;
      }

      super.customServerAiStep();
   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   public boolean isOnFire() {
      return this.isCharged();
   }

   private boolean isCharged() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   private void setCharged(boolean p_70844_1_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_70844_1_) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.entityData.set(DATA_FLAGS_ID, b0);
   }

   static class FireballAttackGoal extends Goal {
      private final WildfireEntity blaze;
      private int attackStep;
      private int attackTime;
      private int lastSeen;

      public FireballAttackGoal(WildfireEntity p_i45846_1_) {
         this.blaze = p_i45846_1_;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.blaze.hasEffect(Effects.CONFUSED)) return false;

         LivingEntity livingentity = this.blaze.getTarget();
         return livingentity != null && livingentity.isAlive() && this.blaze.canAttack(livingentity);
      }

      public void start() {
         this.attackStep = 0;
      }

      public void stop() {
         this.blaze.setCharged(false);
         this.lastSeen = 0;
      }

      public void tick() {
         --this.attackTime;
         LivingEntity livingentity = this.blaze.getTarget();
         if (livingentity != null) {
            boolean flag = this.blaze.getSensing().canSee(livingentity);
            if (flag) {
               this.lastSeen = 0;
            } else {
               ++this.lastSeen;
            }

            double d0 = this.blaze.distanceToSqr(livingentity);
            if (d0 < 4.0D) {
               if (!flag) {
                  return;
               }

               if (this.attackTime <= 0) {
                  this.attackTime = 20;
                  this.blaze.doHurtTarget(livingentity);
               }

               this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
            } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
               double d1 = livingentity.getX() - this.blaze.getX();
               double d2 = livingentity.getY(0.5D) - this.blaze.getY(0.5D);
               double d3 = livingentity.getZ() - this.blaze.getZ();
               boolean hard = this.blaze.veryHardmode();
               if (this.attackTime <= 0) {
                  ++this.attackStep;
                  if (this.attackStep == 1) {
                     if (hard) {
                        this.attackTime = 40;
                     } else {
                        this.attackTime = 60;
                     }
                     this.blaze.setCharged(true);
                  } else if (hard ? this.attackStep <= 6 : this.attackStep <= 4) {
                     if (hard) {
                        this.attackTime = 4;
                     } else {
                        this.attackTime = 6;
                     }
                  } else {
                     if (hard) {
                        this.attackTime = 60;
                     } else {
                        this.attackTime = 100;
                     }
                     this.attackStep = 0;
                     this.blaze.setCharged(false);
                  }

                  if (this.attackStep > 1) {

                     float f = hard ? MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.3F : MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5F;
                     if (!this.blaze.isSilent()) {
                        this.blaze.level.levelEvent((PlayerEntity)null, 1018, this.blaze.blockPosition(), 0);
                     }

                     for(int i = 0; i < 1; ++i) {
                        SmallFireballEntity smallfireballentity = new SmallFireballEntity(this.blaze.level, this.blaze, d1 + this.blaze.getRandom().nextGaussian() * (double)f, d2, d3 + this.blaze.getRandom().nextGaussian() * (double)f);
                        smallfireballentity.setPos(smallfireballentity.getX(), this.blaze.getY(0.5D) + 0.5D, smallfireballentity.getZ());
                        this.blaze.level.addFreshEntity(smallfireballentity);
                     }
                  }
               }

               this.blaze.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
               this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
            }

            super.tick();
         }
      }

      private double getFollowDistance() {
         return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
      }
   }
}