package net.minecraft.world.gen;

import java.util.Random;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;

public class CellularNoise {
   public final double xo;
   public final double yo;

   public CellularNoise(SharedSeedRandom random) {
      this.xo = random.nextDouble() * 256.0D;
      this.yo = random.nextDouble() * 256.0D;
   }

   // Generates cellular noise by computing distance to the nearest feature point
   public double getValue(double x, double y) {
      double distance = Double.MAX_VALUE;

      // Define a grid for feature points
      int gridX = MathHelper.floor(x);
      int gridY = MathHelper.floor(y);

      // Search neighboring cells for closest feature point
      for (int offsetX = -1; offsetX <= 1; offsetX++) {
         for (int offsetY = -1; offsetY <= 1; offsetY++) {
            double featureX = (gridX + offsetX) + randomFeatureOffset(gridX + offsetX, gridY + offsetY);
            double featureY = (gridY + offsetY) + randomFeatureOffset(gridX + offsetX, gridY + offsetY);
            double currentDistance = calculateDistance(x, y, featureX, featureY);

            if (currentDistance < distance) {
               distance = currentDistance;
            }
         }
      }

      return distance;
   }

   // Helper to generate feature offsets
   private double randomFeatureOffset(int gridX, int gridY) {
      Random random = new Random((long)(gridX * 31 + gridY * 57));
      return random.nextDouble();
   }

   // Calculate Euclidean distance
   private double calculateDistance(double x1, double y1, double x2, double y2) {
      double dx = x1 - x2;
      double dy = y1 - y2;
      return Math.sqrt(dx * dx + dy * dy);
   }
}
