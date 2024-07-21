package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.netherinvasion.NetherInvasion;
import net.minecraft.world.netherinvasion.invader.AbstractNetherInvaderEntity;
import net.minecraft.world.server.ServerWorld;

public class MoveTowardsInvasionTask extends Task<Mob> {
   private final double speedModifier;

   public MoveTowardsInvasionTask(double speed) {
      super(ImmutableMap.of(
              MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT,
              MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT,
              MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT
      ));
      this.speedModifier = speed;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerWorld world, Mob mob) {
      if (mob instanceof AbstractNetherInvaderEntity) {
         AbstractNetherInvaderEntity mob2 = (AbstractNetherInvaderEntity) mob;
         if (mob2.hasActiveRaid() && mob2.getCurrentInvasion() != null) {
            return true;
         }
      }
      return false;
   }

   @Override
   protected void start(ServerWorld world, Mob mob, long gameTime) {
      if (mob instanceof AbstractNetherInvaderEntity) {
         AbstractNetherInvaderEntity mob2 = (AbstractNetherInvaderEntity) mob;
         NetherInvasion invasion = mob2.getCurrentInvasion();
         BlockPos invasionCenter = invasion.getCenter();
         Vector3d targetPos = RandomPositionGenerator.getPosTowards(mob2, 15, 4, Vector3d.atBottomCenterOf(invasionCenter));
         Vector3d target = new Vector3d(invasionCenter.getX(),invasionCenter.getY(),invasionCenter.getZ());

         if (targetPos != null) {
            mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(target, (float) this.speedModifier, 0));
         }
      }
   }
}
