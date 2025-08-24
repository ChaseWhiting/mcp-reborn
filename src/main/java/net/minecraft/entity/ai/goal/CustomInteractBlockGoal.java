package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Mob;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public abstract class CustomInteractBlockGoal extends Goal {
   protected Mob mob;
   protected BlockPos blockPos = BlockPos.ZERO;
   protected boolean hasTargetBlock;
   private boolean passed;
   private float blockOpenDirX;
   private float blockOpenDirZ;
   protected Predicate<BlockState> blockPredicate;

   public CustomInteractBlockGoal(Mob mob, Predicate<BlockState> blockPredicate) {
      this.mob = mob;
      this.blockPredicate = blockPredicate;
      if (!GroundPathHelper.hasGroundPathNavigation(mob)) {
         throw new IllegalArgumentException("Unsupported mob type for BlockInteractGoal");
      }
   }

   protected boolean isBlockInteractable() {
      if (!this.hasTargetBlock) {
         return false;
      } else {
         BlockState blockstate = this.mob.level.getBlockState(this.blockPos);
         if (!this.blockPredicate.test(blockstate)) {
            this.hasTargetBlock = false;
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean canUse() {
      if (!GroundPathHelper.hasGroundPathNavigation(this.mob)) {
         return false;
      } else {
         GroundPathNavigator groundpathnavigator = (GroundPathNavigator) this.mob.getNavigation();
         Path path = groundpathnavigator.getPath();
         if (path != null && !path.isDone()) {
            for (int i = 0; i < Math.min(path.getNextNodeIndex() + 3, path.getNodeCount()); ++i) {
               PathPoint pathpoint = path.getNode(i);
               this.blockPos = new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z);
               if (!(this.mob.distanceToSqr((double) this.blockPos.getX(), this.mob.getY(), (double) this.blockPos.getZ()) > 3.3D)) {
                  this.hasTargetBlock = this.blockPredicate.test(this.mob.level.getBlockState(this.blockPos));
                  if (this.hasTargetBlock) {
                     return true;
                  }
               }
            }
            return false;
         } else {
            return false;
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.passed;
   }

   public void start() {
      this.passed = false;
      this.blockOpenDirX = (float) ((double) this.blockPos.getX() + 0.5D - this.mob.getX());
      this.blockOpenDirZ = (float) ((double) this.blockPos.getZ() + 0.5D - this.mob.getZ());
   }

   public void tick() {
      float f = (float) ((double) this.blockPos.getX() + 0.5D - this.mob.getX());
      float f1 = (float) ((double) this.blockPos.getZ() + 0.5D - this.mob.getZ());
      float f2 = this.blockOpenDirX * f + this.blockOpenDirZ * f1;
      if (f2 < 0.0F) {
         this.passed = true;
      }
   }
}
