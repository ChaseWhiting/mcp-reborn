package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class AvoidBlockGoal<T extends Block> extends Goal {
   protected final CreatureEntity mob;
   private final double walkSpeedModifier;
   private final double sprintSpeedModifier;
   protected T toAvoid;
   protected final float maxDist;
   protected Path path;
   protected final PathNavigator pathNav;
   protected final Class<T> avoidClass;
   protected final Predicate<BlockState> avoidPredicate;
   protected final Predicate<BlockState> predicateOnAvoidBlock;

   public AvoidBlockGoal(CreatureEntity creature, Class<T> avoidBlockClass, Predicate<BlockState> avoidPredicate, float maxDist, double walkSpeedModifier, double sprintSpeedModifier, Predicate<BlockState> predicateOnAvoidBlock) {
      this.mob = creature;
      this.avoidClass = avoidBlockClass;
      this.avoidPredicate = avoidPredicate;
      this.maxDist = maxDist;
      this.walkSpeedModifier = walkSpeedModifier;
      this.sprintSpeedModifier = sprintSpeedModifier;
      this.pathNav = creature.getNavigation();
       this.predicateOnAvoidBlock = predicateOnAvoidBlock;
       this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      BlockPos blockPosition = this.mob.level.getNearestBlock(this.avoidClass, this.avoidPredicate, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getBoundingBox().inflate((double)this.maxDist, 6.0D, (double)this.maxDist));
      if (blockPosition  == null) {
         return false;
      } else {
         Vector3d vector3d = RandomPositionGenerator.getPosAvoid(this.mob, 16, 7, new Vector3d(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()));
         if (vector3d == null) {
            return false;
         } else if (blockPosition.distSqr(vector3d.x, vector3d.y, vector3d.z, true) < blockPosition.distSqr(this.mob.getX(), this.mob.getY(), this.mob.getZ(), true)) {
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
      BlockPos blockPosition = this.mob.level.getNearestBlock(this.avoidClass, this.avoidPredicate, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0D, (double)this.maxDist));
      if (blockPosition != null && this.mob.distanceToSqr(blockPosition.getX()+0.5D, blockPosition.getY()+0.5D, blockPosition.getZ()+0.5D) < 49.0D) {
         this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
      } else {
         this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
      }

   }
}