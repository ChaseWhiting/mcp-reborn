package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PetalBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.play.server.SPlayGameEventPacket;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class FallingLeavesParticle extends SpriteTexturedParticle {
   private static final float ACCELERATION_SCALE = 0.0025f;
   private static final int INITIAL_LIFETIME = 300;
   private static final int CURVE_ENDPOINT_TIME = 300;
   private float rotSpeed;
   private final float particleRandom;
   private final float spinAcceleration;
   private final float windBig;
   private boolean swirl;
   private boolean flowAway;
   private double xaFlowScale;
   private double zaFlowScale;
   private double swirlPeriod;
   private final IAnimatedSprite sprites;
   @Nullable
   private final PetalBlock block;

   protected FallingLeavesParticle(ClientWorld world, @Nullable PetalBlock block, double x, double y, double z, IAnimatedSprite sprite, float gravity, float windBig, boolean swirl, boolean flowAway, float size, float ySpeed) {
      super(world, x, y, z);
      this.sprites = sprite;
      this.setSpriteFromAge(sprites);
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

      float elapsedTime = INITIAL_LIFETIME - this.lifetime;
      float curveProgress = Math.min(elapsedTime / CURVE_ENDPOINT_TIME, 1.0f);
      double deltaX = 0.0;
      double deltaZ = 0.0;

      if (this.flowAway) {
         deltaX += this.xaFlowScale * Math.pow(curveProgress, 1.25);
         deltaZ += this.zaFlowScale * Math.pow(curveProgress, 1.25);
      }

      // Swirl effect
      if (this.swirl) {
         deltaX += curveProgress * Math.cos(curveProgress * this.swirlPeriod) * this.windBig;
         deltaZ += curveProgress * Math.sin(curveProgress * this.swirlPeriod) * this.windBig;
      }

      this.xd += deltaX * ACCELERATION_SCALE;
      this.zd += deltaZ * ACCELERATION_SCALE;
      this.yd -= this.gravity;

      this.rotSpeed += this.spinAcceleration / 20.0f;
      this.oRoll = this.roll;
      this.roll += this.rotSpeed / 20.0f;

      this.move(this.xd, this.yd, this.zd);
      if (this.onGround || (this.lifetime < 299 && (this.xd == 0.0 || this.zd == 0.0))) {
         if (this.onGround && this.block != null) {
            if (level.random.nextFloat() < 0.08) {
               BlockPos abovePos = new BlockPos(x, y + 0.05, this.z);
               Direction direction = Direction.getRandom(random);
               do {
                  direction = Direction.getRandom(random);
               } while (direction == Direction.UP || direction == Direction.DOWN);
               BlockState state = block.defaultBlockState().setValue(PetalBlock.FACING, direction).setValue(PetalBlock.AMOUNT, random.nextInt(4) + 1);

               if (block.canSurvive(state, this.level, abovePos)) {
                  if (level.getBlockState(abovePos).isAir()) {
                     this.level.sendPacketToServer(new SPlayGameEventPacket(GameEvent.UPDATE_BLOCK, new BlockPos(this.x, this.y + 0.05, this.z), Block.getId(state), false));
                  }
               }
            }
         }
         this.remove();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class PaleOakProvider implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public PaleOakProvider(IAnimatedSprite sprite) {
         this.sprite = sprite;
      }

      @Override
      public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new FallingLeavesParticle(world, (PetalBlock)Blocks.PALE_LEAF_PILE, x, y, z, this.sprite, 0.07f, 10.0f, true, false, 2.0f, 0.021f);
      }
   }
}
