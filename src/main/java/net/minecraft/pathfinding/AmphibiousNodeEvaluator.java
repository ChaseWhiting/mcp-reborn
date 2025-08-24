
package net.minecraft.pathfinding;


import net.minecraft.entity.Mob;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;


public class AmphibiousNodeEvaluator
extends WalkNodeProcessor {
    private final boolean prefersShallowSwimming;
    private float oldWalkableCost;
    private float oldWaterBorderCost;

    public AmphibiousNodeEvaluator(boolean bl) {
        this.prefersShallowSwimming = bl;
    }

    @Override
    public void prepare(Region pathNavigationRegion, Mob mob) {
        super.prepare(pathNavigationRegion, mob);
        mob.setPathfindingMalus(PathNodeType.WATER, 0.0f);
        this.oldWalkableCost = mob.getPathfindingMalus(PathNodeType.WALKABLE);
        mob.setPathfindingMalus(PathNodeType.WALKABLE, 6.0f);
        this.oldWaterBorderCost = mob.getPathfindingMalus(PathNodeType.WATER_BORDER);
        mob.setPathfindingMalus(PathNodeType.WATER_BORDER, 4.0f);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(PathNodeType.WALKABLE, this.oldWalkableCost);
        this.mob.setPathfindingMalus(PathNodeType.WATER_BORDER, this.oldWaterBorderCost);
        super.done();
    }

    @Override
    public PathPoint getStart() {
        return this.getNode(MathHelper.floor(this.mob.getBoundingBox().minX), MathHelper.floor(this.mob.getBoundingBox().minY + 0.5), MathHelper.floor(this.mob.getBoundingBox().minZ));
    }

    @Override
    public FlaggedPathPoint getGoal(double d, double d2, double d3) {
        return new FlaggedPathPoint(this.getNode(MathHelper.floor(d), MathHelper.floor(d2 + 0.5), MathHelper.floor(d3)));
    }

    @Override
    public int getNeighbors(PathPoint[] nodeArray, PathPoint node) {
        int n = super.getNeighbors(nodeArray, node);
        PathNodeType blockPathTypes = this.getCachedBlockType(this.mob, node.x, node.y + 1, node.z);
        PathNodeType blockPathTypes2 = this.getCachedBlockType(this.mob, node.x, node.y, node.z);
        int n2 = this.mob.getPathfindingMalus(blockPathTypes) >= 0.0f && blockPathTypes2 != PathNodeType.STICKY_HONEY ? MathHelper.floor(Math.max(1.0f, this.mob.maxUpStep)) : 0;
        double d = this.getFloorLevel(new BlockPos(node.x, node.y, node.z));
        PathPoint node2 = this.getLandNode(node.x, node.y + 1, node.z, Math.max(0, n2 - 1), d, net.minecraft.util.Direction.UP, blockPathTypes2);
        PathPoint node3 = this.getLandNode(node.x, node.y - 1, node.z, n2, d, net.minecraft.util.Direction.DOWN, blockPathTypes2);
        if (this.isNeighborValid(node2, node)) {
            nodeArray[n++] = node2;
        }
        if (this.isNeighborValid(node3, node) && blockPathTypes2 != PathNodeType.TRAPDOOR) {
            nodeArray[n++] = node3;
        }
        for (int i = 0; i < n; ++i) {
            PathPoint node4 = nodeArray[i];
            if (node4.type != PathNodeType.WATER || !this.prefersShallowSwimming || node4.y >= this.mob.level.getSeaLevel() - 10) continue;
            node4.costMalus += 1.0f;
        }
        return n;
    }


    protected double getFloorLevel(BlockPos blockPos) {
        return this.mob.isInWater() ? (double)blockPos.getY() + 0.5 : WalkNodeProcessor.getFloorLevel(this.level, blockPos);
    }

    public boolean isAmphibious() {
        return true;
    }

    @Override
    public PathNodeType getBlockPathType(IBlockReader blockGetter, int n, int n2, int n3) {
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
        PathNodeType blockPathTypes = AmphibiousNodeEvaluator.getBlockPathTypeRaw(blockGetter, mutableBlockPos.set(n, n2, n3));
        if (blockPathTypes == PathNodeType.WATER) {
            for (Direction direction : Direction.values()) {
                PathNodeType blockPathTypes2 = AmphibiousNodeEvaluator.getBlockPathTypeRaw(blockGetter, mutableBlockPos.set(n, n2, n3).move(direction));
                if (blockPathTypes2 != PathNodeType.BLOCKED) continue;
                return PathNodeType.WATER_BORDER;
            }
            return PathNodeType.WATER;
        }
        return AmphibiousNodeEvaluator.getBlockPathTypeStatic(blockGetter, mutableBlockPos);
    }
}

