package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public abstract class SimpleHeightmapBasedPlacement<DC extends IPlacementConfig> extends HeightmapBasedPlacement<DC> {
   public SimpleHeightmapBasedPlacement(Codec<DC> p_i242013_1_) {
      super(p_i242013_1_);
   }

   @Override
   public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, DC config, BlockPos pos) {
      int x = pos.getX();
      int z = pos.getZ();

      int y;
      try {
         // Try normal heightmap lookup first
         y = helper.getHeight(this.type(config), x, z);
      } catch (NullPointerException e) {
         // Fallback if heightmap missing — scan down from max build height
         y = fallbackHeight(helper, x, z);
      }

      return y > 0 ? Stream.of(new BlockPos(x, y, z)) : Stream.of();
   }

   private int fallbackHeight(WorldDecoratingHelper helper, int x, int z) {
      // Scan from top of world down until we find a solid, non-replaceable block
      int y = helper.getGenDepth() - 1;
      BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

      while (y > 0) {
         BlockState state = helper.getBlockState(mutable);
         if (!state.isAir() && !state.getMaterial().isReplaceable()) {
            return y;
         }
         y--;
         mutable.setY(y);
      }
      return 0;
   }

}