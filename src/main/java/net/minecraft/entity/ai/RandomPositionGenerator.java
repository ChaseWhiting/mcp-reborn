package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

import net.minecraft.entity.Creature;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Utility class for generating random positions for creatures within certain parameters.
 */
public class RandomPositionGenerator {

   /**
    * Generates a random position for the creature within the specified horizontal and vertical range.
    *
    * @param creature The creature for which to generate the position.
    * @param xzRange  The maximum horizontal distance (X and Z) to consider.
    * @param yRange   The maximum vertical distance (Y) to consider.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getPos(Creature creature, int xzRange, int yRange) {
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              0,
              null,
              true,
              Math.PI / 2F,
              creature::getWalkTargetValue,
              false,
              0,
              0,
              true
      );
   }

   /**
    * Generates a random air position for the creature towards a target vector within the specified ranges.
    *
    * @param creature   The creature for which to generate the position.
    * @param xzRange    The maximum horizontal distance (X and Z) to consider.
    * @param yRange     The maximum vertical distance (Y) to consider.
    * @param yOffset    The Y offset to add to the random position.
    * @param targetVec3 The target vector towards which to generate the position.
    * @param angle      The angle in radians within which to consider positions.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getAirPos(Creature creature, int xzRange, int yRange, int yOffset, @Nullable Vector3d targetVec3, double angle) {
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              yOffset,
              targetVec3,
              true,
              angle,
              creature::getWalkTargetValue,
              true,
              0,
              0,
              false
      );
   }

   /**
    * Generates a random land position for the creature within the specified horizontal and vertical range.
    *
    * @param creature The creature for which to generate the position.
    * @param xzRange  The maximum horizontal distance (X and Z) to consider.
    * @param yRange   The maximum vertical distance (Y) to consider.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getLandPos(Creature creature, int xzRange, int yRange) {
      return getLandPos(creature, xzRange, yRange, creature::getWalkTargetValue);
   }

   /**
    * Generates a random land position for the creature using a custom position evaluator.
    *
    * @param creature       The creature for which to generate the position.
    * @param xzRange        The maximum horizontal distance (X and Z) to consider.
    * @param yRange         The maximum vertical distance (Y) to consider.
    * @param posEvaluator   A function to evaluate the desirability of a position.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getLandPos(Creature creature, int xzRange, int yRange, ToDoubleFunction<BlockPos> posEvaluator) {
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              0,
              null,
              false,
              0.0D,
              posEvaluator,
              true,
              0,
              0,
              true
      );
   }

   /**
    * Generates a random land position for the creature above solid blocks towards a target vector.
    *
    * @param creature         The creature for which to generate the position.
    * @param xzRange          The maximum horizontal distance (X and Z) to consider.
    * @param yRange           The maximum vertical distance (Y) to consider.
    * @param targetVec3       The target vector towards which to generate the position.
    * @param angle            The angle in radians within which to consider positions.
    * @param aboveSolidAmount The maximum number of blocks to move above solid ground.
    * @param aboveSolidOffset The Y offset to add to the position.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getAboveLandPos(Creature creature, int xzRange, int yRange, Vector3d targetVec3, float angle, int aboveSolidAmount, int aboveSolidOffset) {
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              0,
              targetVec3,
              false,
              angle,
              creature::getWalkTargetValue,
              true,
              aboveSolidAmount,
              aboveSolidOffset,
              true
      );
   }

   /**
    * Generates a random land position for the creature towards the specified target vector.
    *
    * @param creature   The creature for which to generate the position.
    * @param xzRange    The maximum horizontal distance (X and Z) to consider.
    * @param yRange     The maximum vertical distance (Y) to consider.
    * @param targetVec3 The target vector towards which to generate the position.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getLandPosTowards(Creature creature, int xzRange, int yRange, Vector3d targetVec3) {
      Vector3d directionVec = targetVec3.subtract(creature.getX(), creature.getY(), creature.getZ());
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              0,
              directionVec,
              false,
              Math.PI / 2F,
              creature::getWalkTargetValue,
              true,
              0,
              0,
              true
      );
   }

   /**
    * Generates a random position for the creature towards the specified target vector.
    *
    * @param creature   The creature for which to generate the position.
    * @param xzRange    The maximum horizontal distance (X and Z) to consider.
    * @param yRange     The maximum vertical distance (Y) to consider.
    * @param targetVec3 The target vector towards which to generate the position.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getPosTowards(Creature creature, int xzRange, int yRange, Vector3d targetVec3) {
      Vector3d directionVec = targetVec3.subtract(creature.getX(), creature.getY(), creature.getZ());
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              0,
              directionVec,
              true,
              Math.PI / 2F,
              creature::getWalkTargetValue,
              false,
              0,
              0,
              true
      );
   }

   /**
    * Generates a random position for the creature towards the specified target vector within a given angle.
    *
    * @param creature   The creature for which to generate the position.
    * @param xzRange    The maximum horizontal distance (X and Z) to consider.
    * @param yRange     The maximum vertical distance (Y) to consider.
    * @param targetVec3 The target vector towards which to generate the position.
    * @param angle      The angle in radians within which to consider positions.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getPosTowards(Creature creature, int xzRange, int yRange, Vector3d targetVec3, double angle) {
      Vector3d directionVec = targetVec3.subtract(creature.getX(), creature.getY(), creature.getZ());
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              0,
              directionVec,
              true,
              angle,
              creature::getWalkTargetValue,
              false,
              0,
              0,
              true
      );
   }

   /**
    * Generates a random air position for the creature towards the specified target vector within a given angle.
    *
    * @param creature   The creature for which to generate the position.
    * @param xzRange    The maximum horizontal distance (X and Z) to consider.
    * @param yRange     The maximum vertical distance (Y) to consider.
    * @param yOffset    The Y offset to add to the random position.
    * @param targetVec3 The target vector towards which to generate the position.
    * @param angle      The angle in radians within which to consider positions.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getAirPosTowards(Creature creature, int xzRange, int yRange, int yOffset, Vector3d targetVec3, double angle) {
      Vector3d directionVec = targetVec3.subtract(creature.getX(), creature.getY(), creature.getZ());
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              yOffset,
              directionVec,
              false,
              angle,
              creature::getWalkTargetValue,
              true,
              0,
              0,
              false
      );
   }

   /**
    * Generates a random position for the creature that is away from the specified position.
    *
    * @param creature      The creature for which to generate the position.
    * @param xzRange       The maximum horizontal distance (X and Z) to consider.
    * @param yRange        The maximum vertical distance (Y) to consider.
    * @param avoidPosition The position to avoid.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getPosAvoid(Creature creature, int xzRange, int yRange, Vector3d avoidPosition) {
      Vector3d directionVec = creature.position().subtract(avoidPosition);
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              0,
              directionVec,
              true,
              Math.PI / 2F,
              creature::getWalkTargetValue,
              false,
              0,
              0,
              true
      );
   }

   /**
    * Generates a random land position for the creature that is away from the specified position.
    *
    * @param creature      The creature for which to generate the position.
    * @param xzRange       The maximum horizontal distance (X and Z) to consider.
    * @param yRange        The maximum vertical distance (Y) to consider.
    * @param avoidPosition The position to avoid.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   public static Vector3d getLandPosAvoid(Creature creature, int xzRange, int yRange, Vector3d avoidPosition) {
      Vector3d directionVec = creature.position().subtract(avoidPosition);
      return generateRandomPos(
              creature,
              xzRange,
              yRange,
              0,
              directionVec,
              false,
              Math.PI / 2F,
              creature::getWalkTargetValue,
              true,
              0,
              0,
              true
      );
   }

   /**
    * Generates a random position for the creature based on various parameters.
    *
    * @param creature           The creature for which to generate the position.
    * @param xzRange            The maximum horizontal distance (X and Z) to consider.
    * @param yRange             The maximum vertical distance (Y) to consider.
    * @param yOffset            The Y offset to add to the random position.
    * @param directionVec3      The direction vector towards which to bias the random position.
    * @param allowWater         Whether the creature can be in water.
    * @param angle              The angle in radians within which to consider positions.
    * @param positionEvaluator  A function to evaluate the desirability of a position.
    * @param canEnterOpenDoors  Whether the creature can enter open doors.
    * @param aboveSolidAmount   The maximum number of blocks to move above solid ground.
    * @param aboveSolidOffset   The Y offset to add when moving above solid ground.
    * @param canSwim            Whether the creature can swim.
    * @return A random position as a Vector3d, or null if none found.
    */
   @Nullable
   private static Vector3d generateRandomPos(
           Creature creature,
           int xzRange,
           int yRange,
           int yOffset,
           @Nullable Vector3d directionVec3,
           boolean allowWater,
           double angle,
           ToDoubleFunction<BlockPos> positionEvaluator,
           boolean canEnterOpenDoors,
           int aboveSolidAmount,
           int aboveSolidOffset,
           boolean canSwim
   ) {
      PathNavigator pathNavigator = creature.getNavigation();
      Random random = creature.getRandom();
      boolean withinRestriction;
      if (creature.hasRestriction()) {
         withinRestriction = creature.getRestrictCenter().closerThan(creature.position(), (double) (creature.getRestrictRadius() + (float) xzRange) + 1.0D);
      } else {
         withinRestriction = false;
      }

      boolean foundPosition = false;
      double highestScore = Double.NEGATIVE_INFINITY;
      BlockPos bestPosition = creature.blockPosition();

      for (int i = 0; i < 10; ++i) {
         BlockPos randomDelta = getRandomDelta(random, xzRange, yRange, yOffset, directionVec3, angle);
         if (randomDelta != null) {
            int x = randomDelta.getX();
            int y = randomDelta.getY();
            int z = randomDelta.getZ();

            if (creature.hasRestriction() && xzRange > 1) {
               BlockPos restrictCenter = creature.getRestrictCenter();
               if (creature.getX() > (double) restrictCenter.getX()) {
                  x -= random.nextInt(xzRange / 2);
               } else {
                  x += random.nextInt(xzRange / 2);
               }

               if (creature.getZ() > (double) restrictCenter.getZ()) {
                  z -= random.nextInt(xzRange / 2);
               } else {
                  z += random.nextInt(xzRange / 2);
               }
            }

            BlockPos newPos = new BlockPos((double) x + creature.getX(), (double) y + creature.getY(), (double) z + creature.getZ());
            if (newPos.getY() >= 0 && newPos.getY() <= creature.level.getMaxBuildHeight() && (!withinRestriction || creature.isWithinRestriction(newPos)) && (!canSwim || pathNavigator.isStableDestination(newPos))) {
               if (canEnterOpenDoors) {
                  newPos = moveUpToAboveSolid(newPos, random.nextInt(aboveSolidAmount + 1) + aboveSolidOffset, creature.level.getMaxBuildHeight(), (blockPos) -> {
                     return creature.level.getBlockState(blockPos).getMaterial().isSolid();
                  });
               }

               if (allowWater || !creature.level.getFluidState(newPos).is(FluidTags.WATER)) {
                  PathNodeType pathNodeType = WalkNodeProcessor.getBlockPathTypeStatic(creature.level, newPos.mutable());
                  if (creature.getPathfindingMalus(pathNodeType) == 0.0F) {
                     double score = positionEvaluator.applyAsDouble(newPos);
                     if (score > highestScore) {
                        highestScore = score;
                        bestPosition = newPos;
                        foundPosition = true;
                     }
                  }
               }
            }
         }
      }

      return foundPosition ? Vector3d.atBottomCenterOf(bestPosition) : null;
   }

   /**
    * Generates a random delta position based on the given parameters.
    *
    * @param random        The Random instance to use.
    * @param xzRange       The maximum horizontal distance (X and Z) to consider.
    * @param yRange        The maximum vertical distance (Y) to consider.
    * @param yOffset       The Y offset to add to the random position.
    * @param directionVec3 The direction vector towards which to bias the random position.
    * @param angle         The angle in radians within which to consider positions.
    * @return A delta BlockPos representing the offset, or null if none found.
    */
   @Nullable
   private static BlockPos getRandomDelta(Random random, int xzRange, int yRange, int yOffset, @Nullable Vector3d directionVec3, double angle) {
      if (directionVec3 != null && !(angle >= Math.PI)) {
         double directionAngle = MathHelper.atan2(directionVec3.z, directionVec3.x) - (double) ((float) Math.PI / 2F);
         double randomAngle = directionAngle + (double) (2.0F * random.nextFloat() - 1.0F) * angle;
         double distance = Math.sqrt(random.nextDouble()) * (double) MathHelper.SQRT_OF_TWO * (double) xzRange;
         double deltaX = -distance * Math.sin(randomAngle);
         double deltaZ = distance * Math.cos(randomAngle);
         if (!(Math.abs(deltaX) > (double) xzRange) && !(Math.abs(deltaZ) > (double) xzRange)) {
            int deltaY = random.nextInt(2 * yRange + 1) - yRange + yOffset;
            return new BlockPos(deltaX, (double) deltaY, deltaZ);
         } else {
            return null;
         }
      } else {
         int deltaX = random.nextInt(2 * xzRange + 1) - xzRange;
         int deltaY = random.nextInt(2 * yRange + 1) - yRange + yOffset;
         int deltaZ = random.nextInt(2 * xzRange + 1) - xzRange;
         return new BlockPos(deltaX, deltaY, deltaZ);
      }
   }

   /**
    * Moves the given position up by a certain amount until it is above solid blocks.
    *
    * @param pos              The starting BlockPos.
    * @param aboveSolidAmount The maximum number of blocks to move above solid ground.
    * @param maxBuildHeight   The maximum build height of the world.
    * @param solidPredicate   A predicate to determine if a block is solid.
    * @return A BlockPos that is above solid ground.
    */
   static BlockPos moveUpToAboveSolid(BlockPos pos, int aboveSolidAmount, int maxBuildHeight, Predicate<BlockPos> solidPredicate) {
      if (aboveSolidAmount < 0) {
         throw new IllegalArgumentException("aboveSolidAmount was " + aboveSolidAmount + ", expected >= 0");
      } else if (!solidPredicate.test(pos)) {
         return pos;
      } else {
         BlockPos abovePos;
         for (abovePos = pos.above(); abovePos.getY() < maxBuildHeight && solidPredicate.test(abovePos); abovePos = abovePos.above()) {
         }

         BlockPos finalPos;
         BlockPos nextPos;
         for (finalPos = abovePos; finalPos.getY() < maxBuildHeight && finalPos.getY() - abovePos.getY() < aboveSolidAmount; finalPos = nextPos) {
            nextPos = finalPos.above();
            if (solidPredicate.test(nextPos)) {
               break;
            }
         }

         return finalPos;
      }
   }
}
