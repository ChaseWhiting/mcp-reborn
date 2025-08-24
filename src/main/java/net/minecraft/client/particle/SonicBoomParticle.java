/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;


import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class SonicBoomParticle
extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;

    protected SonicBoomParticle(ClientWorld clientLevel, double d, double d2, double d3, double d4, IAnimatedSprite spriteSet) {
        super(clientLevel, d, d2, d3);
        this.lifetime = 16;
        this.quadSize = 1.5f;
        float f;
        this.rCol = f = this.random.nextFloat() * 0.6f + 0.4f;
        this.gCol = f;
        this.bCol = f;
        this.setSpriteFromAge(spriteSet);
        this.sprites = spriteSet;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getLightColor(float f) {
        return 0xF000F0;
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
        this.setSpriteFromAge(this.sprites);
    }

    public static class Provider
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Provider(IAnimatedSprite spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(BasicParticleType simpleParticleType, ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new SonicBoomParticle(clientLevel, d, d2, d3, d4, this.sprite);
        }
    }
}

