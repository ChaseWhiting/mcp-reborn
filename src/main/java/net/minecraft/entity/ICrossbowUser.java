package net.minecraft.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.herobrine.HerobrineEntity;
import net.minecraft.entity.monster.PillagerCaptainEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.Difficulty;

import java.util.Random;

public interface ICrossbowUser extends IRangedAttackMob {
   void setChargingCrossbow(boolean value);

   void shootCrossbowProjectile(LivingEntity shooter, ItemStack crossbow, ProjectileEntity projectile, float velocity);

   @Nullable
   LivingEntity getTarget();

   void onCrossbowAttackPerformed();

   default void performCrossbowAttack(LivingEntity attacker, float velocity) {
      Hand hand;
      ItemStack crossbow;

      if (attacker.isHolding(Items.CROSSBOW)) {
         hand = ProjectileHelper.getWeaponHoldingHand(attacker, Items.CROSSBOW);
         crossbow = attacker.getItemInHand(hand);
         CrossbowItem.performShooting(attacker.level, attacker, hand, crossbow, velocity, (float)(14 - attacker.level.getDifficulty().getId() * 4));
      } else if (attacker.isHolding(Items.GILDED_CROSSBOW)) {
         hand = ProjectileHelper.getWeaponHoldingHand(attacker, Items.GILDED_CROSSBOW);
         crossbow = attacker.getItemInHand(hand);
         GildedCrossbowItem.performShooting(attacker.level, attacker, hand, crossbow, velocity, (float)(14 - attacker.level.getDifficulty().getId() * 4));
      } else if (AbstractCrossbowItem.isHoldingAbstractCrossbowItem(attacker)) {
         hand = AbstractCrossbowItem.getHandHoldingAbstractCrossbowItem(attacker);
         crossbow = attacker.getItemInHand(hand);
         // Assuming you have a method to handle shooting for AbstractCrossbowItem
         AbstractCrossbowItem.performShooting(attacker.level, attacker, hand, crossbow, velocity, (float)(14 - attacker.level.getDifficulty().getId() * 4));
      }

      this.onCrossbowAttackPerformed();
   }

   default void shootCrossbowProjectile(LivingEntity shooter, LivingEntity target, ProjectileEntity projectileEntity, float inaccuracy, float speed) {
      double d0 = target.getX() - shooter.getX();
      double d1 = target.getZ() - shooter.getZ();
      double d2 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1);
      double d3 = target.getY(0.3333333333333333D) - projectileEntity.getY() + d2 * (double) 0.2F;
      Vector3f vector3f = this.getProjectileShotVector(shooter, new Vector3d(d0, d3, d1), inaccuracy);
      Difficulty dif = shooter.level.getDifficulty();
      int lev = switch(dif) {
         case EASY -> 0;
         case NORMAL -> 1;
         case HARD -> 2;
         case PEACEFUL -> 0;
      };
      if (shooter instanceof PiglinBruteEntity || shooter instanceof HerobrineEntity) {
         if (projectileEntity instanceof AbstractArrowEntity) {
            AbstractArrowEntity arrow = (AbstractArrowEntity) projectileEntity;
            double damage = arrow.getBaseDamage();
            Difficulty difficulty = shooter.level.getDifficulty();
            int arrowLevel = arrow.getPierceLevel();
            arrow.setPierceLevel((byte) (arrowLevel + 2));

            switch (difficulty) {
               case EASY:
               case PEACEFUL:
               case NORMAL:
                  arrow.setBaseDamage(damage + 1.5D);
                  break;
               case HARD:
                  arrow.setBaseDamage(damage + 3.5D);
                  if (new Random().nextBoolean())
                     arrow.setCritArrow(true);
                  break;
            }
            arrow.shoot((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), speed, (float) (14 - shooter.level.getDifficulty().getId() * 4));
         }
      } else if (shooter instanceof PillagerCaptainEntity) {
         if (projectileEntity instanceof ArrowEntity) {
            ArrowEntity arrow = (ArrowEntity) projectileEntity;
            double damage = arrow.getBaseDamage();
            Difficulty difficulty = shooter.level.getDifficulty();
            EffectInstance[] effectInstances = new EffectInstance[]{
                    new EffectInstance(Effects.POISON, 8 * 20, lev),
                    new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * 20, lev),
                    new EffectInstance(Effects.WEAKNESS, 10 * 20, difficulty == Difficulty.EASY ? 0 : lev - 1)};
            Random rand = new Random();
            int randomEffect = (new Random().nextInt(effectInstances.length));
            EffectInstance mainEffect = effectInstances[randomEffect];

            switch (difficulty) {
               case EASY:
               case PEACEFUL:
               case NORMAL:
                  if (rand.nextBoolean()) {
                     arrow.addEffect(mainEffect);
                  }
                  arrow.setBaseDamage(damage + 0.5D);
                  break;
               case HARD:
                  if(rand.nextFloat() < 0.4F) {
                     arrow.addEffect(mainEffect);
                  }
                  arrow.setBaseDamage(damage + 1.5D);
                  if (new Random().nextBoolean())
                     arrow.setCritArrow(true);
                  break;
            }
            arrow.shoot((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), speed, (float) (14 - shooter.level.getDifficulty().getId() * 4));
         }
      } else {
         projectileEntity.shoot((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), speed, (float) (14 - shooter.level.getDifficulty().getId() * 4));
      }
      shooter.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (shooter.getRandom().nextFloat() * 0.4F + 0.8F));
   }


   default Vector3f getProjectileShotVector(LivingEntity shooter, Vector3d direction, float angle) {
      Vector3d vector3d = direction.normalize();
      Vector3d vector3d1 = vector3d.cross(new Vector3d(0.0D, 1.0D, 0.0D));
      if (vector3d1.lengthSqr() <= 1.0E-7D) {
         vector3d1 = vector3d.cross(shooter.getUpVector(1.0F));
      }

      Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), 90.0F, true);
      Vector3f vector3f = new Vector3f(vector3d);
      vector3f.transform(quaternion);
      Quaternion quaternion1 = new Quaternion(vector3f, angle, true);
      Vector3f vector3f1 = new Vector3f(vector3d);
      vector3f1.transform(quaternion1);
      return vector3f1;
   }
}