/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;



import net.minecraft.client.world.ClientWorld;

import net.minecraft.util.ARGB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class TrailParticle
extends SpriteTexturedParticle {
    private final Vector3d target;

    TrailParticle(ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6, Vector3d vec3, int n) {
        super(clientLevel, d, d2, d3, d4, d5, d6);
        n = ARGB.scaleRGB(n, 0.875f + this.random.nextFloat() * 0.25f, 0.875f + this.random.nextFloat() * 0.25f, 0.875f + this.random.nextFloat() * 0.25f);
        this.rCol = (float)ARGB.red(n) / 255.0f;
        this.gCol = (float)ARGB.green(n) / 255.0f;
        this.bCol = (float)ARGB.blue(n) / 255.0f;
        this.quadSize = 0.26f;
        this.target = vec3;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        int n = this.lifetime - this.age;
        double d = 1.0 / (double)n;
        this.x = MathHelper.lerp(d, this.x, this.target.x());
        this.y = MathHelper.lerp(d, this.y, this.target.y());
        this.z = MathHelper.lerp(d, this.z, this.target.z());
    }

    @Override
    public int getLightColor(float f) {
        return 0xF000F0;
    }

    public static class Provider
    implements IParticleFactory<net.minecraft.particles.TrailParticleOption> {
        private final IAnimatedSprite sprite;

        public Provider(IAnimatedSprite spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(net.minecraft.particles.TrailParticleOption trailParticleOption, ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            TrailParticle trailParticle = new TrailParticle(clientLevel, d, d2, d3, d4, d5, d6, trailParticleOption.getTarget(), trailParticleOption.getColor());
            trailParticle.pickSprite(this.sprite);
            trailParticle.setLifetime(trailParticleOption.getDuration());
            return trailParticle;
        }

    }
}

