package net.minecraft.block;

import javax.annotation.Nullable;

import net.minecraft.entity.warden.event.GameEventListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public interface ITileEntityProvider {
   @Nullable
   TileEntity newBlockEntity(IBlockReader p_196283_1_);

   @Nullable
   default public <T extends TileEntity> GameEventListener getListener(ServerWorld serverWorld, T t) {
      return null;
   }
}