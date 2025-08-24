package net.minecraft.client.particle;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.play.server.SPlayGameEventPacket;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.GameEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class FallingLeavesParticle extends SpriteTexturedParticle {
    private static final float ACCELERATION_SCALE = 0.0025f;
    private static final int INITIAL_LIFETIME = 300;
    private static final int CURVE_ENDPOINT_TIME = 300;
    private float rotSpeed;
    private final float particleRandom;
    private final float spinAcceleration;
    private float windBig;
    private boolean isBurst;
    private boolean swirl;
    private boolean flowAway;
    private double xaFlowScale;
    private double zaFlowScale;
    private double swirlPeriod;
    private final IAnimatedSprite sprites;
    @Nullable
    private final PetalBlock block;
    private int check;

    protected FallingLeavesParticle(ClientWorld world, @Nullable PetalBlock block, double x, double y, double z, IAnimatedSprite sprite, float gravity, float windBig, boolean swirl, boolean flowAway, float size, float ySpeed, boolean isBurst, boolean fromBroken) {
        super(world, x, y, z);
        this.sprites = sprite;
        this.rotSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
        this.particleRandom = this.random.nextFloat();
        this.spinAcceleration = (float) Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
        this.windBig = windBig;
        this.swirl = swirl;
        this.flowAway = flowAway;
        this.lifetime = INITIAL_LIFETIME;
        this.gravity = gravity * 1.2f * ACCELERATION_SCALE;
        this.quadSize = size * (this.random.nextBoolean() ? 0.05f : 0.075f);
        this.yd = -ySpeed;
        this.block = (PetalBlock) block;
        this.xaFlowScale = Math.cos(Math.toRadians(this.particleRandom * 60.0f)) * this.windBig;
        this.zaFlowScale = Math.sin(Math.toRadians(this.particleRandom * 60.0f)) * this.windBig;
        this.swirlPeriod = Math.toRadians(1000.0f + this.particleRandom * 3000.0f);
        this.isBurst = isBurst;
        this.setSprite(sprites.get(this.random));

        if (fromBroken) {
            this.isBurst = true;
            lifetime = 600;
            double angle = 2 * Math.PI * getRandom().nextDouble();
            double speed = (0.2) + getRandom().nextDouble() * 0.05;
            setXd(Math.cos(angle) * speed);
            setZd(Math.sin(angle) * speed);

            setYd(random.nextFloat(-0.01F, 0.01F));
        }
    }

    public Random getRandom() {
        return this.random;
    }

    public void setXd(double xd) {
        this.xd = xd;
    }

    public void setZd(double xd) {
        this.zd = xd;
    }

    public void setYd(double xd) {
        this.yd = xd;
    }

    public void setQuadSize(float f) {
        this.quadSize = f;
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

        if (this.lifetime-- <= 0) {
            this.remove();
            return;
        }



        float elapsedTime = (flowAway && isBurst ? 600 : INITIAL_LIFETIME) - this.lifetime;
        float curveProgress = Math.min(elapsedTime / CURVE_ENDPOINT_TIME, 1.0f);
        double deltaX = 0.0;
        double deltaZ = 0.0;

        if (this.isBurst) {
            block:
            {
                if (elapsedTime > 20) {

                    int t = 70;
                    double d = 0D;
                    int i = 0;
                    do {
                        d += this.xd;
                        d += this.zd;
                        i += 1;
                    } while ((xd > 0.0 || zd > 0.0) && i <= 4);
                    t = t + (int) d;

                    if (elapsedTime > t - 10) {
                        this.swirl = true;
                    }
                    if (elapsedTime > t) {
                        break block;
                    }
                    this.xd = MathHelper.lerp(0.25, this.xd, this.xd * 0.75);
                    this.zd = MathHelper.lerp(0.25, this.zd, this.zd * 0.75);
                }
            }
        }

        if (this.flowAway) {
            deltaX += this.xaFlowScale * Math.pow(curveProgress, 1.25);
            deltaZ += this.zaFlowScale * Math.pow(curveProgress, 1.25);
        }

        if (this.swirl) {
            deltaX += curveProgress * Math.cos(curveProgress * this.swirlPeriod) * this.windBig;
            deltaZ += curveProgress * Math.sin(curveProgress * this.swirlPeriod) * this.windBig;
        }


        this.xd += deltaX * ACCELERATION_SCALE;
        this.zd += deltaZ * ACCELERATION_SCALE;
        this.yd -= this.gravity;


        if (!this.onGround) {
            if (this.isBurst) {
                if (xd != 0.0D && zd != 0.0D) {
                    this.rotSpeed += this.spinAcceleration / 20.0f;
                    this.oRoll = this.roll;
                    this.roll += this.rotSpeed / 20.0f;
                }
            } else {
                this.rotSpeed += this.spinAcceleration / 20.0f;
                this.oRoll = this.roll;
                this.roll += this.rotSpeed / 20.0f;
            }
        }


        this.move(this.xd, this.yd, this.zd);
        if (this.onGround || (this.lifetime < 299 && (this.xd == 0.0 || this.zd == 0.0))) {
            if (this.onGround && this.block != null) {

                float chance = block == Blocks.LEAF_LITTER ? 0.32f : 0.08f;
                if (level.random.nextFloat() < chance) {
                    BlockPos abovePos = new BlockPos(x, y + 0.05, this.z);
                    Direction direction;
                    do {
                        direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    } while (direction == null);
                    BlockState state = block.defaultBlockState().setValue(PetalBlock.FACING, direction).setValue(PetalBlock.AMOUNT, random.nextInt(4) + 1);

                    if (block.canSurvive(state, this.level, abovePos)) {
                        if (level.getBlockState(abovePos).isAir()) {
                            this.level.sendPacketToServer(new SPlayGameEventPacket(GameEvent.UPDATE_BLOCK, new BlockPos(this.x, this.y + 0.05, this.z), Block.getId(state), false));
                        }
                    }
                }
            }
            if (!flowAway) {
                if (this.isBurst) {
                    if (elapsedTime > 10) {


                        if (elapsedTime > (100)) {

                            remove();

                        }
                    }
                } else {
                    remove();
                }
            } else {
                if (elapsedTime > 600) {
                    remove();
                }
            }
        }

    }

    public static class PaleOakProvider implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public PaleOakProvider(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FallingLeavesParticle(world, (PetalBlock) Blocks.PALE_LEAF_PILE, x, y, z, this.sprite, 0.07f, 10.0f, true, false, 2.0f, 0.021f, false, false);
        }
    }

    public static class DeadLeavesProvider implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public DeadLeavesProvider(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FallingLeavesParticle(world, (PetalBlock) Blocks.LEAF_LITTER, x, y, z, this.sprite, 0.07f, 10.0f, true, false, 2.0f, 0.021f, false, false);
        }
    }

    public static class TintedLeavesProvider
            implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;

        public TintedLeavesProvider(IAnimatedSprite spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(BasicParticleType simpleParticleType, ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            FallingLeavesParticle fallingLeavesParticle = new FallingLeavesParticle(clientLevel, null, d, d2, d3, this.sprites, 0.07f, 10.0f, true, false, 2.0f, 0.021f, false, false);
            BlockPos blockPos = BlockPos.containing(d, d2, d3).above();
            BlockState blockState = clientLevel.getBlockState(blockPos);
            int n = Minecraft.getInstance().getBlockColors().getColor(blockState, clientLevel, blockPos, 0);
            fallingLeavesParticle.setColor(ARGB.redFloat(n), ARGB.greenFloat(n), ARGB.blueFloat(n));
            return fallingLeavesParticle;
        }
    }

    public static class BurstingTintedLeavesProvider
            implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;

        public BurstingTintedLeavesProvider(IAnimatedSprite spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(BasicParticleType simpleParticleType, ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            FallingLeavesParticle fallingLeavesParticle = new FallingLeavesParticle(clientLevel, null, d, d2, d3, this.sprites, 0.8f, 5.0f, false, false, 2.0f, 0.021f, true, false);
            BlockPos blockPos = BlockPos.containing(d, d2, d3).above();
            BlockState blockState = clientLevel.getBlockState(blockPos);
            int n = Minecraft.getInstance().getBlockColors().getColor(blockState, clientLevel, blockPos, 0);
            fallingLeavesParticle.setColor(ARGB.redFloat(n), ARGB.greenFloat(n), ARGB.blueFloat(n));
            return fallingLeavesParticle;
        }
    }
}
