package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.Mob;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.util.math.BlockPos;

public class ClimbLadderGoal extends Goal {
   private final Mob mob;
   private BlockPos ladderPos = BlockPos.ZERO;
   private boolean hasLadder;

   public ClimbLadderGoal(Mob mob) {
      this.mob = mob;
      if (!GroundPathHelper.hasGroundPathNavigation(mob)) {
         throw new IllegalArgumentException("Unsupported mob type for ClimbLadderGoal");
      }
   }

   public boolean canUse() {
      // Check if the mob can use this goal and if it's near a ladder
      GroundPathNavigator navigator = (GroundPathNavigator) mob.getNavigation();
      Path path = navigator.getPath();
      if (path != null && !path.isDone()) {
         for (int i = 0; i < Math.min(path.getNextNodeIndex() + 3, path.getNodeCount()); ++i) {
            PathPoint pathpoint = path.getNode(i);
            BlockPos pos = new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z);
            BlockState state = this.mob.level.getBlockState(pos);

            // Check if the block is a ladder
            if (state.getBlock() instanceof LadderBlock) {
               this.ladderPos = pos;
               this.hasLadder = true;
               return true;
            }
         }
      }
      return false;
   }

   public void start() {
      // Move towards the ladder base to start climbing
      this.mob.getNavigation().moveTo(this.ladderPos.getX(), this.ladderPos.getY(), this.ladderPos.getZ(), 1.0D);
   }

   public boolean canContinueToUse() {
      // Continue climbing if the mob is near the ladder and hasn't reached the top
      return this.hasLadder && this.mob.getNavigation().isInProgress();
   }

   public void tick() {
      BlockState state = this.mob.level.getBlockState(this.ladderPos);
      if (state.getBlock() instanceof LadderBlock) {
         LadderBlock ladder = (LadderBlock) state.getBlock();
         // Move the mob in the center of the ladder path based on the ladder's direction
         moveMobToLadderCenter(state);

         // Start climbing the ladder
         this.mob.getMoveControl().setWantedPosition(ladderPos.getX(), ladderPos.getY() + 1, ladderPos.getZ(), 1.0D);
      }
   }

   private void moveMobToLadderCenter(BlockState ladder) {
      // Adjust the mob's position to align with the center of the ladder
      switch (ladder.getValue(LadderBlock.FACING)) {
         case NORTH:
         case SOUTH:
            //this.mob.getMoveControl().setWan(this.ladderPos.getX() + 0.5);  // Center mob on X-axis
            break;
         case EAST:
         case WEST:
            //this.mob.setZ(this.ladderPos.getZ() + 0.5);  // Center mob on Z-axis
            break;
         default:
            break;
      }
   }

   public void stop() {
      // Reset when done
      this.hasLadder = false;
   }
}
