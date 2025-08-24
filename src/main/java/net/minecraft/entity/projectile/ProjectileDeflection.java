package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.Random;

@FunctionalInterface
public interface ProjectileDeflection {
    public static final ProjectileDeflection NONE = (projectile, entity, randomSource) -> {};
    public static final ProjectileDeflection REVERSE = (projectile, entity, randomSource) -> {
        float f = 170.0f + randomSource.nextFloat() * 20.0f;
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-0.5));
        projectile.yRot = (projectile.yRot + f);
        projectile.yRotO += f;
        projectile.hasImpulse = true;
    };
    public static final ProjectileDeflection AIM_DEFLECT = (projectile, entity, randomSource) -> {
        if (entity != null) {
            Vector3d vec3 = entity.getLookAngle().normalize();
            projectile.setDeltaMovement(vec3);
            projectile.hasImpulse = true;
        }
    };
    public static final ProjectileDeflection MOMENTUM_DEFLECT = (projectile, entity, randomSource) -> {
        if (entity != null) {
            Vector3d vec3 = entity.getDeltaMovement().normalize();
            projectile.setDeltaMovement(vec3);
            projectile.hasImpulse = true;
        }
    };

    public void deflect(ProjectileEntity var1, @Nullable Entity var2, Random var3);
}

