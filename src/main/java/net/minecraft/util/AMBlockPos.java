package net.minecraft.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class AMBlockPos {

    public static final BlockPos fromCoords(double x, double y, double z){
        return new BlockPos((int) x, (int) y, (int) z);
    }

    public static final BlockPos fromVec3(Vector3d vec3){
        return fromCoords(vec3.x, vec3.y, vec3.z);
    }
}