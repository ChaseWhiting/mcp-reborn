package net.minecraft.pathfinding;


import net.minecraft.entity.Mob;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class AmphibiousPathNavigation
extends PathNavigator {
    public AmphibiousPathNavigation(Mob mob, World level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int n) {
        this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, n);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    @Override
    protected Vector3d getTempMobPos() {
        return new Vector3d(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
    }


    protected double getGroundY(Vector3d vec3) {
        return vec3.y;
    }

    @Override
    protected boolean canMoveDirectly(Vector3d vec3, Vector3d vec32, int a, int b, int c) {
        if (this.isInLiquid()) {
            return AmphibiousPathNavigation.isClearForMovementBetween(this.mob, vec3, vec32, false);
        }
        return false;
    }

    @Override
    public boolean isStableDestination(BlockPos blockPos) {
        return !this.level.getBlockState(blockPos.below()).isAir();
    }

    @Override
    public void setCanFloat(boolean bl) {
    }
}

