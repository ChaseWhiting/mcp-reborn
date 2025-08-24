package net.minecraft.util.math.vector;

import net.minecraft.util.math.BlockPos;

public class Vec3 extends Vector3d{
    public Vec3(double x, double y, double z) {
        super(x, y, z);
    }

    public Vec3(Vector3f vector) {
        super(vector);
    }

    public Vec3(BlockPos pos) {
        super(pos);
    }
}
