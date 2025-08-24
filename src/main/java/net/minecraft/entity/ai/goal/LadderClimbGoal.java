package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Mob;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class LadderClimbGoal extends Goal {

    private final Mob entity;
    private Path path;

    public LadderClimbGoal(Mob entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.entity.getNavigation().getPath() != null) {
            this.path = this.entity.getNavigation().getPath();
            return this.path != null && this.entity.onLadder();
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.path != null) {
            int currentIndex = this.path.getNextNodeIndex();
            if (currentIndex + 1 < this.path.getNodeCount()) {
                // Get the current and next path points
                PathPoint currentPoint = this.path.getNode(currentIndex);
                PathPoint nextPoint = this.path.getNode(currentIndex + 1);

                // Get the current Y position of the entity
                int currentY = currentPoint.y;
                int nextY = nextPoint.y;

                // Check the block state below the entity
                BlockPos entityPos = this.entity.blockPosition();
                BlockPos blockBelow = entityPos.below();
                BlockState blockStateBelow = this.entity.level.getBlockState(blockBelow);

                // Determine vertical motion based on path direction
                double yMotion;
                if (nextY < currentY || (nextY == currentY && !blockStateBelow.getBlock().is(Blocks.LADDER))) {
                    yMotion = -0.15; // Descend
                } else {
                    yMotion = 0.15;  // Ascend
                }

                // Apply motion to the entity
                this.entity.setDeltaMovement(this.entity.getDeltaMovement().multiply(0.1, 1, 0.1));
                this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(0, yMotion, 0));
            }
        }
    }

}