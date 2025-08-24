package net.minecraft.util.math.vector;

import net.minecraft.util.math.vector.Vector3d;

public class ProjectileAimHelper {

    /**
     * Calculates the optimal yaw (yRot) and pitch (xRot) for firing a projectile to hit a moving target.
     *
     * @param shooterPos   The position of the shooter (usually eye position).
     * @param targetPos    The current position of the target.
     * @param targetDelta  The movement delta of the target per tick.
     * @param ticksAhead   How many ticks into the future to predict.
     * @param projectileSpeed The speed of the projectile (blocks per tick).
     * @return A float array where [0] = yRot (yaw), [1] = xRot (pitch).
     */
    public static float[] calculateRotationsToHit(Vector3d shooterPos, Vector3d targetPos, Vector3d targetDelta,
                                                  int ticksAhead, double projectileSpeed) {
        // Predict future position
        Vector3d predictedTargetPos = targetPos.add(targetDelta.scale(ticksAhead));

        // Get the difference vector
        Vector3d diff = predictedTargetPos.subtract(shooterPos);

        double dx = diff.x;
        double dy = diff.y;
        double dz = diff.z;

        // Horizontal distance
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        // Compute yaw (horizontal rotation)
        float yRot = (float) (Math.toDegrees(Math.atan2(-dx, dz)));

        // Compute pitch (vertical rotation)
        float xRot = (float) (Math.toDegrees(-Math.atan2(dy, horizontalDistance)));

        return new float[]{yRot, xRot};
    }
}
