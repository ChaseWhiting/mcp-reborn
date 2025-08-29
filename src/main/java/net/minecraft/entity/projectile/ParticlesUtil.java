package net.minecraft.entity.projectile;

import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public final class ParticlesUtil {

    /**
     * Spawn a soft, chaotic smoke burst on impact.
     * - Uses hit normal to bias outward spread.
     * - Inherits a small fraction of projectile velocity.
     * - Upward drift slightly increased.
     */
    public static void spawnSmokeImpactBurst(
            World level,
            RayTraceResult hit,             // BlockHitResult or EntityHitResult
            Vector3d projectileVel,        // projectile.getDeltaMovement()
            Random random) {

        final Vector3d pos = hit.getLocation();

        // Impact normal: for blocks, use face normal; for entities, fall back to projectile direction.
        Vector3d normal = switch (hit.getType()) {
            case BLOCK -> {
                Direction dir = ((BlockRayTraceResult) hit).getDirection();
                yield Vector3d.atLowerCornerOf(dir.getNormal()).normalize(); // unit normal
            }
            case ENTITY, MISS -> projectileVel.lengthSqr() > 0
                    ? projectileVel.normalize()
                    : new Vector3d(0, 1, 0);
        };

        // Tuning knobs
        int baseCount = 14 + random.nextInt(10);           // 14–23 particles
        double impactSpeed = projectileVel.length();        // scale by how hard the hit was
        double speedScale = clamp(0.6 + impactSpeed * 0.2, 0.6, 1.3); // gentle scaling
        int count = (int) Math.round(baseCount * (0.8 + 0.2 * speedScale));

        double normalImpulse = 0.06 * speedScale;          // push along the normal
        double tangentSpeedMin = 0.015, tangentSpeedMax = 0.045; // gentle side spread
        double inheritFactor = 0.15;                        // small inheritance of projectile velocity
        double upwardBias = 0.06;                           // add lift to dy
        double jitter = 0.012;                              // chaos
        double spawnRadius = 0.25;                          // spawn within a small disk around the hit

        // Build an orthonormal basis (n, t1, t2) where n = impact normal
        Vector3d n = normal.normalize();
        Vector3d t1 = orthogonal(n);
        Vector3d t2 = n.cross(t1).normalize();

        for (int i = 0; i < count; i++) {
            // Spawn position: small disk around the impact point, on the impact plane
            double r = Math.sqrt(random.nextDouble()) * spawnRadius;
            double theta = random.nextDouble() * Math.PI * 2.0;
            Vector3d spawnOffset = t1.scale(Math.cos(theta) * r).add(t2.scale(Math.sin(theta) * r));
            Vector3d p = pos.add(spawnOffset);

            // Tangential “ring” motion (in plane), randomized
            double tangentialSpeed = lerp(tangentSpeedMin, tangentSpeedMax, random.nextDouble()) * speedScale;
            double phi = random.nextDouble() * Math.PI * 2.0;
            Vector3d tangentDir = t1.scale(Math.cos(phi)).add(t2.scale(Math.sin(phi))).normalize();
            Vector3d vt = tangentDir.scale(tangentialSpeed);

            // Normal (outward) impulse + a little inherited velocity + upward bias + jitter
            Vector3d vn = n.scale(normalImpulse);
            Vector3d vinherit = projectileVel.scale(inheritFactor);
            Vector3d vjitter = new Vector3d(
                    (random.nextDouble() - 0.5) * jitter,
                    (random.nextDouble() - 0.5) * jitter,
                    (random.nextDouble() - 0.5) * jitter
            );

            // Final velocity—kept intentionally “floaty”
            Vector3d v = vt.add(vn).add(vinherit).add(vjitter).add(0, upwardBias, 0);

            level.addAlwaysVisibleParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE, true,
                    p.x, p.y, p.z,
                    v.x, v.y, v.z
            );
        }
    }

    private static Vector3d orthogonal(Vector3d n) {
        // Return any unit vector orthogonal to n
        Vector3d a = Math.abs(n.x) < 0.6 ? new Vector3d(1, 0, 0) : new Vector3d(0, 1, 0);
        return n.cross(a).normalize();
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}