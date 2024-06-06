package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpawnerBlock extends ContainerBlock {
   protected SpawnerBlock(AbstractBlock.Properties p_i48364_1_) {
      super(p_i48364_1_);
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new MobSpawnerTileEntity();
   }

   public void spawnAfterBreak(BlockState p_220062_1_, ServerWorld p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
      super.spawnAfterBreak(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);
      int i = 15 + p_220062_2_.random.nextInt(15) + p_220062_2_.random.nextInt(15);
      this.popExperience(p_220062_2_, p_220062_3_, i);
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }
}