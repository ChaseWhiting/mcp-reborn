
package net.minecraft.client.particle;


import net.minecraft.client.world.ClientWorld;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FireflyParticle
extends SpriteTexturedParticle {
    private static final float PARTICLE_FADE_OUT_TIME = 0.5f;
    private static final float PARTICLE_FADE_IN_TIME = 0.3f;
    private static final int PARTICLE_MIN_LIFETIME = 36;
    private static final int PARTICLE_MAX_LIFETIME = 180;

    FireflyParticle(ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3, d4, d5, d6);
        this.quadSize *= 0.75f;
        this.yd *= (double)0.8f;
        this.xd *= (double)0.8f;
        this.zd *= (double)0.8f;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float f) {
        float f2 = MathHelper.clamp(((float)this.age + f) / (float)this.lifetime, 0.0f, 1.0f);
        if (f2 > PARTICLE_FADE_OUT_TIME) {
            float f3 = (1.0f - f2) / PARTICLE_FADE_OUT_TIME;
            return (int)(255.0f * f3);
        }
        if (f2 < PARTICLE_FADE_IN_TIME) {
            float f4 = f2 / PARTICLE_FADE_IN_TIME;
            return (int)(255.0f * f4);
        }
        return 255;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
            this.remove();
            return;
        }
        if (Math.random() > 0.95 || this.age == 1) {
            this.setParticleSpeed((double)-0.05f + (double)0.1f * Math.random(), (double)-0.05f + (double)0.1f * Math.random(), (double)-0.05f + (double)0.1f * Math.random());
        }
    }

    public static class FireflyProvider
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public FireflyProvider(IAnimatedSprite spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(BasicParticleType simpleParticleType, ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            FireflyParticle fireflyParticle = new FireflyParticle(clientLevel, d, d2, d3, 0.5 - clientLevel.random.nextDouble(), clientLevel.random.nextBoolean() ? d5 : -d5, 0.5 - clientLevel.random.nextDouble());
            fireflyParticle.setLifetime(ParticleTypes.randomBetweenInclusive(clientLevel.random,PARTICLE_MIN_LIFETIME, PARTICLE_MAX_LIFETIME));
            fireflyParticle.scale(1.5f);
            fireflyParticle.pickSprite(this.sprite);
            return fireflyParticle;
        }
    }
}

