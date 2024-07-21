package net.minecraft.entity.pathfinding.owl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Mob;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class OwlFlyingPathNavigator extends PathNavigator {
    public OwlFlyingPathNavigator(Mob mob, World world) {
        super(mob, world);
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        this.nodeEvaluator = new OwlFlyingNodeProcessor();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, range);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.canFloat() && this.isInLiquid() || !this.mob.isPassenger();
    }

    @Override
    protected Vector3d getTempMobPos() {
        return this.mob.position();
    }

    @Override
    public Path createPath(Entity targetEntity, int range) {
        return this.createPath(targetEntity.blockPosition(), range);
    }

    @Override
    public void tick() {
        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }

        if (!this.isDone()) {
            if (this.canUpdatePath()) {
                this.followThePath();
            } else if (this.path != null && !this.path.isDone()) {
                Vector3d nextPos = this.path.getNextEntityPos(this.mob);
                if (MathHelper.floor(this.mob.getX()) == MathHelper.floor(nextPos.x) && MathHelper.floor(this.mob.getY()) == MathHelper.floor(nextPos.y) && MathHelper.floor(this.mob.getZ()) == MathHelper.floor(nextPos.z)) {
                    this.path.advance();
                }
            }

            DebugPacketSender.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
            if (!this.isDone()) {
                Vector3d nextPos = this.path.getNextEntityPos(this.mob);
                this.mob.getMoveControl().setWantedPosition(nextPos.x, nextPos.y, nextPos.z, this.speedModifier);
            }
        }
    }

    @Override
    protected boolean canMoveDirectly(Vector3d startPos, Vector3d endPos, int xSize, int ySize, int zSize) {
        int startX = MathHelper.floor(startPos.x);
        int startY = MathHelper.floor(startPos.y);
        int startZ = MathHelper.floor(startPos.z);
        double dx = endPos.x - startPos.x;
        double dy = endPos.y - startPos.y;
        double dz = endPos.z - startPos.z;
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        if (distanceSquared < 1.0E-8D) {
            return false;
        } else {
            double distance = 1.0D / Math.sqrt(distanceSquared);
            dx *= distance;
            dy *= distance;
            dz *= distance;
            double invDx = 1.0D / Math.abs(dx);
            double invDy = 1.0D / Math.abs(dy);
            double invDz = 1.0D / Math.abs(dz);
            double offsetX = (double)startX - startPos.x;
            double offsetY = (double)startY - startPos.y;
            double offsetZ = (double)startZ - startPos.z;
            if (dx >= 0.0D) {
                ++offsetX;
            }
            if (dy >= 0.0D) {
                ++offsetY;
            }
            if (dz >= 0.0D) {
                ++offsetZ;
            }
            offsetX /= dx;
            offsetY /= dy;
            offsetZ /= dz;
            int stepX = dx < 0.0D ? -1 : 1;
            int stepY = dy < 0.0D ? -1 : 1;
            int stepZ = dz < 0.0D ? -1 : 1;
            int endX = MathHelper.floor(endPos.x);
            int endY = MathHelper.floor(endPos.y);
            int endZ = MathHelper.floor(endPos.z);
            int deltaX = endX - startX;
            int deltaY = endY - startY;
            int deltaZ = endZ - startZ;

            while(deltaX * stepX > 0 || deltaY * stepY > 0 || deltaZ * stepZ > 0) {
                if (offsetX < offsetZ && offsetX <= offsetY) {
                    offsetX += invDx;
                    startX += stepX;
                    deltaX = endX - startX;
                } else if (offsetY < offsetX && offsetY <= offsetZ) {
                    offsetY += invDy;
                    startY += stepY;
                    deltaY = endY - startY;
                } else {
                    offsetZ += invDz;
                    startZ += stepZ;
                    deltaZ = endZ - startZ;
                }
            }
            return true;
        }
    }

   // @Override
    public void setCanOpenDoors(boolean canOpenDoors) {
        this.nodeEvaluator.setCanOpenDoors(canOpenDoors);
    }

   // @Override
    public void setCanPassDoors(boolean canPassDoors) {
        this.nodeEvaluator.setCanPassDoors(canPassDoors);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return this.level.getBlockState(pos).entityCanStandOn(this.level, pos, this.mob);
    }
}
