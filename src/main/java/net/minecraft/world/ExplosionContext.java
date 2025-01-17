package net.minecraft.world;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

public class ExplosionContext {
   public Optional<Float> getBlockExplosionResistance(Explosion p_230312_1_, IBlockReader p_230312_2_, BlockPos p_230312_3_, BlockState p_230312_4_, FluidState p_230312_5_) {
      return p_230312_4_.isAir() && p_230312_5_.isEmpty() ? Optional.empty() : Optional.of(Math.max(p_230312_4_.getBlock().getExplosionResistance(), p_230312_5_.getExplosionResistance()));
   }

   public Optional<Float> getBlockExplosionResistance(NuclearExplosion explosion, IBlockReader iBlockReader, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      return blockState.isAir() && fluidState.isEmpty() ? Optional.empty() : Optional.of(Math.max(blockState.getBlock().getExplosionResistance(), fluidState.getExplosionResistance()) / 2F);
   }

   public boolean shouldBlockExplode(Explosion p_230311_1_, IBlockReader p_230311_2_, BlockPos p_230311_3_, BlockState p_230311_4_, float p_230311_5_) {
      return true;
   }

   public boolean shouldBlockExplode(NuclearExplosion p_230311_1_, IBlockReader p_230311_2_, BlockPos p_230311_3_, BlockState p_230311_4_, float p_230311_5_) {
      return true;
   }
}