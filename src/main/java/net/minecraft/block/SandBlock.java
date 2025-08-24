package net.minecraft.block;

import net.minecraft.block.sounds.AmbientDesertBlockSoundsPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class SandBlock extends FallingBlock {
   private final int dustColor;

   public SandBlock(int p_i48338_1_, AbstractBlock.Properties p_i48338_2_) {
      super(p_i48338_2_);
      this.dustColor = p_i48338_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState p_189876_1_, IBlockReader p_189876_2_, BlockPos p_189876_3_) {
      return this.dustColor;
   }

   @Override
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      AmbientDesertBlockSoundsPlayer.playAmbientBlockSounds(p_180655_1_, p_180655_2_, p_180655_3_, p_180655_4_);
   }
}