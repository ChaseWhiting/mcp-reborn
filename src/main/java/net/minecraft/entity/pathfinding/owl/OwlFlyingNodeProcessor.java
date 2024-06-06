package net.minecraft.entity.pathfinding.owl;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.FlaggedPathPoint;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class OwlFlyingNodeProcessor extends WalkNodeProcessor {

    @Override
    public void prepare(Region region, MobEntity mob) {
        super.prepare(region, mob);
        this.oldWaterCost = mob.getPathfindingMalus(PathNodeType.WATER);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
        super.done();
    }

    @Override
    public PathPoint getStart() {
        int y;
        if (this.canFloat() && this.mob.isInWater()) {
            y = MathHelper.floor(this.mob.getY());
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.mob.getX(), (double) y, this.mob.getZ());

            for (Block block = this.level.getBlockState(blockpos$mutable).getBlock(); block == Blocks.WATER; block = this.level.getBlockState(blockpos$mutable).getBlock()) {
                ++y;
                blockpos$mutable.set(this.mob.getX(), (double) y, this.mob.getZ());
            }
        } else {
            y = MathHelper.floor(this.mob.getY() + 0.5D);
        }

        BlockPos blockpos1 = this.mob.blockPosition();
        PathNodeType pathnodetype1 = this.getBlockPathType(this.mob, blockpos1.getX(), y, blockpos1.getZ());
        if (this.mob.getPathfindingMalus(pathnodetype1) < 0.0F) {
            Set<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.mob.getBoundingBox().minX, (double) y, this.mob.getBoundingBox().minZ));
            set.add(new BlockPos(this.mob.getBoundingBox().minX, (double) y, this.mob.getBoundingBox().maxZ));
            set.add(new BlockPos(this.mob.getBoundingBox().maxX, (double) y, this.mob.getBoundingBox().minZ));
            set.add(new BlockPos(this.mob.getBoundingBox().maxX, (double) y, this.mob.getBoundingBox().maxZ));

            for (BlockPos blockpos : set) {
                PathNodeType pathnodetype = this.getBlockPathType(this.mob, blockpos);
                if (this.mob.getPathfindingMalus(pathnodetype) >= 0.0F) {
                    return super.getNode(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                }
            }
        }

        return super.getNode(blockpos1.getX(), y, blockpos1.getZ());
    }

    @Override
    public FlaggedPathPoint getGoal(double x, double y, double z) {
        return new FlaggedPathPoint(super.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
    }

    @Override
    public int getNeighbors(PathPoint[] neighbors, PathPoint currentPoint) {
        int i = 0;
        PathPoint pathpoint = this.getNode(currentPoint.x, currentPoint.y, currentPoint.z + 1);
        if (this.isOpen(pathpoint)) {
            neighbors[i++] = pathpoint;
        }

        PathPoint pathpoint1 = this.getNode(currentPoint.x - 1, currentPoint.y, currentPoint.z);
        if (this.isOpen(pathpoint1)) {
            neighbors[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = this.getNode(currentPoint.x + 1, currentPoint.y, currentPoint.z);
        if (this.isOpen(pathpoint2)) {
            neighbors[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = this.getNode(currentPoint.x, currentPoint.y, currentPoint.z - 1);
        if (this.isOpen(pathpoint3)) {
            neighbors[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = this.getNode(currentPoint.x, currentPoint.y + 1, currentPoint.z);
        if (this.isOpen(pathpoint4)) {
            neighbors[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = this.getNode(currentPoint.x, currentPoint.y - 1, currentPoint.z);
        if (this.isOpen(pathpoint5)) {
            neighbors[i++] = pathpoint5;
        }

        // Custom diagonal movement for smoother gliding
        PathPoint pathpoint6 = this.getNode(currentPoint.x + 1, currentPoint.y, currentPoint.z + 1);
        if (this.isOpen(pathpoint6) && this.hasMalus(pathpoint) && this.hasMalus(pathpoint2)) {
            neighbors[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = this.getNode(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1);
        if (this.isOpen(pathpoint7) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint)) {
            neighbors[i++] = pathpoint7;
        }

        PathPoint pathpoint8 = this.getNode(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1);
        if (this.isOpen(pathpoint8) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint3)) {
            neighbors[i++] = pathpoint8;
        }

        PathPoint pathpoint9 = this.getNode(currentPoint.x - 1, currentPoint.y, currentPoint.z - 1);
        if (this.isOpen(pathpoint9) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint3)) {
            neighbors[i++] = pathpoint9;
        }

        return i;
    }

    private boolean hasMalus(@Nullable PathPoint pathPoint) {
        return pathPoint != null && pathPoint.costMalus >= 0.0F;
    }

    private boolean isOpen(@Nullable PathPoint pathPoint) {
        return pathPoint != null && !pathPoint.closed;
    }

    @Nullable
    @Override
    protected PathPoint getNode(int x, int y, int z) {
        PathPoint pathpoint = null;
        PathNodeType pathnodetype = this.getBlockPathType(this.mob, x, y, z);
        float f = this.mob.getPathfindingMalus(pathnodetype);
        if (f >= 0.0F) {
            pathpoint = super.getNode(x, y, z);
            pathpoint.type = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            if (pathnodetype == PathNodeType.WALKABLE) {
                ++pathpoint.costMalus;
            }
        }

        return pathnodetype != PathNodeType.OPEN && pathnodetype != PathNodeType.WALKABLE ? pathpoint : pathpoint;
    }

    @Override
    public PathNodeType getBlockPathType(IBlockReader blockReader, int x, int y, int z, MobEntity mob, int xSize, int ySize, int zSize, boolean canOpenDoors, boolean canPassDoors) {
        EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathnodetype = PathNodeType.BLOCKED;
        BlockPos blockpos = mob.blockPosition();
        pathnodetype = this.getBlockPathTypes(blockReader, x, y, z, xSize, ySize, zSize, canOpenDoors, canPassDoors, enumset, pathnodetype, blockpos);
        if (enumset.contains(PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        } else {
            PathNodeType pathnodetype1 = PathNodeType.BLOCKED;

            for (PathNodeType pathnodetype2 : enumset) {
                if (mob.getPathfindingMalus(pathnodetype2) < 0.0F) {
                    return pathnodetype2;
                }

                if (mob.getPathfindingMalus(pathnodetype2) >= mob.getPathfindingMalus(pathnodetype1)) {
                    pathnodetype1 = pathnodetype2;
                }
            }

            return pathnodetype == PathNodeType.OPEN && mob.getPathfindingMalus(pathnodetype1) == 0.0F ? PathNodeType.OPEN : pathnodetype1;
        }
    }

    @Override
    public PathNodeType getBlockPathType(IBlockReader blockReader, int x, int y, int z) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        PathNodeType pathnodetype = getBlockPathTypeRaw(blockReader, blockpos$mutable.set(x, y, z));
        if (pathnodetype == PathNodeType.OPEN && y >= 1) {
            BlockState blockstate = blockReader.getBlockState(blockpos$mutable.set(x, y - 1, z));
            PathNodeType pathnodetype1 = getBlockPathTypeRaw(blockReader, blockpos$mutable.set(x, y - 1, z));
            if (pathnodetype1 != PathNodeType.DAMAGE_FIRE && !blockstate.is(Blocks.MAGMA_BLOCK) && pathnodetype1 != PathNodeType.LAVA && !blockstate.is(BlockTags.CAMPFIRES)) {
                if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
                    pathnodetype = PathNodeType.DAMAGE_CACTUS;
                } else if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
                    pathnodetype = PathNodeType.DAMAGE_OTHER;
                } else if (pathnodetype1 == PathNodeType.COCOA) {
                    pathnodetype = PathNodeType.COCOA;
                } else if (pathnodetype1 == PathNodeType.FENCE) {
                    pathnodetype = PathNodeType.FENCE;
                } else {
                    pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER ? PathNodeType.WALKABLE : PathNodeType.OPEN;
                }
            } else {
                pathnodetype = PathNodeType.DAMAGE_FIRE;
            }
        }

        if (pathnodetype == PathNodeType.WALKABLE || pathnodetype == PathNodeType.OPEN) {
            pathnodetype = checkNeighbourBlocks(blockReader, blockpos$mutable.set(x, y, z), pathnodetype);
        }

        return pathnodetype;
    }

    private PathNodeType getBlockPathType(MobEntity mob, BlockPos pos) {
        return this.getBlockPathType(mob, pos.getX(), pos.getY(), pos.getZ());
    }

    private PathNodeType getBlockPathType(MobEntity mob, int x, int y, int z) {
        return this.getBlockPathType(this.level, x, y, z, mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
    }
}
