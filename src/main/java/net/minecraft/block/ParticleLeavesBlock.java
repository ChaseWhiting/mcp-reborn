package net.minecraft.block;

import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class ParticleLeavesBlock extends LeavesBlock {
   private final IParticleData particle;
   private final int chance;

   public ParticleLeavesBlock(int chance, IParticleData particle, Properties properties) {
      super(properties);
      this.chance = chance;
      this.particle = particle;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
      super.animateTick(state, world, pos, random);

      // Check if the particle should spawn based on the chance
      if (random.nextInt(this.chance) != 0) {
         return;
      }

      BlockPos belowPos = pos.below();
      BlockState belowState = world.getBlockState(belowPos);

      // Check if the block below is solid
      if (!belowState.canOcclude() || !belowState.isFaceSturdy(world, belowPos, Direction.UP)) {
         spawnParticleBelow(world, pos, random, this.particle);
      }
   }

   private static void spawnParticleBelow(World world, BlockPos pos, Random random, IParticleData particle) {
      double x = (double) pos.getX() + random.nextDouble();
      double y = (double) pos.getY() - 0.05;
      double z = (double) pos.getZ() + random.nextDouble();
      world.addAlwaysVisibleParticle(particle, x, y, z, 0.0, 0.0, 0.0);
   }
}
