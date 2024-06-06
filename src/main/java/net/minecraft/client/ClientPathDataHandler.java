package net.minecraft.client;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientPathDataHandler {
    private static final Map<Integer, List<BlockPos>> pathDataMap = new HashMap<>();

    public static void setPathData(int entityId, List<BlockPos> pathNodes) {
        pathDataMap.put(entityId, pathNodes);
    }

    public static List<BlockPos> getPathData(int entityId) {
        return pathDataMap.get(entityId);
    }
}
