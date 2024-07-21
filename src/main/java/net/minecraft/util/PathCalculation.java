package net.minecraft.util;

import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;

public class PathCalculation {

    private NodeProcessor nodeProcessor;
    private PathNavigator navigator;
    private Mob mob;
    private MovementController controller;

    public PathCalculation(NodeProcessor nodeProcessor, PathNavigator pathNavigator, Mob mob) {
        this.navigator = pathNavigator;
        this.nodeProcessor = nodeProcessor;
        this.mob = mob;
    }

    public PathCalculation() {

    }




    // Methods for MobEntity
    public static List<PathPoint> calculateOptimalNodes(Mob mob, World world, int radius) {
        List<PathPoint> nodes = new ArrayList<>();
        BlockPos mobPos = mob.blockPosition();

        for (BlockPos pos : getSurroundingBlocks(mobPos, world, radius)) {
            if (isAccessible(pos, world, mob)) {
                nodes.add(new PathPoint(pos.getX(), pos.getY(), pos.getZ()));
            }
        }

        connectNodes(nodes);
        return nodes;
    }

    private static boolean isAccessible(BlockPos pos, World world, Mob mob) {
        BlockPos belowPos = pos.below();
        if (world.getBlockState(pos).isSolidRender(world, pos)) {
            return false; // Block is solid, so not accessible
        }

        PathNavigator navigator = mob.getNavigation();
        Path path = navigator.createPath(pos, 0);

        return path != null && path.canReach(); // Check if a path can be created and if it is reachable
    }

    // Methods for PlayerEntity
    public static List<PathPoint> calculateOptimalNodesForPlayer(PlayerEntity player, World world, int radius) {
        List<PathPoint> nodes = new ArrayList<>();
        BlockPos playerPos = player.blockPosition();

        for (BlockPos pos : getSurroundingBlocks(playerPos, world, radius)) {
            if (isAccessibleForPlayer(pos, world, player)) {
                nodes.add(new PathPoint(pos.getX(), pos.getY(), pos.getZ()));
            }
        }

        connectNodes(nodes);
        return nodes;
    }

    private static boolean isAccessibleForPlayer(BlockPos pos, World world, PlayerEntity player) {
        BlockPos belowPos = pos.below();
        return world.getBlockState(belowPos).isSolidRender(world, belowPos) &&
                !world.getBlockState(pos).isSolidRender(world, pos) &&
                !world.getBlockState(pos.above()).isSolidRender(world, pos.above());
    }

    private static List<BlockPos> getSurroundingBlocks(BlockPos start, World world, int radius) {
        List<BlockPos> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    blocks.add(pos);
                }
            }
        }
        return blocks;
    }

    private static void connectNodes(List<PathPoint> nodes) {
        // Implementation of connecting nodes based on accessibility and movement cost
    }

    public static Path findPath(Mob mob, BlockPos target, List<PathPoint> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        // Placeholder for pathfinding algorithm, e.g., A* algorithm
        // Create the path object using the nodes list
        List<PathPoint> pathPoints = new ArrayList<>(nodes);
        return new Path(pathPoints, target, true);
    }

    public static void updateMobPath(Mob mob, Path path, float speed) {
        if (path != null) {
            mob.getNavigation().moveTo(path, speed); // Set the mob's navigation to the new path with a speed of 1.0
        }
    }

    public static Path findZigZagPath(Mob mob, BlockPos target, List<PathPoint> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        List<PathPoint> pathPoints = new ArrayList<>(nodes);
        List<PathPoint> zigZagPathPoints = new ArrayList<>();
        int zigZagFactor = 6;  // Increase to make zig-zag more pronounced

        for (int i = 0; i < pathPoints.size() - 1; i++) {
            PathPoint currentPoint = pathPoints.get(i);
            PathPoint nextPoint = pathPoints.get(i + 1);

            // Add current point to the zig-zag path
            zigZagPathPoints.add(currentPoint);

            // Calculate direction towards the next point
            int dirX = Integer.compare(nextPoint.x, currentPoint.x);
            int dirZ = Integer.compare(nextPoint.z, currentPoint.z);

            // Add zig-zag points that divert from the direct path to the next point
            for (int j = 1; j <= zigZagFactor; j++) {
                int zigZagX = currentPoint.x + j * dirZ;  // Z-direction for X-coordinates to get perpendicular direction
                int zigZagZ = currentPoint.z - j * dirX;  // X-direction for Z-coordinates to get perpendicular direction
                zigZagPathPoints.add(new PathPoint(zigZagX, currentPoint.y, zigZagZ));

                zigZagX = currentPoint.x - j * dirZ; // Add point to the opposite side
                zigZagZ = currentPoint.z + j * dirX; // Add point to the opposite side
                zigZagPathPoints.add(new PathPoint(zigZagX, currentPoint.y, zigZagZ));
            }
        }

        // Don't forget to add the final node
        zigZagPathPoints.add(pathPoints.get(pathPoints.size() - 1));

        return new Path(zigZagPathPoints, target, true);
    }
}