package net.minecraft.world.gen;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.util.SharedSeedRandom;

public class PerlinNoiseGenerator implements INoiseGenerator {

   private final SimplexNoiseGenerator[] noiseLevels;
   private final double highestFreqValueFactor;
   private final double highestFreqInputFactor;
   public PerlinNoiseGenerator(SharedSeedRandom random, IntStream octavesStream) {
      this(random, octavesStream.boxed().collect(ImmutableList.toImmutableList()));
   }

   public PerlinNoiseGenerator(SharedSeedRandom random, List<Integer> octaves) {
      this(random, new IntRBTreeSet(octaves));
   }


   private PerlinNoiseGenerator(SharedSeedRandom random, IntSortedSet octavesSet) {
      if (octavesSet.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      }

      int minOctave = -octavesSet.firstInt();
      int maxOctave = octavesSet.lastInt();
      int totalOctaves = minOctave + maxOctave + 1;

      if (totalOctaves < 1) {
         throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
      }

      this.noiseLevels = new SimplexNoiseGenerator[totalOctaves];
      SimplexNoiseGenerator baseNoiseGenerator = new SimplexNoiseGenerator(random);

      if (octavesSet.contains(0)) {
         this.noiseLevels[maxOctave] = baseNoiseGenerator;
      }

      initializeNoiseLevels(random, octavesSet, minOctave, maxOctave, totalOctaves, baseNoiseGenerator);

      this.highestFreqInputFactor = Math.pow(2.0D, maxOctave);
      this.highestFreqValueFactor = 1.0D / (Math.pow(2.0D, totalOctaves) - 1.0D);
   }

   private void initializeNoiseLevels(SharedSeedRandom random, IntSortedSet octavesSet, int minOctave, int maxOctave, int totalOctaves, SimplexNoiseGenerator baseNoiseGenerator) {
      for (int i = maxOctave + 1; i < totalOctaves; ++i) {
         if (octavesSet.contains(minOctave + i)) {
            this.noiseLevels[i] = new SimplexNoiseGenerator(random);
         } else {
            random.consumeCount(262);
         }
      }

      if (maxOctave > 0) {
         long seed = (long) (baseNoiseGenerator.getValue(baseNoiseGenerator.xo, baseNoiseGenerator.yo, baseNoiseGenerator.zo) * 9.223372E18F);
         SharedSeedRandom sharedSeedRandom = new SharedSeedRandom(seed);

         for (int i = maxOctave - 1; i >= 0; --i) {
            if (octavesSet.contains(minOctave + i)) {
               this.noiseLevels[i] = new SimplexNoiseGenerator(sharedSeedRandom);
            } else {
               sharedSeedRandom.consumeCount(262);
            }
         }
      }
   }

   public double getValue(double x, double y, boolean useOffsets) {
      double result = 0.0D;
      double frequency = this.highestFreqInputFactor;
      double amplitude = this.highestFreqValueFactor;

      for (SimplexNoiseGenerator noiseGenerator : this.noiseLevels) {
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
