package net.minecraft.world.gen;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.util.SharedSeedRandom;

public class PerlinNoiseGenerator implements INoiseGenerator {

   public enum NoiseType {
      PERLIN,
      SIMPLEX,
      VALUE,
      VORONOI,
      WHITE_NOISE,
      CUBIC_NOISE
   }

   private final SimplexNoiseGenerator[] noiseLevels;
   private final double highestFreqValueFactor;
   private final double highestFreqInputFactor;
   private final NoiseType noiseType;

   // Default constructor for PERLIN noise
   public PerlinNoiseGenerator(SharedSeedRandom random, IntStream octavesStream) {
      this(random, octavesStream.boxed().collect(ImmutableList.toImmutableList()), NoiseType.PERLIN);
   }

   // Default constructor for PERLIN noise
   public PerlinNoiseGenerator(SharedSeedRandom random, List<Integer> octaves) {
      this(random, new IntRBTreeSet(octaves), NoiseType.PERLIN);
   }

   // New constructor to specify noise type
   public PerlinNoiseGenerator(SharedSeedRandom random, List<Integer> octaves, NoiseType noiseType) {
      this(random, new IntRBTreeSet(octaves), noiseType);
   }

   private PerlinNoiseGenerator(SharedSeedRandom random, IntSortedSet octavesSet, NoiseType noiseType) {
      if (octavesSet.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      }

      this.noiseType = noiseType;
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

      return switch (noiseType) {
         case SIMPLEX -> new SimplexNoise().getValue(result);
         case VALUE -> new ValueNoise().getValue(result);
         case VORONOI -> new VoronoiNoise().getValue(result);
         case WHITE_NOISE -> new WhiteNoise().getValue(result);
         case CUBIC_NOISE -> new CubicNoise().getValue(result);
         default -> result; // Default is PERLIN
      };
   }

   @Override
   public double getSurfaceNoiseValue(double x, double y, double scale, double depth) {
      return this.getValue(x, y, true) * 0.55D;
   }

   private static class SimplexNoise {
      // Constants for the Simplex noise algorithm, simplified for example
      private static final double GRAD_2D[][] = {
              {1,1}, {-1,1}, {1,-1}, {-1,-1},
              {1,0}, {-1,0}, {1,0}, {-1,0},
              {0,1}, {0,-1}, {0,1}, {0,-1}
      };

      public double getValue(double input) {
         // Simplified Simplex noise calculation (2D case)
         int i0 = (int) Math.floor(input);
         int i1 = i0 + 1;
         double x0 = input - i0;
         double x1 = x0 - 1.0;

         double n0, n1;

         double t0 = 0.5 - x0 * x0;
         if (t0 < 0) n0 = 0.0;
         else {
            t0 *= t0;
            int gi0 = i0 % 12;
            n0 = t0 * t0 * dot(GRAD_2D[gi0], x0);
         }

         double t1 = 0.5 - x1 * x1;
         if (t1 < 0) n1 = 0.0;
         else {
            t1 *= t1;
            int gi1 = i1 % 12;
            n1 = t1 * t1 * dot(GRAD_2D[gi1], x1);
         }

         return 70.0 * (n0 + n1);  // Scale to [-1,1] range
      }

      // Dot product function
      private double dot(double[] g, double x) {
         return g[0] * x;
      }
   }

   // Inner class for Value noise type
   private static class ValueNoise {
      public double getValue(double input) {
         // Simplified Value noise calculation
         int x0 = (int) Math.floor(input);
         int x1 = x0 + 1;
         double t = input - x0;

         // Interpolate between the two random values
         double rand0 = random(x0);
         double rand1 = random(x1);

         return lerp(t, rand0, rand1);  // Linear interpolation
      }

      // Simple hash function to simulate random values
      private double random(int x) {
         x = (x << 13) ^ x;
         return (1.0 - ((x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
      }

      // Linear interpolation function
      private double lerp(double t, double a, double b) {
         return a + t * (b - a);
      }
   }

   // Inner class for Voronoi noise type
   private static class VoronoiNoise {
      public double getValue(double input) {
         // Simplified Voronoi noise calculation
         int cellX = (int) Math.floor(input);
         double minDist = Double.MAX_VALUE;

         for (int i = -1; i <= 1; i++) {
            double point = random(cellX + i);
            double dist = Math.abs(input - (cellX + i + point));
            if (dist < minDist) {
               minDist = dist;
            }
         }

         return minDist;  // Return distance to nearest point
      }

      // Simple hash function for random points
      private double random(int x) {
         x = (x << 13) ^ x;
         return (1.0 - ((x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
      }
   }

   // Inner class for White noise type
   private static class WhiteNoise {
      public double getValue(double input) {
         // White noise is just random value generation
         int x = (int) Math.floor(input);
         return random(x);
      }

      // Simple hash function to return random values
      private double random(int x) {
         x = (x << 13) ^ x;
         return (1.0 - ((x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
      }
   }

   // Inner class for Cubic noise type
   private static class CubicNoise {
      public double getValue(double input) {
         // Simplified Cubic noise calculation
         int x0 = (int) Math.floor(input);
         double t = input - x0;

         double v0 = random(x0 - 1);
         double v1 = random(x0);
         double v2 = random(x0 + 1);
         double v3 = random(x0 + 2);

         return cubicInterpolate(v0, v1, v2, v3, t);
      }

      // Simple hash function to simulate random values
      private double random(int x) {
         x = (x << 13) ^ x;
         return (1.0 - ((x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
      }

      // Cubic interpolation
      private double cubicInterpolate(double v0, double v1, double v2, double v3, double t) {
         double P = (v3 - v2) - (v0 - v1);
         double Q = (v0 - v1) - P;
         double R = v2 - v0;
         double S = v1;
         return P * t * t * t + Q * t * t + R * t + S;
      }
   }
}
