package net.minecraft.entity.pathfinding.owl;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class OwlWaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomWalkingGoal {
    public OwlWaterAvoidingRandomFlyingGoal(CreatureEntity mob, double speed) {
        super(mob, speed);
    }

    @Nullable
    @Override
    protected Vector3d getPosition() {
        Vector3d vector3d = null;
        if (this.mob.isInWater()) {
            vector3d = RandomPositionGenerator.getLandPos(this.mob, 15, 15);
        }

        if (this.mob.getRandom().nextFloat() >= this.probability) {
            vector3d = this.getTreePos();
        }

        return vector3d == null ? super.getPosition() : vector3d;
    }

    @Nullable
    private Vector3d getTreePos() {
        BlockPos blockpos = this.mob.blockPosition();
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        BlockPos.Mutable mutablePosDown = new BlockPos.Mutable();

        for (BlockPos targetPos : BlockPos.betweenClosed(MathHelper.floor(this.mob.getX() - 3.0D), MathHelper.floor(this.mob.getY() - 6.0D), MathHelper.floor(this.mob.getZ() - 3.0D), MathHelper.floor(this.mob.getX() + 3.0D), MathHelper.floor(this.mob.getY() + 6.0D), MathHelper.floor(this.mob.getZ() + 3.0D))) {
            if (!blockpos.equals(targetPos)) {
                Block block = this.mob.level.getBlockState(mutablePosDown.setWithOffset(targetPos, Direction.DOWN)).getBlock();
                boolean isPerch = block instanceof LeavesBlock || block.is(BlockTags.LOGS);
                if (isPerch && this.mob.level.isEmptyBlock(targetPos) && this.mob.level.isEmptyBlock(mutablePos.setWithOffset(targetPos, Direction.UP))) {
                    return Vector3d.atBottomCenterOf(targetPos);
                }
            }
        }

        return null;
    }
}
