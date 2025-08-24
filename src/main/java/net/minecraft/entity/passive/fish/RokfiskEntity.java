package net.minecraft.entity.passive.fish;

import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.EntityAINearestTarget3D;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class RokfiskEntity extends AbstractFishEntity {

   public final AnimationState swimState = new AnimationState();
   public final AnimationState flopState = new AnimationState();

   public RokfiskEntity(EntityType<? extends RokfiskEntity> type, World world) {
      super(type, world);

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
      this.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(this, PlayerEntity.class, 10, false, false, player-> {
         return player.isPassenger() && player.getVehicle() instanceof StriderEntity || player.isInLava();
      }));
   }

   public boolean check() {
      return this.isInWater();
   }


   public void tick() {
      super.tick();

      if (this.level.isClientSide) {
         swimState.animateWhen(this.check(), this.tickCount);
         flopState.animateWhen(!this.check(), this.tickCount);
      }
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return AbstractLavaFishEntity.createAttributes().add(Attributes.ATTACK_DAMAGE, 5.5D).add(Attributes.FOLLOW_RANGE, 48D).add(Attributes.MAX_HEALTH, 24.0D).add(Attributes.ARMOR, 5D).add(Attributes.ARMOR_TOUGHNESS, 4D).add(Attributes.KNOCKBACK_RESISTANCE, 0.15D);
   }


   protected ItemStack getBucketItemStack() {
      return new ItemStack(Items.SALMON_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SALMON_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.SALMON_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.SALMON_FLOP;
   }
}