package net.minecraft.client.particle;

import net.minecraft.client.util.FastColor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class DustPlumeParticle
extends BaseAshSmokeParticle {
    private static final int COLOR_RGB24 = 12235202;

    protected DustPlumeParticle(ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6, float f, IAnimatedSprite spriteSet) {
        super(clientLevel, d, d2, d3, 0.7f, 0.6f, 0.7f, d4, d5 + (double)0.15f, d6, f, spriteSet, 0.5f, 7, 0.5f, false);
        float f2 = (float)Math.random() * 0.2f;
        this.rCol = (float)FastColor.ARGB32.red(12235202) / 255.0f - f2;
        this.gCol = (float)FastColor.ARGB32.green(12235202) / 255.0f - f2;
        this.bCol = (float) FastColor.ARGB32.blue(12235202) / 255.0f - f2;
    }

    @Override
    public void tick() {
        this.gravity = 0.88f * this.gravity;
        this.friction = 0.92f * this.friction;
        super.tick();
    }

    public static class Provider
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;

        public Provider(IAnimatedSprite spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(BasicParticleType simpleParticleType, ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new DustPlumeParticle(clientLevel, d, d2, d3, d4, d5, d6, 1.0f, this.sprites);
        }
    }
}

