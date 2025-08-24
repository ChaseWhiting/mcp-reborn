package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Creature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class RandomLavaSwimmingGoal extends RandomWalkingGoal {
   public RandomLavaSwimmingGoal(Creature p_i48937_1_, double p_i48937_2_, int p_i48937_4_) {
      super(p_i48937_1_, p_i48937_2_, p_i48937_4_);
   }

   @Nullable
   protected Vector3d getPosition() {
      Vector3d vector3d = RandomPositionGenerator.getPos(this.mob, 10, 7);

      for(int i = 0; vector3d != null && !this.mob.level.getBlockState(new BlockPos(vector3d)).isPathfindable(this.mob.level, new BlockPos(vector3d), PathType.LAVA) && i++ < 10; vector3d = RandomPositionGenerator.getPos(this.mob, 10, 7)) {
      }

      return vector3d;
   }
}