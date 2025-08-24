package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class AdvancedCreeperSwellGoal extends Goal {
   private final CreeperEntity creeper;
   private LivingEntity target;

   public AdvancedCreeperSwellGoal(CreeperEntity creeper) {
      this.creeper = creeper;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }



   @Override
   public void start() {
      this.creeper.getNavigation().stop();
   }

   @Override
   public void stop() {
      this.target = null;
      this.creeper.setSwellDir(-1);
   }

   @Override
   public boolean canUse() {
      if (!this.creeper.veryHardmode()) return false;

      this.target = this.creeper.getTarget();
      if (this.target == null) {
         return false;
      }

      double distance = this.creeper.distanceTo(this.target);
      boolean canSee = this.creeper.getSensing().canSee(this.target);
      Path path = this.creeper.getNavigation().createPath(this.target.blockPosition(), 0);

      // Direct visibility or reachable path - no explosion
      if (canSee || (path != null && path.canReach())) {
         return false;
      }

      // Only consider exploding if there are no paths and an obstruction is detected
      return !canSee && this.canExplodeBasedOnLayer();
   }

   private boolean canExplodeBasedOnLayer() {
      if (this.target == null) {
         return false;
      }

      BlockPos creeperPos = this.creeper.blockPosition();
      BlockPos targetPos = this.target.blockPosition();

      // Check if there is a valid path closer to the target
      Path path = this.creeper.getNavigation().createPath(targetPos, 0);
      if (path != null && path.canReach()) {
         return false; // Path exists, no need to explode
      }

      // Find the closest obstruction
      BlockPos closestObstruction = findClosestObstruction(creeperPos, targetPos);
      if (closestObstruction == null) {
         return false; // No obstruction found, don't explode
      }

      double distanceToObstruction = creeperPos.distSqr(closestObstruction);

      // Allow explosion if the creeper is within 2 blocks of the obstruction
      return distanceToObstruction <= 2.0D;
   }

   private BlockPos findClosestObstruction(BlockPos start, BlockPos end) {
      BlockPos.Mutable mutablePos = new BlockPos.Mutable();
      BlockPos closestObstruction = null;
      double closestDistance = Double.MAX_VALUE;

      // Traverse in a straight line between the creeper and the target
      for (int x = start.getX(); x != end.getX(); x += Integer.signum(end.getX() - start.getX())) {
         for (int y = start.getY(); y != end.getY(); y += Integer.signum(end.getY() - start.getY())) {
            for (int z = start.getZ(); z != end.getZ(); z += Integer.signum(end.getZ() - start.getZ())) {
               mutablePos.set(x, y, z);

               if (this.creeper.level.getBlockState(mutablePos).isSolidRender(creeper.level, mutablePos.immutable())) {
                  double distance = start.distSqr(mutablePos);
                  if (distance < closestDistance) {
                     closestDistance = distance;
                     closestObstruction = mutablePos.immutable(); // Immutable to prevent changes
                  }
               }
            }
         }
      }

      return closestObstruction;
   }

   @Override
   public void tick() {
      if (this.target == null) {
         this.creeper.setSwellDir(-1);
         return;
      }

      double distance = this.creeper.distanceTo(this.target);
      boolean canSee = this.creeper.getSensing().canSee(this.target);
      Path path = this.creeper.getNavigation().createPath(this.target.blockPosition(), 0);

      if (distance < 5.0D && canSee) {
         this.creeper.setSwellDir(1);
      } else if (!canSee && (path == null || !path.canReach())) {
         if (this.canExplodeBasedOnLayer()) {
            this.creeper.setSwellDir(1);
         } else {
            this.creeper.setSwellDir(-1);
         }
      } else {
         this.creeper.setSwellDir(-1);
      }
   }

}
