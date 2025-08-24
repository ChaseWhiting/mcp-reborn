package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.PetalBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class LeafLitterTreeDecorator extends TreeDecorator {
   public static final Codec<LeafLitterTreeDecorator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
           Codec.INT.optionalFieldOf("circleSize", 3).forGetter((decorator) -> decorator.circleSize),
           Codec.INT.optionalFieldOf("offsetRange", 16).forGetter((decorator) -> decorator.offsetRange)
   ).apply(instance, LeafLitterTreeDecorator::new));

   private final BlockStateProvider leafLitterProvider;

   private final int circleSize;  // The size of the circle around the log (default 3)
   private final int offsetRange; // The range for random offsets around the log (default 16)

   // No-argument constructor with default circle size and offset range
   public LeafLitterTreeDecorator() {
      this(3, 16);  // Default values
   }

   // Second constructor that takes circleSize and offsetRange as parameters
   public LeafLitterTreeDecorator(int circleSize, int offsetRange) {
      this.leafLitterProvider = new SimpleBlockStateProvider(Blocks.LEAF_LITTER.defaultBlockState());
      this.circleSize = circleSize;
      this.offsetRange = offsetRange;
   }

   @Override
   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.LEAF_LITTER;
   }

   @Override
   public void place(ISeedReader world, Random random, List<BlockPos> logs, List<BlockPos> leaves, Set<BlockPos> changedBlocks, MutableBoundingBox boundingBox) {
      int baseY = logs.get(0).getY(); // Y-coordinate of the base log
      boolean isBirch = world.getBlockState(logs.get(0)).is(Blocks.BIRCH_LOG);
      logs.stream().filter((logPosition) -> logPosition.getY() == baseY)
              .forEach((logPosition) -> {
                 // Place decoration in four cardinal directions around the log, but closer
                 this.placeCircle(world, random, logPosition.west(), isBirch);
                 this.placeCircle(world, random, logPosition.east(), isBirch);
                 this.placeCircle(world, random, logPosition.north(), isBirch);
                 this.placeCircle(world, random, logPosition.south(), isBirch);

                 // Randomly place additional circles in a smaller area around the log based on offsetRange
                 for (int j = 0; j < 2; ++j) {
                    int randomOffset = random.nextInt(offsetRange); // Use customizable range
                    int offsetX = randomOffset % 4;
                    int offsetZ = randomOffset / 4;

                    // Only place at the edges of the smaller grid
                    if (offsetX == 0 || offsetX == 3 || offsetZ == 0 || offsetZ == 3) {
                       this.placeCircle(world, random, logPosition.offset(-1 + offsetX, 0, -1 + offsetZ), isBirch);
                    }
                 }
              });
   }

   private void placeCircle(ISeedReader world, Random random, BlockPos center, boolean isBirch) {
      // Use customizable circle size for placement
      for (int xOffset = -circleSize / 2; xOffset <= circleSize / 2; ++xOffset) {
         for (int zOffset = -circleSize / 2; zOffset <= circleSize / 2; ++zOffset) {
            // Only place blocks in the inner part of the square (no corners)
            if (Math.abs(xOffset) != 1 || Math.abs(zOffset) != 1) {
               this.placeBlock(world, random, center.offset(xOffset, 0, zOffset), isBirch);
            }
         }
      }
   }

   private void placeBlock(ISeedReader world, Random random, BlockPos position, boolean isBirch) {
      for (int yOffset = 2; yOffset >= -3; --yOffset) {
         BlockPos adjustedPos = position.above(yOffset);
         Optional<RegistryKey<Biome>> biome = world.getBiomeName(position);
         // Ensure the block below is dirt, grass, etc., and prevent replacing logs or air
         if (Feature.isGrassOrDirt(world, adjustedPos) && !world.getBlockState(adjustedPos).is(BlockTags.LOGS_THAT_BURN)) {
            BlockPos abovePos = adjustedPos.above();

            // Ensure there's air above and the block above is not already a decoration
            if (Feature.isAir(world, abovePos)) {
               if (random.nextFloat() < 0.6f) {

                  // 30% chance to place pale moss carpet, but only if the block below is solid
                  if (world.getBlockState(adjustedPos).canOcclude()) { // Ensures the block below is solid
                     int randomNumber = random.nextInt(4) + 1;
                     Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                     world.setBlock(abovePos, this.leafLitterProvider.getState(random, abovePos).setValue(PetalBlock.AMOUNT, randomNumber).setValue(PetalBlock.FACING, randomFacing), 19);
                  }
               }
               if (random.nextFloat() < 0.005f && isBirch) {

                  // 30% chance to place pale moss carpet, but only if the block below is solid
                  if (world.getBlockState(adjustedPos).canOcclude()) { // Ensures the block below is solid
                     int randomNumber = random.nextInt(4) + 1;
                     Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                     world.setBlock(abovePos, Blocks.WILDFLOWERS.defaultBlockState().setValue(PetalBlock.AMOUNT, randomNumber).setValue(PetalBlock.FACING, randomFacing), 19);
                  }
               }
            }
            break;
         }

         // Break the loop if there's no air below and it's past yOffset 0, ensuring no floating carpets
         if (!Feature.isAir(world, adjustedPos) && yOffset < 0) {
            break;
         }
      }
   }
}
