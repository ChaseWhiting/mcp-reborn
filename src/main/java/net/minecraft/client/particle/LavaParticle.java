package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.server.SPlayGameEventPacket;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.level.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LavaParticle extends SpriteTexturedParticle {
    private boolean isTNT;

    private LavaParticle(ClientWorld p_i232403_1_, double p_i232403_2_, double p_i232403_4_, double p_i232403_6_) {
        super(p_i232403_1_, p_i232403_2_, p_i232403_4_, p_i232403_6_, 0.0D, 0.0D, 0.0D);
        this.xd *= (double) 0.8F;
        this.yd *= (double) 0.8F;
        this.zd *= (double) 0.8F;
        this.yd = (double) (this.random.nextFloat() * 0.4F + 0.05F);
        this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
    }

    private LavaParticle(ClientWorld p_i232403_1_, double p_i232403_2_, double p_i232403_4_, double p_i232403_6_, boolean replace) {
        super(p_i232403_1_, p_i232403_2_, p_i232403_4_, p_i232403_6_, 0.0D, 0.0D, 0.0D);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.isTNT = true;
        this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
        this.lifetime = (int) (20.0D / (Math.random() * 0.8D + 0.2D));
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public int getLightColor(float p_189214_1_) {
        int i = super.getLightColor(p_189214_1_);
        int j = 240;
        int k = i >> 16 & 255;
        return 240 | k << 16;
    }

    public float getQuadSize(float p_217561_1_) {
        float f = ((float) this.age + p_217561_1_) / (float) this.lifetime;
        return this.quadSize * (1.0F - f * f);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float f = (float) this.age / (float) this.lifetime;
        if (this.random.nextFloat() > f) {
            this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
        }

        for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().deflate(0.005))) {
            this.level.sendPacketToServer(new SPlayGameEventPacket(GameEvent.HURT_ENTITY_FIRE, livingEntity.blockPosition(), livingEntity.getId(), false));
            this.remove();
        }

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            if (this.age <= 1 && this.isTNT) {
                this.yd += random.nextFloat(0.05F, 0.07F);
            }
            double d = (this.isTNT ? (age > 10 ? 0.04D : 0.03D) : 0.03D);

            float ff = (this.isTNT ? (age > 15 ? 0.999F : 1.001F) : 0.999F);

            this.yd -= d;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) ff;
            this.yd *= (double) ff;
            this.zd *= (double) ff;
            if (this.onGround) {
                if (!isTNT) {
                    this.xd *= (double) 0.7F;
                    this.zd *= (double) 0.7F;
                } else {
                    this.yd *= -0.6;
                    float f2 = (age > 15 ? 0.95F : 1.01F);
                    this.xd *= f2;
                    this.zd *= f2;

                }
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite p_i50495_1_) {
            this.sprite = p_i50495_1_;
        }

        public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
            LavaParticle lavaparticle = new LavaParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
            lavaparticle.pickSprite(this.sprite);
            return lavaparticle;
        }
    }

    public static class TntFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public TntFactory(IAnimatedSprite p_i50495_1_) {
            this.sprite = p_i50495_1_;
        }

        public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
            LavaParticle lavaparticle = new LavaParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, false);
            lavaparticle.pickSprite(this.sprite);
            return lavaparticle;
        }
    }
}