package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.util.UniformInt;
import net.minecraft.util.Unit;
import net.minecraft.world.server.ServerWorld;

public class TryToSniff extends Task<WardenEntity> {
   private static final UniformInt SNIFF_COOLDOWN = UniformInt.of(100, 200);

   public TryToSniff() {
      super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleStatus.VALUE_ABSENT,
              MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleStatus.VALUE_ABSENT));
   }

   protected void start(ServerWorld p_212831_1_, WardenEntity p_212831_2_, long p_212831_3_) {
      Brain<WardenEntity> brain = p_212831_2_.getBrain();
      brain.setMemory(MemoryModuleType.IS_SNIFFING, Unit.INSTANCE);
      brain.setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, SNIFF_COOLDOWN.sample(p_212831_1_.random));
      brain.eraseMemory(MemoryModuleType.WALK_TARGET);
      p_212831_2_.setPose(Pose.SNIFFING);
   }
}