package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

public class BaseAshSmokeParticle
extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;

    protected BaseAshSmokeParticle(ClientWorld clientLevel, double d, double d2, double d3, float f, float f2, float f3, double d4, double d5, double d6, float f4, IAnimatedSprite spriteSet, float f5, int n, float f6, boolean bl) {
        super(clientLevel, d, d2, d3, 0.0, 0.0, 0.0);
        float f7;
        this.friction = 0.96f;
        this.gravity = f6;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = spriteSet;
        this.xd *= (double)f;
        this.yd *= (double)f2;
        this.zd *= (double)f3;
        this.xd += d4;
        this.yd += d5;
        this.zd += d6;
        this.rCol = f7 = clientLevel.random.nextFloat() * f5;
        this.gCol = f7;
        this.bCol = f7;
        this.quadSize *= 0.75f * f4;
        this.lifetime = (int)((double)n / ((double)clientLevel.random.nextFloat() * 0.8 + 0.2) * (double)f4);
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(spriteSet);
        this.hasPhysics = bl;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float f) {
        return this.quadSize * MathHelper.clamp(((float)this.age + f) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age < this.lifetime) {
            this.setSpriteFromAge(this.sprites);
        }
    }

}