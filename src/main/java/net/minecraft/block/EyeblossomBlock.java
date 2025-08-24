package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.particles.TrailParticleOption;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;
@SuppressWarnings("all")
public class EyeblossomBlock extends FlowerBlock {
   private final Type type;

   public EyeblossomBlock(Type type, Properties settings) {
      super(type.effect, type.effectDuration, settings.emissiveRendering(Blocks::never));
      this.type = type;
   }

   @Override
   public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
      if (this.type.emitSounds() && random.nextInt(700) == 0 && world.getBlockState(pos.below()).getBlock() == Blocks.PALE_MOSS_BLOCK) {
         world.playSound(null, pos, SoundEvents.EYEBLOSSOM_IDLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
      }
   }

   @Override
   public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (type == Type.OPEN && isSoulLanternOrFireNearby(world, pos) && world.isDay()) {
         super.randomTick(state, world, pos, random);
         return;
      }
      if (this.tryChangingState(state, world, pos, random)) {
         world.playSound(null, pos, this.type.transform().longSwitchSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
      }
      super.randomTick(state, world, pos, random);
   }

   @Override
   public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (type == Type.OPEN && isSoulLanternOrFireNearby(world, pos) && world.isDay()) {
         super.tick(state, world, pos, random);
         return;
      }
      if (this.tryChangingState(state, world, pos, random)) {
         world.playSound(null, pos, this.type.transform().shortSwitchSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
      }
      super.tick(state, world, pos, random);
   }

   private boolean tryChangingState(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (world.isDay() && this.type == Type.CLOSED && isSoulLanternOrFireNearby(world, pos)) {
         Type newType = this.type.transform();
         world.setBlock(pos, newType.state(), 3);

         newType.spawnTransformParticle(world, pos, random);
         return true;
      }

      if (!world.dimensionType().natural() || world.isDay() != this.type.open) {
         return false;
      }

      Type newType = this.type.transform();
      world.setBlock(pos, newType.state(), 3);

      newType.spawnTransformParticle(world, pos, random);

      BlockPos.betweenClosed(pos.offset(-3, -2, -3), pos.offset(3, 2, 3)).forEach(neighborPos -> {
         BlockState neighborState = world.getBlockState(neighborPos);
         if (neighborState.getBlock() == state.getBlock()) {
            double distance = Math.sqrt(pos.distSqr(neighborPos));
            int delay = nextIntBetweenInclusive((int) (distance * 5.0), (int) (distance * 10.0), random);
            world.getBlockTicks().scheduleTick(neighborPos, this, delay, TickPriority.HIGH);
         }
      });

      return true;
   }

   public static boolean isSoulLanternOrFireNearby(IServerWorld world, BlockPos pos) {
      int radius = 7;

      for (int x = -radius; x <= radius; x++) {
         for (int y = -radius; y <= radius; y++) {
            for (int z = -radius; z <= radius; z++) {
               BlockPos currentPos = pos.offset(x, y, z);
               Block block = world.getBlockState(currentPos).getBlock();
               if (block == Blocks.SOUL_LANTERN || block == Blocks.SOUL_FIRE || block == Blocks.SOUL_CAMPFIRE && world.getBlockState(currentPos).getValue(CampfireBlock.LIT)) {
                  return true;
               }
            }
         }
      }

      return isCreakingNearby(world, pos);
   }

   public static boolean isCreakingNearby(IServerWorld world, BlockPos pos) {
      List<CreakingEntity> creakingEntities = world.getEntitiesOfClass(CreakingEntity.class, new AxisAlignedBB(pos).inflate(12, 8, 12));

      return !creakingEntities.isEmpty();
   }

   public static int nextIntBetweenInclusive(int $$0, int $$1, Random random) {
      return random.nextInt($$1 - $$0 + 1) + $$0;
   }


   @Override
   public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
      if (!world.isClientSide && entity instanceof CreakingEntity) {
         if (world.isDay() && this.type == Type.CLOSED) {
            Type newType = this.type.transform();
            world.setBlock(pos, newType.state(), 3);

            newType.spawnTransformParticle((ServerWorld) world, pos, ((CreakingEntity) entity).getRandom());
         }
      }
      if (!world.isClientSide && world.getDifficulty() != Difficulty.PEACEFUL && entity instanceof BeeEntity) {
         BeeEntity bee = entity.as(BeeEntity.class);
         if (!bee.hasEffect(Effects.POISON)) {
            bee.addEffect(new EffectInstance(Effects.POISON, 25));
         }
      }
   }

   public enum Type {
      OPEN(true, Effects.BLINDNESS, 7, SoundEvents.EYEBLOSSOM_OPEN_LONG, SoundEvents.EYEBLOSSOM_OPEN, 16545810),
      CLOSED(false, Effects.CONFUSION, 7, SoundEvents.EYEBLOSSOM_CLOSE_LONG, SoundEvents.EYEBLOSSOM_CLOSE, 0x5F5F5F);

      final boolean open;
      final Effect effect;
      final int effectDuration;
      final SoundEvent longSwitchSound;
      final SoundEvent shortSwitchSound;
      final int particleColor;

      Type(boolean open, Effect effect, int effectDuration, SoundEvent longSwitchSound, SoundEvent shortSwitchSound, int particleColor) {
         this.open = open;
         this.effect = effect;
         this.effectDuration = effectDuration;
         this.longSwitchSound = longSwitchSound;
         this.shortSwitchSound = shortSwitchSound;
         this.particleColor = particleColor;
      }

      public static Type fromBoolean(boolean open) {
         return open ? OPEN : CLOSED;
      }

      public boolean emitSounds() {
         return this.open;
      }

      public Type transform() {
         return this == OPEN ? CLOSED : OPEN;
      }

      public Block block() {
         return this.open ? Blocks.OPEN_EYEBLOSSOM : Blocks.CLOSED_EYEBLOSSOM;
      }

      public BlockState state() {
         return this.block().defaultBlockState();
      }

      public void spawnTransformParticle(ServerWorld world, BlockPos pos, Random random) {
         Vector3d vector3d = pos.getCenter();
         double d = 0.5 + random.nextDouble();
         Vector3d vector3d1 = new Vector3d(random.nextDouble() - 0.5, random.nextDouble() + 1.0, random.nextDouble() - 0.5);
         Vector3d vector3d2 = vector3d.add(vector3d1.scale(d));
         TrailParticleOption trailParticleOption = new TrailParticleOption(vector3d2, this.particleColor, (int)(20.0 * d));
         world.sendParticles(trailParticleOption, vector3d.x, vector3d.y, vector3d.z, 1, 0.0, 0.0, 0.0, 0.0);
      }

   }
}
