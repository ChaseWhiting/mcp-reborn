package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElectricSparkParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteSet;

    protected ElectricSparkParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.spriteSet = spriteSet;

        this.setColor(1.0f, 0.9f, 1.0f); // Electric spark color
        this.xd = xSpeed * 0.25; // Speed factor for X
        this.yd = ySpeed * 0.25; // Speed factor for Y
        this.zd = zSpeed * 0.25; // Speed factor for Z
        this.gravity = 0.0f; // No gravity
        this.lifetime = this.random.nextInt(2) + 2; // Random lifetime (2 to 4 ticks)
        this.quadSize *= 0.75f; // Adjust size
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);

        // Fading over time
        float ageFactor = (float) this.age / (float) this.lifetime;
        this.alpha = MathHelper.clamp(1.0f - ageFactor, 0.0f, 1.0f);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Provider(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ElectricSparkParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    public static class WaxOnProvider implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public WaxOnProvider(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
           ElectricSparkParticle particle = new ElectricSparkParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
           particle.setColor(0.91f, 0.55f, 0.08f);
           particle.xd = xSpeed * 0.01 / 2.0;
           particle.yd = ySpeed * 0.01;
           particle.zd = zSpeed * 0.01 / 2.0;
           particle.setLifetime(world.random.nextInt(30) + 10);
           return particle;
        }

    }

    public static class WaxOffProvider implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public WaxOffProvider(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ElectricSparkParticle particle = new ElectricSparkParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setColor(1.0f, 0.9f, 1.0f);
            particle.xd = xSpeed * 0.01 / 2.0;
            particle.yd = ySpeed * 0.01;
            particle.zd = zSpeed * 0.01 / 2.0;
            particle.setLifetime(world.random.nextInt(30) + 10);
            return particle;
        }

    }

    public static class ScrapeProvider implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public ScrapeProvider(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ElectricSparkParticle particle = new ElectricSparkParticle(world, x, y, z, 0, 0, 0, this.spriteSet);
            if (world.random.nextBoolean()) {
                particle.setColor(0.29f, 0.58f, 0.51f);
            } else {
                particle.setColor(0.43f, 0.77f, 0.62f);
            }
            particle.xd = xSpeed * 0.01;
            particle.yd = ySpeed * 0.01;
            particle.zd = zSpeed * 0.01;
            particle.setLifetime(world.random.nextInt(30) + 10);
            return particle;
        }

    }
}
