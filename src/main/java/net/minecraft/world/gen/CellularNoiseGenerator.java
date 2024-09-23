package net.minecraft.world.gen;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.util.SharedSeedRandom;

public class CellularNoiseGenerator implements INoiseGenerator {
   private final CellularNoise[] noiseLevels;
   private final double highestFreqValueFactor;
   private final double highestFreqInputFactor;

   public CellularNoiseGenerator(SharedSeedRandom random, IntStream octavesStream) {
      this(random, octavesStream.boxed().collect(ImmutableList.toImmutableList()));
   }

   public CellularNoiseGenerator(SharedSeedRandom random, List<Integer> octaves) {
      this(random, new IntRBTreeSet(octaves));
   }

   private CellularNoiseGenerator(SharedSeedRandom random, IntSortedSet octavesSet) {
      if (octavesSet.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      }

      int minOctave = -octavesSet.firstInt();
      int maxOctave = octavesSet.lastInt();
      int totalOctaves = minOctave + maxOctave + 1;

      if (totalOctaves < 1) {
         throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
      }

      this.noiseLevels = new CellularNoise[totalOctaves];
      CellularNoise baseNoiseGenerator = new CellularNoise(random);

      if (octavesSet.contains(0)) {
         this.noiseLevels[maxOctave] = baseNoiseGenerator;
      }

      for (int i = maxOctave + 1; i < totalOctaves; ++i) {
         if (octavesSet.contains(minOctave + i)) {
            this.noiseLevels[i] = new CellularNoise(random);
         } else {
            random.consumeCount(262);
         }
      }

      if (maxOctave > 0) {
         long seed = (long) (baseNoiseGenerator.getValue(baseNoiseGenerator.xo, baseNoiseGenerator.yo) * 9.223372E18F);
         SharedSeedRandom sharedSeedRandom = new SharedSeedRandom(seed);

         for (int i = maxOctave - 1; i >= 0; --i) {
            if (octavesSet.contains(minOctave + i)) {
               this.noiseLevels[i] = new CellularNoise(sharedSeedRandom);
            } else {
               sharedSeedRandom.consumeCount(262);
            }
         }
      }

      this.highestFreqInputFactor = Math.pow(2.0D, maxOctave);
      this.highestFreqValueFactor = 1.0D / (Math.pow(2.0D, totalOctaves) - 1.0D);
   }

   public double getValue(double x, double y, boolean useOffsets) {
      double result = 0.0D;
      double frequency = this.highestFreqInputFactor;
      double amplitude = this.highestFreqValueFactor;

      for (CellularNoise noiseGenerator : this.noiseLevels) {
         if (noiseGenerator != null) {
            double offsetX = useOffsets ? noiseGenerator.xo : 0.0D;
            double offsetY = useOffsets ? noiseGenerator.yo : 0.0D;
            result += noiseGenerator.getValue(x * frequency + offsetX, y * frequency + offsetY) * amplitude;
         }

         frequency /= 2.0D;
         amplitude *= 2.0D;
      }

      return result;
   }

   @Override
   public double getSurfaceNoiseValue(double x, double y, double scale, double depth) {
      return this.getValue(x, y, true) * 0.55D;
   }
}
