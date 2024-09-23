package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.entity.Creature;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.vector.Vector3d;

public class AvoidEntityGoal<T extends LivingEntity> extends Goal {
   protected final Creature mob;
   private final double walkSpeedModifier;
   private final double sprintSpeedModifier;
   protected T toAvoid;
   protected final float maxDist;
   protected Path path;
   protected final PathNavigator pathNav;
   protected final Class<T> avoidClass;
   protected final Predicate<LivingEntity> avoidPredicate;
   protected final Predicate<LivingEntity> predicateOnAvoidEntity;
   private final EntityPredicate avoidEntityTargeting;

   public AvoidEntityGoal(Creature creature, Class<T> avoidClass, float maxDistance, double walkSpeedModifier, double sprintSpeedModifier) {
      this(creature, avoidClass, (entity) -> true, maxDistance, walkSpeedModifier, sprintSpeedModifier, EntityPredicates.NO_CREATIVE_OR_SPECTATOR::test);
   }

   public AvoidEntityGoal(Creature creature, Class<T> avoidClass, Predicate<LivingEntity> avoidPredicate, float maxDistance, double walkSpeedModifier, double sprintSpeedModifier, Predicate<LivingEntity> additionalPredicate) {
      this.mob = creature;
      this.avoidClass = avoidClass;
      this.avoidPredicate = avoidPredicate;
      this.maxDist = maxDistance;
      this.walkSpeedModifier = walkSpeedModifier;
      this.sprintSpeedModifier = sprintSpeedModifier;
      this.predicateOnAvoidEntity = additionalPredicate;
      this.pathNav = creature.getNavigation();
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      this.avoidEntityTargeting = (new EntityPredicate()).range((double) maxDistance).selector(additionalPredicate.and(avoidPredicate));
   }

   public AvoidEntityGoal(Creature mob, Class<T> mobClass, float maxDis, double speedMod, double sprintMod, Predicate<LivingEntity> additionalPred) {
      this(mob, mobClass, (entity) -> {
         return true;
      }, maxDis, speedMod, sprintMod, additionalPred);
   }

   public AvoidEntityGoal(Creature mob, Class<T> mobClass, float maxDis, double speedMod, Predicate<LivingEntity> additionalPred) {
      this(mob, mobClass, (entity) -> {
         return true;
      }, maxDis, speedMod, speedMod, additionalPred);
   }

   public boolean canUse() {
      this.toAvoid = this.mob.level.getNearestLoadedEntity(this.avoidClass, this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0D, (double)this.maxDist));
      if (this.toAvoid == null) {
         return false;
      } else {
         Vector3d vector3d = RandomPositionGenerator.getPosAvoid(this.mob, 16, 7, this.toAvoid.position());
         if (vector3d == null) {
            return false;
         } else if (this.toAvoid.distanceToSqr(vector3d.x, vector3d.y, vector3d.z) < this.toAvoid.distanceToSqr(this.mob)) {
            return false;
         } else {
            this.path = this.pathNav.createPath(vector3d.x, vector3d.y, vector3d.z, 0);
            return this.path != null;
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.pathNav.isDone();
   }

   public void start() {
      this.pathNav.moveTo(this.path, this.walkSpeedModifier);
   }

   public void stop() {
      this.toAvoid = null;
   }

   public void tick() {
      if (this.mob.distanceToSqr(this.toAvoid) < 49.0D) {
         this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
      } else {
         this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
      }

   }
}