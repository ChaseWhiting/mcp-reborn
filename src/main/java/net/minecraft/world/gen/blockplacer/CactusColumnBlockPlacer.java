package net.minecraft.world.gen.blockplacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.block.Blocks;

public class CactusColumnBlockPlacer extends BlockPlacer {
   public static final Codec<CactusColumnBlockPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(
         Codec.INT.fieldOf("min_size").forGetter((placer) -> placer.minSize),
         Codec.INT.fieldOf("extra_size").forGetter((placer) -> placer.extraSize),
         Codec.FLOAT.fieldOf("flower_chance").forGetter((placer) -> placer.flowerChance) // Added flower chance
      ).apply(instance, CactusColumnBlockPlacer::new);
   });

   private final int minSize;
   private final int extraSize;
   private final float flowerChance; // Probability for a flower to generate on top

   public CactusColumnBlockPlacer(int minSize, int extraSize, float flowerChance) {
      this.minSize = minSize;
      this.extraSize = extraSize;
      this.flowerChance = flowerChance;
   }

   @Override
   protected BlockPlacerType<?> type() {
      return BlockPlacerType.CACTUS_PLACER; // Uses the existing column placer type
   }

   @Override
   public void place(IWorld world, BlockPos pos, BlockState state, Random random) {
      BlockPos.Mutable mutablePos = pos.mutable();
      int height = this.minSize + random.nextInt(random.nextInt(this.extraSize + 1) + 1);

      // Generate the cactus column
      for (int j = 0; j < height; ++j) {
         world.setBlock(mutablePos, state, 2);
         mutablePos.move(Direction.UP);
      }

      // 20% chance (example) to place a cactus flower on top
      if (random.nextFloat() < this.flowerChance) {
         world.setBlock(mutablePos, Blocks.CACTUS_FLOWER.defaultBlockState(), 2);
      }
   }
}
