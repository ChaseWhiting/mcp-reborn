package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class GildedRavagerEntity extends AbstractRaiderEntity {
   private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = (p_213685_0_) -> {
      return p_213685_0_.isAlive() && !(p_213685_0_ instanceof GildedRavagerEntity) && !(p_213685_0_ instanceof RavagerEntity);
   };
   private static final byte ATTACK_BYTE = (byte) 7910;
   private static final byte KNOCKBACK_BYTE = (byte) 7911;
   private int attackTick;
   private int stunnedTick;
   private int roarTick;

   public GildedRavagerEntity(EntityType<? extends GildedRavagerEntity> ravager, World world) {
      super(ravager, world);
      this.maxUpStep = 1.0F;
      this.xpReward = 45;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(4, new GildedRavagerEntity.AttackGoal());
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.4D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
   }

   protected void updateControlFlags() {
      boolean flag = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
      boolean flag1 = !(this.getVehicle() instanceof BoatEntity);
      this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
      this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
      this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
      this.goalSelector.setControlFlag(Goal.Flag.TARGET, flag);
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Monster.createMonsterAttributes()
              .add(Attributes.MAX_HEALTH, 120.0D) // Reduced from 145.0D
              .add(Attributes.MOVEMENT_SPEED, 0.34D) // Slightly reduced from 0.34D
              .add(Attributes.ARMOR, 14D) // Reduced from 16D
              .add(Attributes.ARMOR_TOUGHNESS, 10D) // Reduced from 12D
              .add(Attributes.KNOCKBACK_RESISTANCE, 0.85D) // Reduced from 1D
              .add(Attributes.ATTACK_DAMAGE, 18.0D) // Slightly reduced from 15.0D
              .add(Attributes.ATTACK_KNOCKBACK, 1.6D) // Reduced from 1.7D
              .add(Attributes.FOLLOW_RANGE, 54.0D); // Reduced from 64.0D
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("AttackTick", this.attackTick);
      p_213281_1_.putInt("StunTick", this.stunnedTick);
      p_213281_1_.putInt("RoarTick", this.roarTick);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.attackTick = p_70037_1_.getInt("AttackTick");
      this.stunnedTick = p_70037_1_.getInt("StunTick");
      this.roarTick = p_70037_1_.getInt("RoarTick");
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.RAVAGER_CELEBRATE;
   }

   protected PathNavigator createNavigation(World p_175447_1_) {
      return new GildedRavagerEntity.Navigator(this, p_175447_1_);
   }

   public int getMaxHeadYRot() {
      return 45;
   }

   public double getPassengersRidingOffset() {
      return 2.34D;
   }

   public boolean canBeControlledByRider() {
      return !this.isNoAi() && this.getControllingPassenger() instanceof LivingEntity;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
   }

   public void aiStep() {
      super.aiStep();
      if (this.isAlive()) {
         if (this.isImmobile()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
         } else {
            double d0 = this.getTarget() != null ? 0.35D : 0.3D;
            double d1 = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1D, d1, d0));
         }

         if (this.horizontalCollision && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            boolean flag = false;
            AxisAlignedBB axisalignedbb = this.getBoundingBox().inflate(0.2D);

            for(BlockPos blockpos : BlockPos.betweenClosed(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ))) {
               BlockState blockstate = this.level.getBlockState(blockpos);
               Block block = blockstate.getBlock();
               if (block instanceof LeavesBlock) {
                  flag = this.level.destroyBlock(blockpos, true, this) || flag;
               }
            }

            if (!flag && this.onGround) {
               this.jumpFromGround();
            }
         }

         if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
               this.roar();
            }
         }

         if (this.attackTick > 0) {
            --this.attackTick;
         }

         if (this.stunnedTick > 0) {
            --this.stunnedTick;
            this.stunEffect();
            if (this.stunnedTick == 0) {
               this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
               this.roarTick = 20;
            }
         }

      }
   }

   private void stunEffect() {
      if (this.random.nextInt(6) == 0) {
         double d0 = this.getX() - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
         double d1 = this.getY() + (double)this.getBbHeight() - 0.3D;
         double d2 = this.getZ() + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
      }

   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
   }

   public boolean canSee(Entity p_70685_1_) {
      return this.stunnedTick <= 0 && this.roarTick <= 0 ? super.canSee(p_70685_1_) : false;
   }

   protected void blockedByShield(LivingEntity p_213371_1_) {
      if (this.roarTick == 0) {
         if (this.random.nextDouble() < 0.5D) {
            this.stunnedTick = 40;
            this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 0.67F);
            this.level.broadcastEntityEvent(this, KNOCKBACK_BYTE);
            p_213371_1_.push(this);
         } else {
            this.strongKnockback(p_213371_1_);
         }

         p_213371_1_.hurtMarked = true;
      }

   }

   private void roar() {
      if (this.isAlive()) {
         for(Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), NO_RAVAGER_AND_ALIVE)) {
            if (!(entity instanceof AbstractIllagerEntity) && !(entity instanceof WolfEntity)) {
               entity.hurt(DamageSource.mobAttack(this), 8.0F);
               this.strongKnockback(entity);
            }


         }

         Vector3d vector3d = this.getBoundingBox().getCenter();


         for(int i = 0; i < 40; ++i) {
            double d0 = this.random.nextGaussian() * 0.2D;
            double d1 = this.random.nextGaussian() * 0.2D;
            double d2 = this.random.nextGaussian() * 0.2D;
            this.level.addParticle(ParticleTypes.POOF, vector3d.x, vector3d.y, vector3d.z, d0, d1, d2);
         }
         this.gameEvent(GameEvent.ENTITY_ROAR);
         List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0D), entity -> entity != this);
         if(!entities.isEmpty()) {
            for (Entity entity : entities) {
               if (entity instanceof AbstractIllagerEntity || entity instanceof WolfEntity) {
                  ((Mob) entity).addEffect(new EffectInstance(Effects.ABSORPTION, 30 * 20, 4, false, true));
               }
            }
         }
         if(!entities.isEmpty()) {
            int size = random.nextInt(entities.size());
            if (size <= 0) {
               size = 1;
            }
            for(int i = 0; i < 4 * entities.size() ; ++i) {
               AxisAlignedBB bound = this.getBoundingBox();
               long seed = 0L;
               if (!this.level.isClientSide) {
                  seed = ((ServerWorld)this.level).getSeed();
               }
               if(seed != 0 && seed * (long) size > Long.MAX_VALUE) {
                  long weight = seed * (long) size;
                  while (weight > Long.MAX_VALUE) {
                     size -= 30;
                  }
               }
               Random xtra = new Random(seed == 0L ? random.nextLong() : seed * (long) size);
               Random xtra2 = new Random(this.level.getDayTime());
               Vector3d randomVec = getRandomVec(bound, xtra, xtra2);

               double d0 =  xtra.nextGaussian() * 0.2D;
               double d1 = xtra2.nextGaussian() * 0.2D;
               double d2 = xtra.nextGaussian() * 0.2D;
               this.level.addParticle(ParticleTypes.TOTEM_OF_UNDYING, randomVec.x, randomVec.y, randomVec.z, d0, d1, d2);
            }
         }
      }

   }

   public Vector3d getRandomVec(AxisAlignedBB bound, Random xtra, Random xtra2) {
      // Calculate the size of the bounding box along each axis
      int sizeX = (int) (bound.maxX - bound.minX);
      int sizeY = (int) (bound.maxY - bound.minY);
      int sizeZ = (int) (bound.maxZ - bound.minZ);

      // Ensure sizes are positive before generating random numbers
      sizeX = Math.max(sizeX, 1);
      sizeY = Math.max(sizeY, 1);
      sizeZ = Math.max(sizeZ, 1);

      // Generate random coordinates within the bounding box
      double randomX = bound.minX + xtra2.nextInt(sizeX);
      double randomY = bound.minY + xtra.nextInt(sizeY);
      double randomZ = bound.minZ + xtra2.nextInt(sizeZ);

      return new Vector3d(randomX, randomY, randomZ);
   }

   private void strongKnockback(Entity entity) {
      double d0 = entity.getX() - this.getX();
      double d1 = entity.getZ() - this.getZ();
      double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
      double upwardForce = 1.0D; // Increase this value to make the knockback stronger upwards
      entity.push(d0 / d2 * 4.0D, upwardForce, d1 / d2 * 4.0D);
      entity.hurtMarked = true;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte event) {
      if (event == ATTACK_BYTE) {
         this.attackTick = 25;
         this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 0.7F);
      } else if (event == KNOCKBACK_BYTE) {
         this.stunnedTick = 60;
      }

      super.handleEntityEvent(event);
   }

   public float getVoicePitch() {
      return super.getVoicePitch() - 0.2F;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAttackTick() {
      return this.attackTick;
   }

   @OnlyIn(Dist.CLIENT)
   public int getStunnedTick() {
      return this.stunnedTick;
   }

   @OnlyIn(Dist.CLIENT)
   public int getRoarTick() {
      return this.roarTick;
   }

   public boolean doHurtTarget(Entity target) {
      this.attackTick = 25;
      this.level.broadcastEntityEvent(this, ATTACK_BYTE);
      this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
      return super.doHurtTarget(target);
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.RAVAGER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.RAVAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.RAVAGER_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.RAVAGER_STEP, 0.15F, 0.98F);
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return !p_205019_1_.containsAnyLiquid(this.getBoundingBox());
   }

   public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
   }

   public boolean canBeLeader() {
      return false;
   }

   class AttackGoal extends MeleeAttackGoal {
      public AttackGoal() {
         super(GildedRavagerEntity.this, 0.75D, true);
      }

      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
         float f = GildedRavagerEntity.this.getBbWidth() - 0.1F;
         return (double)(f * 2.0F * f * 2.0F + p_179512_1_.getBbWidth());
      }
   }

   static class Navigator extends GroundPathNavigator {
      public Navigator(Mob p_i50754_1_, World p_i50754_2_) {
         super(p_i50754_1_, p_i50754_2_);
      }

      protected PathFinder createPathFinder(int p_179679_1_) {
         this.nodeEvaluator = new GildedRavagerEntity.Processor();
         return new PathFinder(this.nodeEvaluator, p_179679_1_);
      }
   }

   static class Processor extends WalkNodeProcessor {
      private Processor() {
      }

      protected PathNodeType evaluateBlockPathType(IBlockReader p_215744_1_, boolean p_215744_2_, boolean p_215744_3_, BlockPos p_215744_4_, PathNodeType p_215744_5_) {
         return p_215744_5_ == PathNodeType.LEAVES ? PathNodeType.OPEN : super.evaluateBlockPathType(p_215744_1_, p_215744_2_, p_215744_3_, p_215744_4_, p_215744_5_);
      }
   }
}