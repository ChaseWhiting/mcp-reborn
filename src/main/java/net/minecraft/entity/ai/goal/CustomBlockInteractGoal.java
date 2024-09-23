package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Mob;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class CustomBlockInteractGoal extends Goal {
   protected Mob mob;
   protected BlockPos blockPos = BlockPos.ZERO;
   protected boolean hasBlock;
   protected Predicate<BlockState> statePredicate;

   public CustomBlockInteractGoal(Mob mob, Predicate<BlockState> blockStatePredicate) {
      this.mob = mob;
      this.statePredicate = blockStatePredicate;
      if (!(mob.getNavigation() instanceof GroundPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for FenceInteractGoal");
      }
   }

   protected boolean isValid() {
      if (!this.hasBlock) {
         return false;
      } else {
         BlockState blockstate = this.mob.level.getBlockState(this.blockPos);
         if (!statePredicate.test(blockstate)) {
            this.hasBlock = false;
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean canUse() {
      if (!(this.mob.getNavigation() instanceof GroundPathNavigator)) {
         return false;
      } else {

         // Get the block directly in front of the mob
         BlockPos targetPos = this.blockPos.relative(this.mob.getDirection());

         if (this.mob.getTarget() != null) {
            if (mob.distanceTo(this.mob.getTarget()) < 2.4 && !statePredicate.test(this.mob.level.getBlockState(targetPos))) {
               return false;
            }
         }

         // Check if the block in front is valid for breaking
         for (int i = -1; i <3; i++) {
            BlockPos target;
            target = targetPos.offset(0, i, 0);
            BlockState state = this.mob.level.getBlockState(target);
            if (canSee(target)) {
               if (this.statePredicate.test(state)) {
                  this.blockPos = targetPos;
                  return true;
               }
            }
         }

         // Perform the regular search if no block is directly in front
         return this.findBlockOnArea();
      }
   }

   public boolean findBlockInArea() {
      GroundPathNavigator navigator = (GroundPathNavigator)this.mob.getNavigation();
      Path path = navigator.getPath();

      // Check in a 5x5x5 cube centered around the mob
      int radius = 2; // 2 blocks in every direction from the mob (radius of 2 means a 5x5x5 area)
      BlockPos mobPos = this.mob.blockPosition();

      for (int x = -radius; x <= radius; x++) {
         for (int y = -radius; y <= radius; y++) {
            for (int z = -radius; z <= radius; z++) {
               BlockPos blockPos = mobPos.offset(x, y, z);

               if (!(this.mob.distanceToSqr(blockPos.getX(), this.mob.getY(), blockPos.getZ()) > 12.3D)) {
                  this.hasBlock = this.statePredicate.test(this.mob.level.getBlockState(this.blockPos));

                  if (canSee(blockPos)) {
                     if (this.hasBlock) {
                        this.blockPos = blockPos;
                        return true;
                     }
                  }
               }
            }
         }
      }

      // If no fence block is found, default to the mob's current position
      this.blockPos = this.mob.blockPosition();
      this.hasBlock = this.statePredicate.test(this.mob.level.getBlockState(this.blockPos));
      return this.hasBlock;
   }

   protected boolean canSee(BlockPos pos) {
      // Get the mob's eye position
      Vector3d eyePosition = this.mob.getEyePosition(1.0F);

      // Get the center position of the target block
      Vector3d targetPosition = Vector3d.atCenterOf(pos);

      // Create a ray trace context
      RayTraceContext context = new RayTraceContext(
              eyePosition,
              targetPosition,
              RayTraceContext.BlockMode.COLLIDER,
              RayTraceContext.FluidMode.NONE,
              this.mob
      );

      // Perform the ray trace
      BlockRayTraceResult result = this.mob.level.clip(context);

      // Check if the ray trace hit the target block
      if (result.getType() == RayTraceResult.Type.MISS) {
         // No obstruction; the mob can see the block
         return true;
      } else {
         // Return true if the hit block is the target block
         return result.getBlockPos().equals(pos);
      }
   }

   public boolean findBlockOnArea() {
      int radius = 3;
      boolean found = false;
      BlockPos basePos = this.mob.blockPosition().offset(0, 1, 0); // Offset the initial position once

      // Define possible offsets to check along each axis
      int[][] offsetsArray = {
              {1, 1, 0}, {1, 0, 1}, {0, 1, 1},
              {-1, 1, 1}, {1, 1, -1}, {-1, 1, -1},
              {0, 1, 0}
      };

      // Convert to list and shuffle the offsets to make it random
      List<int[]> offsets = Arrays.asList(offsetsArray);
      Collections.shuffle(offsets);

      // Iterate over the shuffled offsets
      for (int[] offset : offsets) {
         for (int i = -radius; i < radius; i++) {
            BlockPos pos = basePos.offset(i * offset[0], i * offset[1], i * offset[2]);
            if (pos.getY() <= this.mob.getY()) {
               if (this.mob.getRandom().nextInt(3) == 0) return false;
            }
            BlockState state = mob.level.getBlockState(pos);
            if (canSee(pos)) {
               if (statePredicate.test(state)) {
                  this.blockPos = pos;
                  this.hasBlock = true;
                  found = true;
                  break;
               }
            }
         }
         if (found) break; // Stop checking once a block is found
      }

      return found;
   }

   public boolean canContinueToUse() {
      return this.hasBlock;
   }

   public void start() {
      // Add any additional start logic here
   }

   public void tick() {
      // Add any additional tick logic if needed
   }
}