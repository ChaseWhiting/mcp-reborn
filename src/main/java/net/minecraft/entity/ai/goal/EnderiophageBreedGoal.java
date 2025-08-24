package net.minecraft.entity.ai.goal;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.monster.enderiophage.EntityEnderiophage;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class EnderiophageBreedGoal extends Goal {
   protected static final EntityPredicate PARTNER_TARGETING = (new EntityPredicate()).range(128.0D).allowInvulnerable().allowSameTeam().allowUnseeable();
   protected final EntityEnderiophage animal;
   protected final Class<? extends EntityEnderiophage> partnerClass;
   protected final World level;
   protected EntityEnderiophage partner;
   private int loveTime;
   private final double speedModifier;

   public EnderiophageBreedGoal(EntityEnderiophage p_i1619_1_, double p_i1619_2_) {
      this(p_i1619_1_, p_i1619_2_, p_i1619_1_.getClass());
   }

   public EnderiophageBreedGoal(EntityEnderiophage p_i47306_1_, double p_i47306_2_, Class<? extends EntityEnderiophage> p_i47306_4_) {
      this.animal = p_i47306_1_;
      this.level = p_i47306_1_.level;
      this.partnerClass = p_i47306_4_;
      this.speedModifier = p_i47306_2_;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (!this.animal.isInLove()) {
         return false;
      } else {
         this.partner = this.getFreePartner();
         return this.partner != null;
      }
   }

   public boolean canContinueToUse() {
      return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
   }

   public void stop() {
      this.partner = null;
      this.loveTime = 0;
      animal.breedingTimer = 0;
   }

   public void tick() {
      this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
      if (animal.isFlying()) {
         this.animal.getMoveControl().setWantedPosition(this.partner.blockPosition(), 1.3D);
      } else {
//         this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
         animal.setFlying(true);
      }
      ++this.loveTime;
      animal.breedingTimer = this.loveTime;
      if (this.loveTime >= 60 && this.animal.distanceToSqr(this.partner) < 9.0D) {
         this.breed();
      }

   }

   @Nullable
   private EntityEnderiophage getFreePartner() {
      List<EntityEnderiophage> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(128.0D));
      double d0 = Double.MAX_VALUE;
      EntityEnderiophage animalentity = null;

      for(EntityEnderiophage animalentity1 : list) {
         if (this.animal.canMate(animalentity1) && this.animal.distanceToSqr(animalentity1) < d0) {
            animalentity = animalentity1;
            d0 = this.animal.distanceToSqr(animalentity1);
         }
      }

      return animalentity;
   }

   protected void breed() {
      this.animal.spawnChildFromBreeding((ServerWorld)this.level, this.partner);
   }
}